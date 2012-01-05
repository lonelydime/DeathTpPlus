package org.simiancage.DeathTpPlus;

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
 * TombDTP a plugin from Belphemur https://github.com/Belphemur/TombDTP
 * Original Copyright (C) of DeathTpPlus 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import com.ensifera.animosity.craftirc.CraftIRC;
import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWCPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
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

//Register
//craftirc
// importing commands and listeners
//importing Lockette
// importing LWC

public class DeathTpPlus extends JavaPlugin {
    // listeners

    private EntityListenerDTP entityListener;

    private BlockListenerDTP blockListener;

    private ServerListenerDTP serverListener;

    private PlayerListenerDTP playerListener;

    private StreakEventsListenerDTP streakListener;

    private static DeathTpPlus instance;

    private ConfigDTP config;
    private LoggerDTP log;
    private DeathMessagesDTP deathMessages;
    private TombMessagesDTP tombMessages;
    private TombStoneHelperDTP tombStoneHelper;

    //plugin variables

    private DeathTpPlus plugin = this;
    private static DeathLocationsLogDTP deathLocationLog;
    private static DeathLogDTP deathLog;
    private static StreakLogDTP streakLog;
    protected static String pluginPath;
    protected static PluginManager pm;
    private boolean worldTravel = false;
    private FileConfiguration configuration;
    private LWCPlugin lwcPlugin = null;
    private String lwcPluginVersion;
    private Lockette LockettePlugin = null;
    protected HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();
    private boolean economyActive = false;
    private static Server server = null;
    private boolean mobArenaEnabled = false;
    private MobArenaHandler maHandler;
    private DynMapHelperDTP dynMapHelperDTP;

    // Vault
    private boolean useVault = false;
    private Economy economy = null;

    //craftirc
    public static CraftIRC craftircHandle = null;

    //DynMap
    private boolean dynmapEnabled = false;
    private Plugin dynmap;
    private DynmapAPI dynmapAPI;
    private boolean dynmapActive = false;

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

    public void onEnable() {
        instance = this;
        log = LoggerDTP.getInstance(this);
        config = ConfigDTP.getInstance();
        config.setupConfig(configuration, plugin);
        deathMessages = DeathMessagesDTP.getInstance();
        deathMessages.setupDeathMessages(plugin);
        tombMessages = TombMessagesDTP.getInstance();
        tombMessages.setupTombMessages(plugin);
        entityListener = new EntityListenerDTP(this);
        blockListener = new BlockListenerDTP(this);
        serverListener = new ServerListenerDTP(this);
        playerListener = new PlayerListenerDTP(this);
        streakListener = new StreakListenerDTP(this);
        pluginPath = getDataFolder() + System.getProperty("file.separator");
        deathLocationLog = new DeathLocationsLogDTP(this);
        deathLog = new DeathLogDTP(this);
        streakLog = new StreakLogDTP(this);
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        pm = this.getServer().getPluginManager();

//Create the pluginmanager pm.
        PluginManager pm = getServer().getPluginManager();

// register entityListener
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        // register entityListener for Deathnotify
        if (config.isShowDeathNotify()) {
            pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        }
        // register entityListener for Deathnotify , Show Streaks  or Tomb
        if (config.isShowDeathNotify() || config.isShowStreaks() || config.isEnableTomb()) {
            pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
            pm.registerEvent(Type.CUSTOM_EVENT, streakListener, Priority.Normal, this);
        }
        //register entityListener for Enable Tombstone or Tomb
        if (config.isEnableTombStone() || config.isEnableTomb()) {
            pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
            lwcPlugin = (LWCPlugin) checkPlugin("LWC");
            LockettePlugin = (Lockette) checkPlugin("Lockette");
        }
        // register entityListener for Enable Tomb
        if (config.isEnableTomb()) {
            pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Highest, this);
            pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
            //ToDo check if this is really needed
            //pm.registerEvent(Event.Type.WORLD_SAVE, worldSaveListener, Priority.Normal, this);
            server = getServer();
            TombWorkerDTP.getInstance().setPluginInstance(this);
            TombWorkerDTP.getInstance().load();
        }

        //Register Server Listener
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new ServerListenerDTP(this), Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new ServerListenerDTP(this), Priority.Monitor, this);


        //craftirc
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
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new TombStoneWorkerDTP(plugin), 0L, 100L);
        }

        // registering commands

        this.addCommands();

/*        // ToDo remove permission compability in 3.2
        Permission deathtp = new Permission("deathtp");
        Permission kills = new Permission("kills");
        Permission streak = new Permission("streak");
        Permission deaths = new Permission("deaths");
        this.getServer().getPluginManager().addPermission(deathtp);
        this.getServer().getPluginManager().addPermission(kills);
        this.getServer().getPluginManager().addPermission(streak);
        this.getServer().getPluginManager().addPermission(deaths);*/

        // print success
        log.enableMsg();
    }

    public static Server getBukkitServer() {
        return server;
    }

    private void CreateDefaultFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.warning("Cannot create file " + file.getPath() + "/" + file.getName());
        }
    }

    /*
    * Check if a plugin is loaded/enabled already. Returns the plugin if so,
    * null otherwise
    */
    private Plugin checkPlugin(String p) {
        Plugin plugin = pm.getPlugin(p);
        return checkPlugin(plugin);
    }

    public Plugin checkPlugin(Plugin plugin) {
        if (plugin != null && plugin.isEnabled()) {
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

    public boolean isDynmapActive() {
        return dynmapActive;
    }

    public void setDynmapActive(boolean dynmapActive) {
        this.dynmapActive = dynmapActive;
    }

    public DynmapAPI getDynmapAPI() {
        return dynmapAPI;
    }

    public void setDynmapAPI(DynmapAPI dynmapAPI) {
        this.dynmapAPI = dynmapAPI;
    }

    public DynMapHelperDTP getDynMapHelperDTP() {
        return dynMapHelperDTP;
    }

    public void setDynMapHelperDTP(DynMapHelperDTP dynMapHelperDTP) {
        this.dynMapHelperDTP = dynMapHelperDTP;
    }


    public void setDynMap(Plugin dynMap) {
        this.dynmap = dynMap;
    }

    public Plugin getDynmap() {
        return dynmap;
    }

    public boolean isDynmapEnabled() {
        return dynmapEnabled;
    }

    public void setDynmapEnabled(boolean dynmapEnabled) {
        this.dynmapEnabled = dynmapEnabled;
    }

    public MobArenaHandler getMaHandler() {
        return maHandler;
    }

    public void setMaHandler(MobArenaHandler maHandler) {
        this.maHandler = maHandler;
    }

    public boolean isMobArenaEnabled() {
        return mobArenaEnabled;
    }

    public void setMobArenaEnabled(boolean mobArenaEnabled) {
        this.mobArenaEnabled = mobArenaEnabled;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setWorldTravel(boolean worldTravel) {
        this.worldTravel = worldTravel;
    }

    public void setEconomyActive(boolean economyActive) {
        this.economyActive = economyActive;
    }

    public void setUseVault(boolean useVault) {
        this.useVault = useVault;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public boolean isWorldTravel() {
        return worldTravel;
    }

    public static DeathTpPlus getPlugin() {
        return instance;
    }


    public HashMap<String, EntityDamageEvent> getDeathCause() {
        return deathCause;
    }

    public boolean isEconomyActive() {
        return economyActive;
    }

    public static DeathLocationsLogDTP getDeathLocationLog() {
        return deathLocationLog;
    }

    public static DeathLogDTP getDeathLog() {
        return deathLog;
    }

    public static StreakLogDTP getStreakLog() {
        return streakLog;
    }

    public LWCPlugin getLwcPlugin() {
        return lwcPlugin;
    }

    public Lockette getLockettePlugin() {
        return LockettePlugin;
    }

    public boolean isUseVault() {
        return useVault;
    }

    public static CraftIRC getCraftircHandle() {
        return craftircHandle;
    }

    public void setLwcPlugin(LWCPlugin lwcPlugin) {
        this.lwcPlugin = lwcPlugin;
    }

    public void setLockettePlugin(Lockette lockettePlugin) {
        LockettePlugin = lockettePlugin;
    }


// Register commands

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


    /*
    * Check whether the player has the given permissions.
    */
    public boolean hasPerm(CommandSender sender, String label,
                           boolean consoleUse) {
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

    boolean console(CommandSender sender) {
        return !(sender instanceof Player);
    }

    public void sendMessage(Player p, String msg) {
        if (!config.isShowTombStoneStatusMessage()) {
            return;
        }
        p.sendMessage(msg);
    }

    public void sendMessage(CommandSender p, String msg) {
        if (!config.isShowTombStoneStatusMessage()) {
            return;
        }
        p.sendMessage(msg);
    }

    public void setLwcPluginVersion(String lwcPluginVersion) {
        this.lwcPluginVersion = lwcPluginVersion;
    }

    public boolean isLWC4() {
        if (lwcPluginVersion.startsWith("3")) {
            return false;
        }
        return true;
    }
}
