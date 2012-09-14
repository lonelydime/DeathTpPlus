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
 * Tomb a plugin from Belphemur https://github.com/Belphemur/Tomb
 * Original Copyright (C) of DeathTp 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import java.io.IOException;
import java.util.HashMap;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
import org.simiancage.DeathTpPlus.common.ConfigManager;
import org.simiancage.DeathTpPlus.common.CraftIRCEndPoint;
import org.simiancage.DeathTpPlus.common.DynMapHelper;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.common.Metrics;
import org.simiancage.DeathTpPlus.common.listeners.ServerListener;
import org.simiancage.DeathTpPlus.death.DeathMessages;
import org.simiancage.DeathTpPlus.death.commands.DeathsCommand;
import org.simiancage.DeathTpPlus.death.commands.KillsCommand;
import org.simiancage.DeathTpPlus.death.commands.ReportCommand;
import org.simiancage.DeathTpPlus.death.commands.StreakCommand;
import org.simiancage.DeathTpPlus.death.commands.TopCommand;
import org.simiancage.DeathTpPlus.death.listeners.EntityListener;
import org.simiancage.DeathTpPlus.death.listeners.StreakListener;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecordDao;
import org.simiancage.DeathTpPlus.death.persistence.StreakRecordDao;
import org.simiancage.DeathTpPlus.teleport.commands.DeathTpCommand;
import org.simiancage.DeathTpPlus.teleport.persistence.DeathLocationDao;
import org.simiancage.DeathTpPlus.tomb.TombMessages;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.commands.AdminCommand;
import org.simiancage.DeathTpPlus.tomb.commands.FindCommand;
import org.simiancage.DeathTpPlus.tomb.commands.ListCommand;
import org.simiancage.DeathTpPlus.tomb.commands.ResetCommand;
import org.simiancage.DeathTpPlus.tomb.commands.TimeCommand;
import org.simiancage.DeathTpPlus.tomb.listeners.BlockListener;
import org.simiancage.DeathTpPlus.tomb.listeners.PlayerListener;
import org.simiancage.DeathTpPlus.tomb.workers.TombStoneWorker;
import org.simiancage.DeathTpPlus.tomb.workers.TombWorker;
import org.yi.acru.bukkit.Lockette.Lockette;

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
 * @author DonRedhorse
 * @version 3.8.0.1818, 25.01.2012
 */
public class DeathTpPlus extends JavaPlugin {
	// craftirc
	/**
	 * Field description
	 */
	public static CraftIRCEndPoint craftIRCEndPoint = null;
	/**
	 * Field description
	 */
	private static Server server = null;
	/**
	 * Field description
	 */
	private static DeathLocationDao deathLocationLog;
	/**
	 * Field description
	 */
	private static DeathRecordDao deathLog;
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
	private static StreakRecordDao streakLog;

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
	private ConfigManager config;
	/**
	 * Field description
	 */
	private FileConfiguration configuration;
	/**
	 * Field description
	 */
	private DeathMessages deathMessages;
	/**
	 * Field description
	 */
	private DynMapHelper dynMapHelper;
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
	private DefaultLogger log;
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
	private TombMessages tombMessages;
	/**
	 * Field description
	 */
	private TombStoneHelper tombStoneHelper;
	private WorldGuardPlugin worldGuardPlugin;
	private boolean worldGuardEnabled = false;
	private Plugin spout;
	private boolean spoutEnabled = false;

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 */
	public void onDisable() {
		for (World w : getServer().getWorlds()) {
			tombStoneHelper.saveTombStoneList(w.getName());
		}

		if (config.isEnableTomb()) {
			TombWorker.getInstance().save();
			server.getScheduler().cancelTasks(this);
		}

		deathLocationLog.save();
		deathLog.save();
		streakLog.save();

		log.disableMsg();
	}

	/**
	 * Method description
	 */
	public void onEnable() {
		instance = this;
		log = DefaultLogger.getInstance(this);
		config = ConfigManager.getInstance();
		config.setupConfig(configuration, plugin);
		deathMessages = DeathMessages.getInstance();
		deathMessages.setupDeathMessages(plugin);
		tombMessages = TombMessages.getInstance();
		tombMessages.setupTombMessages(plugin);
		pluginPath = getDataFolder() + System.getProperty("file.separator");
		deathLocationLog = new DeathLocationDao(this);
		deathLog = new DeathRecordDao(this);
		streakLog = new StreakRecordDao(this);
		tombStoneHelper = TombStoneHelper.getInstance();
		pm = this.getServer().getPluginManager();

//      register entityListener
		Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
		Bukkit.getPluginManager().registerEvents(new StreakListener(this), this);

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
			TombWorker.getInstance().setPluginInstance(this);
			TombWorker.getInstance().load();
		}

		// Register Server Listener
		Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);

		// craftirc
		Plugin checkCraftIRC = this.getServer().getPluginManager().getPlugin("CraftIRC");

		if (checkCraftIRC instanceof CraftIRC) {
			try {
				craftIRCEndPoint = new CraftIRCEndPoint((CraftIRC) checkCraftIRC);
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
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new TombStoneWorker(plugin), 0L,
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
			Metrics metrics = new Metrics();

			// adding plotter DeathTpPlus
			metrics.addCustomData(plugin, new Metrics.Plotter() {
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
			metrics.addCustomData(plugin, new Metrics.Plotter() {
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
			metrics.addCustomData(plugin, new Metrics.Plotter() {
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
	 * @return
	 */
	public static Server getBukkitServer() {
		return server;
	}

	public boolean isWorldGuardEnabled() {
		return worldGuardEnabled;
	}

	public WorldGuardPlugin getWorldGuardPlugin() {
		return worldGuardPlugin;
	}

	public boolean isSpoutEnabled() {
		return spoutEnabled;
	}

	public Plugin getSpoutPlugin() {
		return spout;
	}

	//~--- methods ------------------------------------------------------------

	/*
     *    Check if a plugin is loaded/enabled already. Returns the plugin if so,
     *    null otherwise
     */

	/**
	 * Method description
	 * @param p
	 * @return
	 */
	private Plugin checkPlugin(String p) {
		Plugin plugin = pm.getPlugin(p);

		return checkPlugin(plugin);
	}

	/**
	 * Method description
	 * @param plugin
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
	 * @return
	 */
	public boolean isDynmapActive() {
		return dynmapActive;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapActive
	 */
	public void setDynmapActive(boolean dynmapActive) {
		this.dynmapActive = dynmapActive;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public DynmapAPI getDynmapAPI() {
		return dynmapAPI;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapAPI
	 */
	public void setDynmapAPI(DynmapAPI dynmapAPI) {
		this.dynmapAPI = dynmapAPI;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public DynMapHelper getDynMapHelper() {
		return dynMapHelper;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynMapHelper
	 */
	public void setDynMapHelper(DynMapHelper dynMapHelper) {
		this.dynMapHelper = dynMapHelper;
	}

	/**
	 * Method description
	 * @param dynMap
	 */
	public void setDynMap(Plugin dynMap) {
		this.dynmap = dynMap;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public Plugin getDynmap() {
		return dynmap;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isDynmapEnabled() {
		return dynmapEnabled;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapEnabled
	 */
	public void setDynmapEnabled(boolean dynmapEnabled) {
		this.dynmapEnabled = dynmapEnabled;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public MobArenaHandler getMaHandler() {
		return maHandler;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param maHandler
	 */
	public void setMaHandler(MobArenaHandler maHandler) {
		this.maHandler = maHandler;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isMobArenaEnabled() {
		return mobArenaEnabled;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param mobArenaEnabled
	 */
	public void setMobArenaEnabled(boolean mobArenaEnabled) {
		this.mobArenaEnabled = mobArenaEnabled;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public Economy getEconomy() {
		return economy;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param worldTravel
	 */
	public void setWorldTravel(boolean worldTravel) {
		this.worldTravel = worldTravel;
	}

	/**
	 * Method description
	 * @param economyActive
	 */
	public void setEconomyActive(boolean economyActive) {
		this.economyActive = economyActive;
	}

	/**
	 * Method description
	 * @param useVault
	 */
	public void setUseVault(boolean useVault) {
		this.useVault = useVault;
	}

	/**
	 * Method description
	 * @param economy
	 */
	public void setEconomy(Economy economy) {
		this.economy = economy;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isWorldTravel() {
		return worldTravel;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathTpPlus getPlugin() {
		return instance;
	}

	/**
	 * Method description
	 * @return
	 */
	public HashMap<String, EntityDamageEvent> getDeathCause() {
		return deathCause;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isEconomyActive() {
		return economyActive;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathLocationDao getDeathLocationLog() {
		return deathLocationLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathRecordDao getDeathLog() {
		return deathLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public static StreakRecordDao getStreakLog() {
		return streakLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public LWCPlugin getLwcPlugin() {
		return lwcPlugin;
	}

	/**
	 * Method description
	 * @return
	 */
	public Lockette getLockettePlugin() {
		return LockettePlugin;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isUseVault() {
		return useVault;
	}

	/**
	 * Method description
	 * @return
	 */
	public static CraftIRCEndPoint getCraftircHandle() {
		return craftIRCEndPoint;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param lwcPlugin
	 */
	public void setLwcPlugin(LWCPlugin lwcPlugin) {
		this.lwcPlugin = lwcPlugin;
	}

	/**
	 * Method description
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
		getCommand("dtplist").setExecutor(new ListCommand(this));
		getCommand("dtpfind").setExecutor(new FindCommand(this));
		getCommand("dtptime").setExecutor(new TimeCommand(this));
		getCommand("dtpreset").setExecutor(new ResetCommand(this));
		getCommand("dtpadmin").setExecutor(new AdminCommand(this));
		getCommand("deathtp").setExecutor(new DeathTpCommand(this));
		getCommand("deaths").setExecutor(new DeathsCommand(this));
		getCommand("kills").setExecutor(new KillsCommand(this));
		getCommand("streak").setExecutor(new StreakCommand(this));
		getCommand("dtpreport").setExecutor(new ReportCommand(this));
		getCommand("dtptop").setExecutor(new TopCommand(this));
	}

	//~--- get methods --------------------------------------------------------

	/*
			  *    Check whether the player has the given permissions.
			  */

	/**
	 * Method description
	 * @param sender
	 * @param label
	 * @param consoleUse
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
	 * @param sender
	 * @return
	 */
	boolean console(CommandSender sender) {
		return !(sender instanceof Player);
	}

	/**
	 * Method description
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
	 * @param lwcPluginVersion
	 */
	public void setLwcPluginVersion(String lwcPluginVersion) {
		this.lwcPluginVersion = lwcPluginVersion;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isLWC4() {
		if (lwcPluginVersion.startsWith("3")) {
			return false;
		}

		return true;
	}

	public void setWorldGuardEnabled(boolean b) {
		worldGuardEnabled = b;
	}

	public void setWorldGuardPlugin(WorldGuardPlugin worldGuardPlugin) {
		this.worldGuardPlugin = worldGuardPlugin;
	}

	public void setSpoutEnabled(boolean b) {
		spoutEnabled = b;
	}
	
	public void setSpoutPlugin(Plugin spout) {
	    this.spout = spout;
	}
}
