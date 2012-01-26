package org.simiancage.DeathTpPlus;

//~--- non-JDK imports --------------------------------------------------------

/**
 * PluginName: DeathTpPlus
 * Class: DeathTpPlus
 * User: DonRedhorse
 * Date: 18.10.11
 * Time: 22:33
 * based on:
 * DeathTpPlus from lonelydime
 * and material from
 * an updated fork of Furt https://github.com/Furt of
 * Cenopath - A Dead Man's Chest plugin for Bukkit
 * By Jim Drey (Southpaw018) <moof@moofit.com>
 * and material from
 * Tomb a plugin from Belphemur https://github.com/Belphemur/TombDTP
 * Original Copyright (C) of DeathTp 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import com.ensifera.animosity.craftirc.CraftIRC;
import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWCPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.simiancage.DeathTpPlus.commands.*;
import org.simiancage.DeathTpPlus.helpers.*;
import org.simiancage.DeathTpPlus.listeners.*;
import org.simiancage.DeathTpPlus.logs.DeathLocationsLogDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.logs.StreakLogDTP;
import org.simiancage.DeathTpPlus.workers.TombStoneWorkerDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;
import org.yi.acru.bukkit.Lockette.Lockette;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

//Register
//craftirc
// importing commands and listeners
//importing Lockette
// importing LWC

/**
 * Class description
 * <p/>
 * todo   Make sure to change the version
 *
 * @author DonRedhorse
 * @version 3.8.0.1818, 25.01.2012
 */
public class DeathTpPlus extends JavaPlugin {
    // craftirc

    /**
     * Field description
     */
    public static CraftIRC craftircHandle = null;

    /**
     * Field description
     */
    private static Server server = null;

    /**
     * Field description
     */
    private static DeathLocationsLogDTP deathLocationLog;

    /**
     * Field description
     */
    private static DeathLogDTP deathLog;

    /**
     * Field description
     */
    private static DeathTpPlus instance;

    /**
     * Field description
     */
    protected static String pluginPath;

    /**
     * Field description
     */
    protected static PluginManager pm;

    /**
     * Field description
     */
    private static StreakLogDTP streakLog;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    private Lockette LockettePlugin = null;

    /**
     * Field description
     */
    private Economy economy = null;

    /**
     * Field description
     */
    private LWCPlugin lwcPlugin = null;

    // plugin variables

    /**
     * Field description
     */
    private DeathTpPlus plugin = this;

    /**
     * Field description
     */
    private boolean worldTravel = false;

    // Vault

    /**
     * Field description
     */
    private boolean useVault = false;

    /**
     * Field description
     */
    private boolean mobArenaEnabled = false;

    /**
     * Field description
     */
    private boolean economyActive = false;

    // DynMap

    /**
     * Field description
     */
    private boolean dynmapEnabled = false;

    /**
     * Field description
     */
    private boolean dynmapActive = false;

    /**
     * Field description
     */
    protected HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();

    /**
     * Field description
     */
    private ConfigDTP config;

    /**
     * Field description
     */
    private FileConfiguration configuration;

    /**
     * Field description
     */
    private DeathMessagesDTP deathMessages;

    /**
     * Field description
     */
    private DynMapHelperDTP dynMapHelperDTP;

    /**
     * Field description
     */
    private Plugin dynmap;

    /**
     * Field description
     */
    private DynmapAPI dynmapAPI;

    /**
     * Field description
     */
    private LoggerDTP log;

    /**
     * Field description
     */
    private String lwcPluginVersion;

    /**
     * Field description
     */
    private MobArenaHandler maHandler;

    /**
     * Field description
     */
    private TombMessagesDTP tombMessages;

    /**
     * Field description
     */
    private TombStoneHelperDTP tombStoneHelper;

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    public void onDisable() {
        for (World w : getServer().getWorlds()) {
            tombStoneHelper.saveTombStoneList(w.getName());
        }

        if (config.isEnableTomb()) {
            TombWorkerDTP.getInstance().save();
            server.getScheduler().cancelTasks(this);
        }

        log.disableMsg();
    }

    /**
     * Method description
     */
    public void onEnable() {
        instance = this;
        log = LoggerDTP.getInstance(this);
        config = ConfigDTP.getInstance();
        config.setupConfig(configuration, plugin);
        deathMessages = DeathMessagesDTP.getInstance();
        deathMessages.setupDeathMessages(plugin);
        tombMessages = TombMessagesDTP.getInstance();
        tombMessages.setupTombMessages(plugin);
        pluginPath = getDataFolder() + System.getProperty("file.separator");
        deathLocationLog = new DeathLocationsLogDTP(this);
        deathLog = new DeathLogDTP(this);
        streakLog = new StreakLogDTP(this);
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        pm = this.getServer().getPluginManager();

//      Create the pluginmanager pm.
        PluginManager pm = getServer().getPluginManager();

//      register entityListener
        Bukkit.getPluginManager().registerEvents(new EntityListenerDTP(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListenerDTP(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockListenerDTP(this), this);
        Bukkit.getPluginManager().registerEvents(new StreakListenerDTP(this), this);

        // register entityListener for Enable Tombstone or Tomb
        if (config.isEnableTombStone() || config.isEnableTomb()) {
            lwcPlugin = (LWCPlugin) checkPlugin("LWC");
            LockettePlugin = (Lockette) checkPlugin("Lockette");
        }

        // register entityListener for Enable Tomb
        if (config.isEnableTomb()) {

            // ToDo check if this is really needed
            // pm.registerEvent(Event.Type.WORLD_SAVE, worldSaveListener, Priority.Normal, this);
            server = getServer();
            TombWorkerDTP.getInstance().setPluginInstance(this);
            TombWorkerDTP.getInstance().load();
        }

        // Register Server Listener
        Bukkit.getPluginManager().registerEvents(new ServerListenerDTP(this), this);

        // craftirc
        Plugin checkCraftIRC = this.getServer().getPluginManager().getPlugin("CraftIRC");

        if (checkCraftIRC != null) {
            try {
                craftircHandle = (CraftIRC) checkCraftIRC;
                log.info("CraftIRC Support Enabled.");
            } catch (ClassCastException ex) {
                log.warning("Problems with getting CraftIRC", ex);
            }
        }

        // reading in Tomblist
        for (World w : getServer().getWorlds()) {
            tombStoneHelper.loadTombStoneList(w.getName());
        }

        // starting Removal Thread
        if (config.isRemoveTombStoneSecurity() || config.isRemoveTombStone()) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new TombStoneWorkerDTP(plugin), 0L,
                    100L);
        }

        // registering commands
        this.addCommands();
        metrics();

        // print success
        log.enableMsg();
    }

    /**
     * Method description
     */
    private void metrics() {
        try {
            MetricsDTP metrics = new MetricsDTP();

            // adding plotter DeathTpPlus
            metrics.addCustomData(plugin, new MetricsDTP.Plotter() {
                @Override
                public String getColumnName() {
                    return "DeathTP enabled";
                }

                @Override
                public int getValue() {
                    int enabled = 0;

                    if (config.isEnableDeathtp()) {
                        enabled = 1;
                    }

                    return enabled;
                }
            });

            // adding plotter TombStone
            metrics.addCustomData(plugin, new MetricsDTP.Plotter() {
                @Override
                public String getColumnName() {
                    return "TombStone enabled";
                }

                @Override
                public int getValue() {
                    int enabled = 0;

                    if (config.isEnableTombStone()) {
                        enabled = 1;
                    }

                    return enabled;
                }
            });

            // adding plotter Tomb
            metrics.addCustomData(plugin, new MetricsDTP.Plotter() {
                @Override
                public String getColumnName() {
                    return "Tomb enabled";
                }

                @Override
                public int getValue() {
                    int enabled = 0;

                    if (config.isEnableTomb()) {
                        enabled = 1;
                    }

                    return enabled;
                }
            });
            metrics.beginMeasuringPlugin(this);
        } catch (IOException e) {
            log.severe("Problems submitting plugin stats");
        }
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public static Server getBukkitServer() {
        return server;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param file
     */
    private void CreateDefaultFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.warning("Cannot create file " + file.getPath() + "/" + file.getName());
        }
    }

    /*
          *    Check if a plugin is loaded/enabled already. Returns the plugin if so,
          *    null otherwise
          */

    /**
     * Method description
     *
     * @param p
     *
     * @return
     */
    private Plugin checkPlugin(String p) {
        Plugin plugin = pm.getPlugin(p);

        return checkPlugin(plugin);
    }

    /**
     * Method description
     *
     * @param plugin
     *
     * @return
     */
    public Plugin checkPlugin(Plugin plugin) {
        if ((plugin != null) && plugin.isEnabled()) {
            String pluginName = plugin.getDescription().getName();
            String pluginVersion = plugin.getDescription().getVersion();

            log.info("Found " + pluginName + " (v" + pluginVersion + ")");

            if (pluginName.equalsIgnoreCase("LWC")) {
                setLwcPlugin((LWCPlugin) plugin);
                setLwcPluginVersion(pluginVersion);
                log.debug("lwcVersion ", pluginVersion);
            }

            if (pluginName.equalsIgnoreCase("Lockette")) {
                setLockettePlugin((Lockette) plugin);
            }

            if (config.isEnableLockette() && (getLockettePlugin() == null)) {
                log.warning("is configured to use Lockette, but Lockette wasn't found yet!");
                log.warning("Still waiting for Lockette to become active!");
            }

            if (config.isEnableLWC() && (getLwcPlugin() == null)) {
                log.warning("is configured to use LWC, but LWC wasn't found yet!");
                log.warning("Still waiting for LWC to become active!");
            }

            return plugin;
        }

        return null;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public boolean isDynmapActive() {
        return dynmapActive;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param dynmapActive
     */
    public void setDynmapActive(boolean dynmapActive) {
        this.dynmapActive = dynmapActive;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public DynmapAPI getDynmapAPI() {
        return dynmapAPI;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param dynmapAPI
     */
    public void setDynmapAPI(DynmapAPI dynmapAPI) {
        this.dynmapAPI = dynmapAPI;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public DynMapHelperDTP getDynMapHelperDTP() {
        return dynMapHelperDTP;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param dynMapHelperDTP
     */
    public void setDynMapHelperDTP(DynMapHelperDTP dynMapHelperDTP) {
        this.dynMapHelperDTP = dynMapHelperDTP;
    }

    /**
     * Method description
     *
     * @param dynMap
     */
    public void setDynMap(Plugin dynMap) {
        this.dynmap = dynMap;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public Plugin getDynmap() {
        return dynmap;
    }

    /**
     * Method description
     *
     * @return
     */
    public boolean isDynmapEnabled() {
        return dynmapEnabled;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param dynmapEnabled
     */
    public void setDynmapEnabled(boolean dynmapEnabled) {
        this.dynmapEnabled = dynmapEnabled;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public MobArenaHandler getMaHandler() {
        return maHandler;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param maHandler
     */
    public void setMaHandler(MobArenaHandler maHandler) {
        this.maHandler = maHandler;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public boolean isMobArenaEnabled() {
        return mobArenaEnabled;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param mobArenaEnabled
     */
    public void setMobArenaEnabled(boolean mobArenaEnabled) {
        this.mobArenaEnabled = mobArenaEnabled;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public Economy getEconomy() {
        return economy;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param worldTravel
     */
    public void setWorldTravel(boolean worldTravel) {
        this.worldTravel = worldTravel;
    }

    /**
     * Method description
     *
     * @param economyActive
     */
    public void setEconomyActive(boolean economyActive) {
        this.economyActive = economyActive;
    }

    /**
     * Method description
     *
     * @param useVault
     */
    public void setUseVault(boolean useVault) {
        this.useVault = useVault;
    }

    /**
     * Method description
     *
     * @param economy
     */
    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public boolean isWorldTravel() {
        return worldTravel;
    }

    /**
     * Method description
     *
     * @return
     */
    public static DeathTpPlus getPlugin() {
        return instance;
    }

    /**
     * Method description
     *
     * @return
     */
    public HashMap<String, EntityDamageEvent> getDeathCause() {
        return deathCause;
    }

    /**
     * Method description
     *
     * @return
     */
    public boolean isEconomyActive() {
        return economyActive;
    }

    /**
     * Method description
     *
     * @return
     */
    public static DeathLocationsLogDTP getDeathLocationLog() {
        return deathLocationLog;
    }

    /**
     * Method description
     *
     * @return
     */
    public static DeathLogDTP getDeathLog() {
        return deathLog;
    }

    /**
     * Method description
     *
     * @return
     */
    public static StreakLogDTP getStreakLog() {
        return streakLog;
    }

    /**
     * Method description
     *
     * @return
     */
    public LWCPlugin getLwcPlugin() {
        return lwcPlugin;
    }

    /**
     * Method description
     *
     * @return
     */
    public Lockette getLockettePlugin() {
        return LockettePlugin;
    }

    /**
     * Method description
     *
     * @return
     */
    public boolean isUseVault() {
        return useVault;
    }

    /**
     * Method description
     *
     * @return
     */
    public static CraftIRC getCraftircHandle() {
        return craftircHandle;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param lwcPlugin
     */
    public void setLwcPlugin(LWCPlugin lwcPlugin) {
        this.lwcPlugin = lwcPlugin;
    }

    /**
     * Method description
     *
     * @param lockettePlugin
     */
    public void setLockettePlugin(Lockette lockettePlugin) {
        LockettePlugin = lockettePlugin;
    }

    //~--- methods ------------------------------------------------------------

//  Register commands

    /**
     * Method description
     */
    private void addCommands() {
        getCommand("dtplist").setExecutor(new ListCommandDTP(this));
        getCommand("dtpfind").setExecutor(new FindCommandDTP(this));
        getCommand("dtptime").setExecutor(new TimeCommandDTP(this));
        getCommand("dtpreset").setExecutor(new ResetCommandDTP(this));
        getCommand("dtpadmin").setExecutor(new AdminCommandDTP(this));
        getCommand("deathtp").setExecutor(new DeathtpCommandDTP(this));
        getCommand("deaths").setExecutor(new DeathsCommandDTP(this));
        getCommand("kills").setExecutor(new KillsCommandDTP(this));
        getCommand("streak").setExecutor(new StreakCommandDTP(this));
    }

    //~--- get methods --------------------------------------------------------

    /*
          *    Check whether the player has the given permissions.
          */

    /**
     * Method description
     *
     * @param sender
     * @param label
     * @param consoleUse
     *
     * @return
     */
    public boolean hasPerm(CommandSender sender, String label, boolean consoleUse) {
        boolean perm = sender.hasPermission("deathtpplus." + label);

        if (this.console(sender)) {
            if (consoleUse) {
                return true;
            }

            log.warning("This command cannot be used in console.");

            return false;
        } else {
            if (sender.isOp()) {
                return true;
            }

            return perm;
        }
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param sender
     *
     * @return
     */
    boolean console(CommandSender sender) {
        return !(sender instanceof Player);
    }

    /**
     * Method description
     *
     * @param p
     * @param msg
     */
    public void sendMessage(Player p, String msg) {
        if (!config.isShowTombStoneStatusMessage()) {
            return;
        }

        p.sendMessage(msg);
    }

    /**
     * Method description
     *
     * @param p
     * @param msg
     */
    public void sendMessage(CommandSender p, String msg) {
        if (!config.isShowTombStoneStatusMessage()) {
            return;
        }

        p.sendMessage(msg);
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param lwcPluginVersion
     */
    public void setLwcPluginVersion(String lwcPluginVersion) {
        this.lwcPluginVersion = lwcPluginVersion;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public boolean isLWC4() {
        if (lwcPluginVersion.startsWith("3")) {
            return false;
        }

        return true;
    }
}
