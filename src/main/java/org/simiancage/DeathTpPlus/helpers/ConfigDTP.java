package org.simiancage.DeathTpPlus.helpers;
/**
 *
 * PluginName: DeathTpPlus
 * Class: ConfigDTP
 * User: DonRedhorse
 * Date: 10.11.11
 * Time: 14:14
 *
 */

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The ConfigDTP Class allows you to write a custom config file for craftbukkit plugins incl. comments.
 * It allows autoupdating config changes, checking for plugin updates and writing back the configuration.
 * Please note that writing to the config file will overwrite any manual changes.<p>
 * You NEED to fix all ToDos, otherwise the class will NOT work!<p>
 *
 * @author Don Redhorse
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConfigDTP {

	/**
	 * Instance of the Configuration Class
	 */
	private static ConfigDTP instance = null;

// Nothing to change from here to ==>>>
	/**
	 * Object to handle the configuration
	 *
	 * @see org.bukkit.configuration.file.FileConfiguration
	 */
	private FileConfiguration config;
	/**
	 * Object to handle the plugin
	 */
	private static Plugin plugin;
	/**
	 * Configuration File Name
	 */
	private static String configFile = "config.yml";
	/**
	 * Is the configuration available or did we have problems?
	 */
	private boolean configAvailable = false;
// Default plugin configuration
	/**
	 * Enables logging to server console. Warning and Severe will still be logged.
	 */
	private boolean informationLogEnabled = true;
	/**
	 * Enable more logging.. could be messy!
	 */
	private boolean debugLogEnabled = false;
	/**
	 * Check if there is a new version of the plugin out.
	 */
	private boolean checkForUpdate = true;
	/**
	 * AutoUpdate the config file if necessary. This will overwrite any changes outside the configuration parameters!
	 */
	private boolean autoUpdateConfig = false;
	/**
	 * Enable saving of the config file
	 */
	private boolean saveConfig = false;
	/**
	 * Contains the plugin name
	 */
	private String pluginName;
	/**
	 * Contains the plugin version
	 */
	private String pluginVersion;
	/**
	 * Do we require a config update?
	 */
	private boolean configRequiresUpdate = false;

	/**
	 * Is there a different plugin version available?
	 */
	private boolean differentPluginAvailable = false;


// <<<<=== here..


// Stuff with minor changes

	/**
	 * Link to the location of the plugin website
	 */
	@SuppressWarnings({"FieldCanBeLocal"})
	private final String pluginSlug = "http://dev.bukkit.org/server-mods/deathtpplus/";
	/**
	 * Link to the location of the recent version number, the file should be a text with just the number
	 */
	@SuppressWarnings({"FieldCanBeLocal"})
	private final String versionURL = "https://raw.github.com/dredhorse/DeathTpPlus/master/Resources/deathtpplus.ver";


	//ToDo create new link for every version

	/**
	 * Reference of the LoggerDTP class.
	 *
	 * @see LoggerDTP
	 */
	private static LoggerDTP log;

	// ToDo Change the configCurrent if the config changes!
	/**
	 * This is the internal config version
	 */
	private final String configCurrent = "3.4";
	/**
	 * This is the DEFAULT for the config file version, should be the same as configCurrent. Will afterwards be changed
	 */
	private String configVer = "3.4";


// and now the real stuff


// ********************************************************************************************************************


// Default Config Variables start here!

	/**
	 * Flag for CraftIrc
	 */
	private String ircDeathTpTag = "all";
	/**
	 * Date Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html)
	 */
	private String dateFormat = "MM/dd/yyyy";
	/**
	 * Time Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html)
	 */
	private String timeFormat = "hh:mm a";
	/**
	 * Enable Lockette Support
	 */
	private boolean enableLockette = true;
	/**
	 * Enable LWC Support
	 */
	private boolean enableLWC = false;
	/**
	 * Set LWC Protection to public instead of removing it
	 */
	private boolean lwcPublic = false;
	/**
	 * Allow World Travel = yes, no, permissions
	 */
	private String allowWorldTravel = "no";

	/**
	 * Only use AIR to create signs or chests
	 */
	private boolean shouldOnlyUseAirToCreate = false;
	/**
	 * Integrate into Dynmap
	 */
	private boolean integrateIntoDynmap = true;


// DeathTp Features

	/**
	 * Enable DeathTp Features
	 */
	private boolean enableDeathtp = true;
	/**
	 * Show the death messages on the server
	 */
	private boolean showDeathNotify = true;
	/**
	 * Show the death messages on the server console
	 */
	private boolean showDeathNotifyOnConsole = false;
	/**
	 * Disable DeathNotify for certain worlds?
	 */
	private boolean disableDeathNotifyInSpecifiedWorlds = false;
	/**
	 * Configure Worlds which don't receive death notify broadcasts
	 */
	private List<String> disabledDeathNotifyWorlds;
	/**
	 * Show death messages only in the world they happened
	 */
	private boolean showDeathNotifyInDeathWorldOnly = false;
	/**
	 * Allow players to access the deathtp command (Override permissions)
	 */
	private boolean allowDeathtp = false;
	/**
	 * Log the deaths to file
	 */
	private boolean allowDeathLog = true;
	/**
	 * Show kill, death streaks and multi kills
	 */
	private boolean showStreaks = true;
	/**
	 * Window of time (in milliseconds) to count kills towards a multikill
	 */
	private String multiKillTimeWindow = "5000";
	/**
	 * Item ID of the item you must have in your hand to teleport. Will remove 1 of these when the command is given. Leave 0 for free teleports
	 */
	private String chargeItem = "0";
	/**
	 * Show Deathsign (aka Tombstone Signs) upon death
	 */
	private boolean showDeathSign = true;
	/**
	 * Economy costs for deathtp command, leave 0 if you don't want to charge
	 */
	private String deathtpCost = "10";

	/**
	 * Use Displaynames for Broadcast messages
	 * Note: Depending on the characters you are using in the names this can cause NPE's!!
	 */
	private boolean useDisplayNameforBroadcasts = false;

	/**
	 * Use the old teleport to highest block or the new save location feature.
	 * Note: Save location feature will display the location of the death if it doesn't find a save spot
	 * and not teleport the player in this case.
	 */
	private boolean teleportToHighestBlock = true;


// TombStone Features (General)

	/**
	 * Enable TombStone Feature
	 */
	private boolean enableTombStone = true;
	/**
	 * Place a Sign on the TombStone
	 */
	private boolean showTombStoneSign = true;
	/**
	 * Prevent non-Op players from destroying TombStones if true (destroyQuickLoot overrides)
	 */
	private boolean allowTombStoneDestroy = false;
	/**
	 * Show status messages to the player
	 */
	private boolean showTombStoneStatusMessage = true;
	/**
	 * Save TombStone Listing between server reloads
	 */
	private boolean saveTombStoneList = true;
	/**
	 * Stop TombStone creation next to existing chests if true (IF set to true it could allow users to circumvent chest protection.)
	 */
	private boolean allowInterfere = false;
	/**
	 * we normally checks to make sure we aren't trying to create a chest in the void.
	 * If you handle or modify the void with another plugin, you can disable that check here.
	 * This option should be true for most servers.
	 */
	private boolean voidCheck = true;
	/**
	 * If you are not locking your chests with Lockette or LWC but still want them to be
	 * protected against Creeper explosions, or you want your chests to be protected even
	 * after they are unlocked, enable this
	 */
	private boolean creeperProtection = false;
	/**
	 * Each line may be one of any custom text OR:
	 * {name} for player name
	 * {date} for day of death
	 * {time} for time of death (server time)
	 * {reason} for cause of death
	 * REMEMBER: LINES ARE LIMITED TO 15 CHARACTERS, AND DON'T FORGET THE QUOTES!
	 */
	private String[] tombStoneSign = new String[]{"{name}", "RIP", "{date}", "{time}"};

	/**
	 * Keep dropped experience when you die AND use quickloot.
	 */
	private boolean keepExperienceOnQuickLoot = false;

	/**
	 * Keep full experience?
	 */
	private boolean keepFullExperience = false;

// TombStone features (Removal)

	/**
	 * Destroy Tombstone on player quickloot
	 */
	private boolean destroyOnQuickLoot = true;
	/**
	 * Remove TombStone after RemoveTime
	 */
	private boolean removeTombStone = true;
	/**
	 * Remove the Tombstone after x Seconds if removeTombStone is true
	 */
	private String removeTombStoneTime = "3600";
	/**
	 * Immediately remove TombStone once it is empty, overriding all other timeout options
	 * WARNING: THIS IS A PROCESSOR-INTENSIVE OPTION
	 */
	private boolean removeTombStoneWhenEmpty = false;
	/**
	 * Never remove a TombStone unless it is empty
	 * WARNING: THIS IS A PROCESSOR-INTENSIVE OPTION
	 */
	private boolean keepTombStoneUntilEmpty = false;


// TombStone Features (Security)

	/**
	 * Remove security after timeout
	 */
	private boolean removeTombStoneSecurity = false;
	/**
	 * Timeout for Security Removal in seconds
	 */
	private String removeTombStoneSecurityTimeOut = "3600";

// Tomb Features

	/**
	 * Enable the TombDTP feature
	 */
	private boolean enableTomb = true;
	/**
	 * Price for createing a Tomb
	 */
	private String tombCost = "10";
	/**
	 * Amount of Tombs a player can have
	 */
	private int maxTomb = 1;
	/**
	 * Use the TombDTP as a respawn point
	 */
	private boolean useTombAsRespawnPoint = false;
	/**
	 * Keyword used to detect a Tomb
	 */
	private String tombKeyWord = "[Tomb]";
	/**
	 * Number of death before destruction of every tomb of the player
	 * without resetting the counter. If set to 2, every 2 deaths, the tombs are destroyed. (Sign is dropped) 0 = Disabled
	 */
	private int maxDeaths = 0;
	/**
	 * When a TombDTP is destroyed, the respawn point is reset.
	 */
	private boolean resetTombRespawn = false;

	/**
	 * Allow RightClick of Tomb to Teleport to Deathlocation
	 */
	private boolean allowTombAsTeleport = false;

// *******************************************************************************************************************


/*  Here comes the custom config, the default config is later on in the class
Keep in mind that you need to create your config file in a way which is
afterwards parsable again from the configuration class of bukkit
*/

// First we have the default part..
// Which is devided in setting up some variables first

	/**
	 * Method to setup the config variables with default values
	 */

	private void setupCustomDefaultVariables() {

		disabledDeathNotifyWorlds = Arrays.asList(new String[]{"none"});

	}

// And than we add the defaults

	/**
	 * Method to add the config variables to the default configuration
	 */
	private void customDefaultConfig() {

// Default DeathTpPlus Variables
		log.debug("config", config);
		config.addDefault("ircDeathTpTag", ircDeathTpTag);
		config.addDefault("dateFormat", dateFormat);
		config.addDefault("timeFormat", timeFormat);
		config.addDefault("enableLockette", enableLockette);
		config.addDefault("enableLWC", enableLWC);
		config.addDefault("allowWorldTravel", allowWorldTravel);
		config.addDefault("shouldOnlyUseAirToCreate", shouldOnlyUseAirToCreate);
		config.addDefault("integrateIntoDynmap", integrateIntoDynmap);

// DeathTp Features Variables
		config.addDefault("enableDeathtp", enableDeathtp);
		config.addDefault("showDeathNotify", showDeathNotify);
		config.addDefault("disableDeathNotifyInSpecifiedWorlds", disableDeathNotifyInSpecifiedWorlds);
		config.addDefault("showDeathNotifyInDeathWorldOnly", showDeathNotifyInDeathWorldOnly);
		config.addDefault("showDeathNotifyOnConsole", showDeathNotifyOnConsole);
		// disabledDeathNotifyWorlds is configured in load customconfig
		config.addDefault("allowDeathtp", allowDeathtp);
		config.addDefault("allowDeathLog", allowDeathLog);
		config.addDefault("showStreaks", showStreaks);
		config.addDefault("multiKillTimeWindow", multiKillTimeWindow);
		config.addDefault("chargeItem", chargeItem);
		config.addDefault("showDeathSign", showDeathSign);
		config.addDefault("deathtpCost", deathtpCost);
		config.addDefault("lwcPublic", lwcPublic);
		config.addDefault("useDisplayNamesForBroadcast", useDisplayNameforBroadcasts);
		config.addDefault("teleportToHighestBlock", teleportToHighestBlock);
// TombStone Features (General)
		config.addDefault("enableTombStone", enableTombStone);
		config.addDefault("showTombStoneSign", showTombStoneSign);
		config.addDefault("allowTombStoneDestroy", allowTombStoneDestroy);
		config.addDefault("showTombStoneStatusMessage", showTombStoneStatusMessage);
		config.addDefault("saveTombStoneList", saveTombStoneList);
		config.addDefault("allowInterfere", allowInterfere);
		config.addDefault("voidCheck", voidCheck);
		config.addDefault("creeperProtection", creeperProtection);
		config.addDefault("keepExperienceOnQuickLoot", keepExperienceOnQuickLoot);
		config.addDefault("keepFullExperience", keepFullExperience);
// TombStone Features (Removal)
		config.addDefault("destroyOnQuickLoot", destroyOnQuickLoot);
		config.addDefault("removeTombStone", removeTombStone);
		config.addDefault("removeTombStoneTime", removeTombStoneTime);
		config.addDefault("removeTombStoneWhenEmpty", removeTombStoneWhenEmpty);
		config.addDefault("keepTombStoneUntilEmpty", keepTombStoneUntilEmpty);

// TombStone Features (Security)
		config.addDefault("removeTombStoneSecurity", removeTombStoneSecurity);
		config.addDefault("removeTombStoneSecurityTimeOut", removeTombStoneSecurityTimeOut);
// TombDTP Features
		config.addDefault("enableTomb", enableTomb);
		config.addDefault("tombCost", tombCost);
		config.addDefault("maxTomb", maxTomb);
		config.addDefault("useTombAsRespawnPoint", useTombAsRespawnPoint);
		config.addDefault("tombKeyWord", tombKeyWord);
		config.addDefault("maxDeaths", maxDeaths);
		config.addDefault("resetTombRespawn", resetTombRespawn);
		config.addDefault("allowTombAsTeleport", allowTombAsTeleport);

	}


// Than we load it....

	/**
	 * Method to load the configuration into the config variables
	 */

	private void loadCustomConfig() {

// Default DeathTpPlus Variables
		ircDeathTpTag = config.getString("ircDeathTpTag");
		dateFormat = config.getString("dateFormat");
		timeFormat = config.getString("timeFormat");
		enableLockette = config.getBoolean("enableLockette");
		enableLWC = config.getBoolean("enableLWC");
		allowWorldTravel = config.getString("allowWorldTravel");
		tombStoneSign[0] = config.getString("tombStoneSign.Line1", tombStoneSign[0]);
		tombStoneSign[1] = config.getString("tombStoneSign.Line2", tombStoneSign[1]);
		tombStoneSign[2] = config.getString("tombStoneSign.Line3", tombStoneSign[2]);
		tombStoneSign[3] = config.getString("tombStoneSign.Line4", tombStoneSign[3]);
		shouldOnlyUseAirToCreate = config.getBoolean("shouldOnlyUseAirToCreate");
		integrateIntoDynmap = config.getBoolean("integrateIntoDynmap");

// DeathTpPlus Features
		enableDeathtp = config.getBoolean("enableDeathtp");
		showDeathNotify = config.getBoolean("showDeathNotify");
		disableDeathNotifyInSpecifiedWorlds = config.getBoolean("disableDeathNotifyInSpecifiedWorlds");
		disabledDeathNotifyWorlds = (List<String>) (List<?>) config.getList("disabledDeathNotifyWorlds", disabledDeathNotifyWorlds);
		showDeathNotifyInDeathWorldOnly = config.getBoolean("showDeathNotifyInDeathWorldOnly");
		showDeathNotifyOnConsole = config.getBoolean("showDeathNotifyOnConsole");
		allowDeathtp = config.getBoolean("allowDeathtp");
		allowDeathLog = config.getBoolean("allowDeathLog");
		showStreaks = config.getBoolean("showStreaks");
		multiKillTimeWindow = config.getString("multiKillTimeWindow");
		chargeItem = config.getString("chargeItem");
		showDeathSign = config.getBoolean("showDeathSign");
		deathtpCost = config.getString("deathtpCost");
		lwcPublic = config.getBoolean("lwcPublic");
		useDisplayNameforBroadcasts = config.getBoolean("useDisplayNameforBroadcasts");
		teleportToHighestBlock = config.getBoolean("teleportToHighestBlock");
// Tombstone Features (General)
		enableTombStone = config.getBoolean("enableTombStone");
		showTombStoneSign = config.getBoolean("showTombStoneSign");
		allowTombStoneDestroy = config.getBoolean("allowTombStoneDestroy");
		showTombStoneStatusMessage = config.getBoolean("showTombStoneStatusMessage");
		saveTombStoneList = config.getBoolean("saveTombStoneList");
		allowInterfere = config.getBoolean("allowInterfere");
		voidCheck = config.getBoolean("voidCheck");
		creeperProtection = config.getBoolean("creeperProtection");
		keepExperienceOnQuickLoot = config.getBoolean("keepExperienceOnQuickLoot");
		keepFullExperience = config.getBoolean("keepFullExperience");
// Tombstone Features (Removal)
		destroyOnQuickLoot = config.getBoolean("destroyOnQuickLoot");
		removeTombStone = config.getBoolean("removeTombStone");
		removeTombStoneTime = config.getString("removeTombStoneTime");
		removeTombStoneWhenEmpty = config.getBoolean("removeTombStoneWhenEmpty");
		keepTombStoneUntilEmpty = config.getBoolean("keepTombStoneUntilEmpty");

// Tombstone Features (Security)
		removeTombStoneSecurity = config.getBoolean("removeTombStoneSecurity");
		removeTombStoneSecurityTimeOut = config.getString("removeTombStoneSecurityTimeOut");
// TombDTP Features
		enableTomb = config.getBoolean("enableTomb");
		tombCost = config.getString("tombCost");
		maxTomb = config.getInt("maxTomb");
		useTombAsRespawnPoint = config.getBoolean("useTombAsRespawnPoint");
		tombKeyWord = config.getString("tombKeyWord");
		maxDeaths = config.getInt("maxDeaths");
		resetTombRespawn = config.getBoolean("resetTombRespawn");
		allowTombAsTeleport = config.getBoolean("allowTombAsTeleport");


// Debugging

		log.debug("ircDeathTpTag", ircDeathTpTag);
		log.debug("dateFormat", dateFormat);
		log.debug("timeFormat", timeFormat);
		log.debug("enableLockette", enableLockette);
		log.debug("enableLWC", enableLWC);
		log.debug("tombStoneSign", tombStoneSign[0]);
		log.debug("tombStoneSign", tombStoneSign[1]);
		log.debug("tombStoneSign", tombStoneSign[2]);
		log.debug("tombStoneSign", tombStoneSign[3]);
		log.debug("shouldOnlyUseAirToCreate", shouldOnlyUseAirToCreate);
		log.debug("integrateIntoDynmap", integrateIntoDynmap);
		log.debug("allowWordTravel", allowWorldTravel);
		log.debug("enableDeathtp", enableDeathtp);
		log.debug("showDeathNotify", showDeathNotify);
		log.debug("disableDeathNotifyInSpecifiedWorlds", disableDeathNotifyInSpecifiedWorlds);
		log.debug("disabledDeathNotifyWorlds", disabledDeathNotifyWorlds);
		log.debug("showDeathNotifyInDeathWorldOnly", showDeathNotifyInDeathWorldOnly);
		log.debug("showDeathNotifyOnConsole", showDeathNotifyOnConsole);
		log.debug("allowDeathtp", allowDeathtp);
		log.debug("allowDeathLog", allowDeathLog);
		log.debug("showStreaks", showStreaks);
		log.debug("multiKillTimeWindow", multiKillTimeWindow);
		log.debug("chargeItem", chargeItem);
		log.debug("showDeathSign", showDeathSign);
		log.debug("deathtpCost", deathtpCost);
		log.debug("lwcPublic", lwcPublic);
		log.debug("useDisplayNameforBroadcasts", useDisplayNameforBroadcasts);
		log.debug("teleportToHighestBlock", teleportToHighestBlock);
		log.debug("enableTombStone", enableTombStone);
		log.debug("showTombStoneSign", showTombStoneSign);
		log.debug("allowTombStoneDestroy", allowTombStoneDestroy);
		log.debug("showTombStoneStatusMessage", showTombStoneStatusMessage);
		log.debug("saveTombStoneList", saveTombStoneList);
		log.debug("allowInterfere", allowInterfere);
		log.debug("voidCheck", voidCheck);
		log.debug("creeperProtection", creeperProtection);
		log.debug("keepExperienceOnQuickLoot", keepExperienceOnQuickLoot);
		log.debug("keepFullExperience", keepFullExperience);
		log.debug("destroyOnQuickLoot", destroyOnQuickLoot);
		log.debug("removeTombStone", removeTombStone);
		log.debug("removeTombStoneTime", removeTombStoneTime);
		log.debug("removeTombStoneWhenEmpty", removeTombStoneWhenEmpty);
		log.debug("keepTombStoneUntilEmpty", keepTombStoneUntilEmpty);

		log.debug("removeTombStoneSecurity", removeTombStoneSecurity);
		log.debug("removeTombStoneSecurityTimeOut", removeTombStoneSecurityTimeOut);
		log.debug("enableTomb", enableTomb);
		log.debug("tombCost", tombCost);
		log.debug("maxTomb", maxTomb);
		log.debug("useTombAsRespawnPoint", useTombAsRespawnPoint);
		log.debug("tombKeyWord", tombKeyWord);
		log.debug("maxDeaths", maxDeaths);
		log.debug("resetTombRespawn", resetTombRespawn);
		log.debug("allowTombAsTeleport", allowTombAsTeleport);

// and now some working...

		if (shouldOnlyUseAirToCreate) {
			log.warning("shouldOnlyUseAirToCreate is enabled. This can mean that there will be no Deathsigns or Tombstones being created!");
		}

		if (isRemoveTombStoneWhenEmpty()) {
			log.warning("RemoveWhenEmpty is enabled. This is processor intensive!");
		}
		if (isKeepTombStoneUntilEmpty()) {
			log.warning("KeepUntilEmpty is enabled. This is processor intensive!");
		}

		if (getAllowWorldTravel().equalsIgnoreCase("yes") || getAllowWorldTravel().equalsIgnoreCase("no") || getAllowWorldTravel().equalsIgnoreCase("permissions")) {
			log.info("allow-wordtravel is: " + getAllowWorldTravel());
		} else {
			log.warning("Wrong allow-worldtravel value of " + getAllowWorldTravel() + ". Defaulting to NO!");
			allowWorldTravel = "no";
		}

		if (isKeepExperienceOnQuickLoot()) {
			log.info("Keeping Experience on Quickloot is: " + isKeepExperienceOnQuickLoot());
			log.info("Keep in mind it only works if people are quicklooting their TombStone!");
		}
	}

// And than we write it....


	/**
	 * Method to write the custom config variables into the config file
	 *
	 * @param stream will be handed over by  writeConfig
	 */
	private void writeCustomConfig(PrintWriter stream) {

		stream.println("#-------- Plugin Configuration");
		stream.println();
		stream.println("#--------- Default Config Variables start here!");
		stream.println();
		stream.println("# Flag for CraftIrc ");
		stream.println("ircDeathTpTag: '" + ircDeathTpTag + "'");
		stream.println();
		stream.println("# Date Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html) ");
		stream.println("dateFormat: '" + dateFormat + "'");
		stream.println();
		stream.println("# Time Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html) ");
		stream.println("timeFormat: '" + timeFormat + "'");
		stream.println();
		stream.println("# Enable Lockette Support");
		stream.println("enableLockette: " + enableLockette);
		stream.println();
		stream.println("# Enable LWC Support");
		stream.println("enableLWC: " + enableLWC);
		stream.println();
		stream.println("# Set LWC Protection to public instead of removing it");
		stream.println("lwcPublic: " + lwcPublic);
		stream.println();
		stream.println("# Allow World Travel: yes, no, permissions");
		stream.println("allowWorldTravel: '" + allowWorldTravel + "'");
		stream.println();
		stream.println("# Each line may be one of any custom text OR:");
		stream.println("# {name} for player name");
		stream.println("# {date} for day of death");
		stream.println("# {time} for time of death (server time)");
		stream.println("# {reason} for cause of death");
		stream.println("# REMEMBER: LINES ARE LIMITED TO 15 CHARACTERS, AND DON'T FORGET THE QUOTES!");
		stream.println("tombStoneSign:");
		stream.println("   Line1: \"" + tombStoneSign[0] + "\"");
		stream.println("   Line2: \"" + tombStoneSign[1] + "\"");
		stream.println("   Line3: \"" + tombStoneSign[2] + "\"");
		stream.println("   Line4: \"" + tombStoneSign[3] + "\"");
		stream.println();
		stream.println("# Should we only use air to create signs and chests.");
		stream.println("# WARNING: This can mean that NO DeathSigns or TombStones are created.");
		stream.println("#          Use at your own risk!");
		stream.println("shouldOnlyUseAirToCreate: " + shouldOnlyUseAirToCreate);
		stream.println();
		stream.println("# Integrate into DynMap");
		stream.println("integrateIntoDynmap: " + integrateIntoDynmap);
		stream.println();
		stream.println("#--------- DeathTp Features");
		stream.println();
		stream.println("# Enable DeathTp Features");
		stream.println("enableDeathtp: " + enableDeathtp);
		stream.println();
		stream.println("# Show the death messages on the server");
		stream.println("showDeathNotify: " + showDeathNotify);
		stream.println();
		stream.println("# Disable the death messages on specific worlds");
		stream.println("disableDeathNotifyInSpecifiedWorlds: " + disableDeathNotifyInSpecifiedWorlds);
		stream.println();
		stream.println("# Worlds on which death messages are disabled");
		stream.println("disabledDeathNotifyWorlds:");
		for (String msg : disabledDeathNotifyWorlds) {
			stream.println("    - \"" + msg + "\"");
		}
		stream.println();
		stream.println("# Show DeathMessages on console");
		stream.println("showDeathNotifyOnConsole: " + showDeathNotifyOnConsole);
		stream.println();
		stream.println("# Show DeathMessages only in Death World");
		stream.println("showDeathNotifyInDeathWorldOnly: " + showDeathNotifyInDeathWorldOnly);

		stream.println();
		stream.println("# Allow players to access the deathtp command (Override permissions)");
		stream.println("allowDeathtp: " + allowDeathtp);
		stream.println();
		stream.println("# Log the deaths to file");
		stream.println("allowDeathLog: " + allowDeathLog);
		stream.println();
		stream.println("# Show kill, death streaks and multi kill messages");
		stream.println("showStreaks: " + showStreaks);
		stream.println();
		stream.println("# Window of time (in milliseconds) to count kills towards a multikill");
		stream.println("multiKillTimeWindow: \"" + multiKillTimeWindow + "\"");
		stream.println();
		stream.println("# Item ID of the item you must have in your hand to teleport. Will remove 1 of these when the command is given. Leave 0 for free teleports ");
		stream.println("chargeItem: \"" + chargeItem + "\"");
		stream.println();
		stream.println("# Show Deathsign (aka Tombstone Signs) upon death");
		stream.println("showDeathSign: " + showDeathSign);
		stream.println();
		stream.println("# Economy costs for deathtp command, leave 0 if you don't want to charge");
		stream.println("deathtpCost: \"" + deathtpCost + "\"");
		stream.println();
		stream.println("# Use DisplayName for Broadcast Messages");
		stream.println("# Note: Depending on the characters you are using in the names this can cause NPE's!");
		stream.println("useDisplayNameforBroadcasts: " + useDisplayNameforBroadcasts);
		stream.println();
		stream.println("# Use the old teleport to highest block or the new save location feature.");
		stream.println("# Note: Save location feature will display the location of the death if it doesn't find a save spot");
		stream.println("# and not teleport the player in this case.");
		stream.println("teleportToHighestBlock: " + teleportToHighestBlock);
		stream.println();


		stream.println("#--------- TombStone Features (General)");
		stream.println();
		stream.println("# Enable TombStone Feature");
		stream.println("enableTombStone: " + enableTombStone);
		stream.println();
		stream.println("# Place a Sign on the TombStone");
		stream.println("showTombStoneSign: " + showTombStoneSign);
		stream.println();
		stream.println("#  Prevent non-Op players from destroying TombStones if true (destroyQuickLoot overrides)");
		stream.println("allowTombStoneDestroy: " + allowTombStoneDestroy);
		stream.println();
		stream.println("# Show status messages to the player");
		stream.println("showTombStoneStatusMessage: " + showTombStoneStatusMessage);
		stream.println();
		stream.println("# Save TombStone Listing between server reloads");
		stream.println("saveTombStoneList: " + saveTombStoneList);
		stream.println();
		stream.println("# Stop TombStone creation next to existing chests if true (IF set to true it could allow users to circumvent chest protection.)");
		stream.println("allowInterfere: " + allowInterfere);
		stream.println();
		stream.println("# We normally check to make sure we aren't trying to create a chest in the void.");
		stream.println("# If you handle or modify the void with another plugin, you can disable that check here.");
		stream.println("# This option should be true for most servers.");
		stream.println("voidCheck: " + voidCheck);
		stream.println();
		stream.println("# If you are not locking your chests with Lockette or LWC but still want them to be");
		stream.println("# protected against Creeper explosions, or you want your chests to be protected even");
		stream.println("# after they are unlocked, enable this");
		stream.println("creeperProtection: " + creeperProtection);
		stream.println();
		stream.println("# Keep experience when quicklooting (Default is dropped / partial experience, see below)");
		stream.println("keepExperienceOnQuickLoot: " + keepExperienceOnQuickLoot);
		stream.println();
		stream.println("# Keep FULL experience instead of dropped.");
		stream.println("keepFullExperience: " + keepFullExperience);
		stream.println();
		stream.println("#--------- TombStone features (Removal");
		stream.println();
		stream.println("# Destroy Tombstone on player quickloot");
		stream.println("destroyOnQuickLoot: " + destroyOnQuickLoot);
		stream.println();
		stream.println("# Remove TombStone after RemoveTime");
		stream.println("removeTombStone: " + removeTombStone);
		stream.println();
		stream.println("# Remove the Tombstone after x Seconds if removeTombStone is true");
		stream.println("removeTombStoneTime: \"" + removeTombStoneTime + "\"");
		stream.println();
		stream.println("# Immediately remove TombStone once it is empty, overriding all other timeout options");
		stream.println("# WARNING: THIS IS A PROCESSOR-INTENSIVE OPTION");
		stream.println("removeTombStoneWhenEmpty: " + removeTombStoneWhenEmpty);
		stream.println();
		stream.println("# Never remove a TombStone unless it is empty");
		stream.println("# WARNING: THIS IS A PROCESSOR-INTENSIVE OPTION");
		stream.println("keepTombStoneUntilEmpty: " + keepTombStoneUntilEmpty);
		stream.println();
		stream.println("#--------- TombStone Features (Security");
		stream.println();
		stream.println("# Remove security after timeout");
		stream.println("removeTombStoneSecurity: " + removeTombStoneSecurity);
		stream.println();
		stream.println("# Timeout for Security Removal in seconds");
		stream.println("removeTombStoneSecurityTimeOut: \"" + removeTombStoneSecurityTimeOut + "\"");
		stream.println();
		stream.println("#--------- Tomb Features");
		stream.println();
		stream.println("# Enable the Tomb feature");
		stream.println("enableTomb: " + enableTomb);
		stream.println();
		stream.println("# Price for createing a Tomb");
		stream.println("tombCost: \"" + tombCost + "\"");
		stream.println();
		stream.println("# Amount of Tombs a player can have");
		stream.println("maxTomb: " + maxTomb);
		stream.println();
		stream.println("# Use the Tomb as a respawn point");
		stream.println("useTombAsRespawnPoint: " + useTombAsRespawnPoint);
		stream.println();
		stream.println("# Keyword used to detect a Tomb");
		stream.println("tombKeyWord: \"" + tombKeyWord + "\"");
		stream.println();
		stream.println("# Number of death before destruction of every tomb of the player");
		stream.println("# without resetting the counter. If set to 2, every 2 deaths, the tombs are destroyed. (Sign is dropped) 0: = Disabled");
		stream.println("maxDeaths: " + maxDeaths);
		stream.println();
		stream.println("# When a Tomb is destroyed, the respawn point is reset.");
		stream.println("resetTombRespawn: " + resetTombRespawn);
		stream.println();
		stream.println("# When a Tomb right clicked the player is teleported to his deathlocation.");
		stream.println("# Please Note: This only works if DeathTP is also enabled.");
		stream.println("allowTombAsTeleport: " + allowTombAsTeleport);
		stream.println();

	}


// *******************************************************************************************************

// And now you need to create the getters and setters if needed for your config variables


// The plugin specific getters start here!


	public boolean isKeepFullExperience() {
		return keepFullExperience;
	}

	public String getPluginSlug() {
		return pluginSlug;
	}

	public boolean isIntegrateIntoDynmap() {
		return integrateIntoDynmap;
	}

	public boolean isAllowTombAsTeleport() {
		return allowTombAsTeleport;
	}

	public boolean isShowDeathNotifyInDeathWorldOnly() {
		return showDeathNotifyInDeathWorldOnly;
	}

	public boolean isShowDeathNotifyOnConsole() {
		return showDeathNotifyOnConsole;
	}

	public boolean isShouldOnlyUseAirToCreate() {
		return shouldOnlyUseAirToCreate;
	}

	public boolean isTeleportToHighestBlock() {
		return teleportToHighestBlock;
	}

	public boolean isDisableDeathNotifyInSpecifiedWorlds() {
		return disableDeathNotifyInSpecifiedWorlds;
	}

	public boolean isDisabledDeathNotifyWorld(String world) {
		return disabledDeathNotifyWorlds.contains(world);
	}

	public boolean isKeepExperienceOnQuickLoot() {
		return keepExperienceOnQuickLoot;
	}


	public boolean isUseDisplayNameforBroadcasts() {
		return useDisplayNameforBroadcasts;
	}

	public boolean isEnableTomb() {
		return enableTomb;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public String getIrcDeathTpTag() {
		return ircDeathTpTag;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public boolean isEnableLockette() {
		return enableLockette;
	}

	public boolean isEnableLWC() {
		return enableLWC;
	}

	public boolean isLwcPublic() {
		return lwcPublic;
	}

	public String getMultiKillTimeWindow() {
		return multiKillTimeWindow;
	}

	public boolean isEnableDeathtp() {
		return enableDeathtp;
	}

	public boolean isShowDeathNotify() {
		return showDeathNotify;
	}

	public boolean isAllowDeathtp() {
		return allowDeathtp;
	}

	public boolean isAllowDeathLog() {
		return allowDeathLog;
	}

	public boolean isShowStreaks() {
		return showStreaks;
	}

	public String getChargeItem() {
		return chargeItem;
	}

	public boolean isShowDeathSign() {
		return showDeathSign;
	}

	public String getDeathtpCost() {
		return deathtpCost;
	}

	public String getAllowWorldTravel() {
		return allowWorldTravel;
	}

	public boolean isEnableTombStone() {
		return enableTombStone;
	}

	public boolean isShowTombStoneSign() {
		return showTombStoneSign;
	}

	public boolean isAllowTombStoneDestroy() {
		return allowTombStoneDestroy;
	}

	public boolean isShowTombStoneStatusMessage() {
		return showTombStoneStatusMessage;
	}

	public boolean isSaveTombStoneList() {
		return saveTombStoneList;
	}

	public boolean isAllowInterfere() {
		return allowInterfere;
	}

	public boolean isVoidCheck() {
		return voidCheck;
	}

	public boolean isCreeperProtection() {
		return creeperProtection;
	}

	public String[] getTombStoneSign() {
		return tombStoneSign;
	}

	public boolean isDestroyOnQuickLoot() {
		return destroyOnQuickLoot;
	}

	public boolean isRemoveTombStone() {
		return removeTombStone;
	}

	public String getRemoveTombStoneTime() {
		return removeTombStoneTime;
	}

	public boolean isRemoveTombStoneWhenEmpty() {
		return removeTombStoneWhenEmpty;
	}

	public boolean isKeepTombStoneUntilEmpty() {
		return keepTombStoneUntilEmpty;
	}

	public boolean isRemoveTombStoneSecurity() {
		return removeTombStoneSecurity;
	}

	public String getRemoveTombStoneSecurityTimeOut() {
		return removeTombStoneSecurityTimeOut;
	}

	public String getTombCost() {
		return tombCost;
	}

	public int getMaxTomb() {
		return maxTomb;
	}

	public boolean isUseTombAsRespawnPoint() {
		return useTombAsRespawnPoint;
	}

	public String getTombKeyWord() {
		return tombKeyWord;
	}

	public int getMaxDeaths() {
		return maxDeaths;
	}

	public boolean isResetTombRespawn() {
		return resetTombRespawn;
	}


	/**
	 * Method to get the Instance of the Class, if the class hasn't been initialized yet it will.
	 *
	 * @return instance of class
	 */
	public static ConfigDTP getInstance() {
		if (instance == null) {
			instance = new ConfigDTP();
		}
		log = LoggerDTP.getLogger();
		return instance;
	}

	/**
	 * Method to get the Instance of the Class and pass over a different name for the config file, if the class
	 * hasn't been initialized yet it will.
	 *
	 * @param configuratonFile name of the config file
	 * @param plugin
	 *
	 * @return instance of class
	 */
	public static ConfigDTP getInstance(String configuratonFile, Plugin plugin) {
		if (instance == null) {
			instance = new ConfigDTP();
			ConfigDTP.plugin = plugin;
		}
		log = LoggerDTP.getLogger();
		configFile = configuratonFile;
		return instance;
	}


// Well that's it.... at least in this class... thanks for reading...


// NOTHING TO CHANGE NORMALLY BELOW!!!

// ToDo.... NOTHING.. you are DONE!


// *******************************************************************************************************************
// Other Methods no change normally necessary


// The class stuff first


	private ConfigDTP() {

	}


// than the getters


	/**
	 * Method to return the PluginName
	 *
	 * @return PluginName
	 */

	public String pluginName() {
		return pluginName;
	}

	/**
	 * Method to return the PluginVersion
	 *
	 * @return PluginVersion
	 */
	public String pluginVersion() {
		return pluginVersion;
	}

	/**
	 * Method to return the Config File Version
	 *
	 * @return configVer  Config File Version
	 */
	public String getConfigVer() {
		return configVer;
	}

	/**
	 * Method to return if Error Logging is enabled
	 *
	 * @return errorLogEnabled
	 */

	public boolean isInformationLogEnabled() {
		return informationLogEnabled;
	}

	/**
	 * Method to return if Debug Loggin is enabled
	 *
	 * @return debugLogEnabled
	 */
	public boolean isDebugLogEnabled() {
		return debugLogEnabled;
	}

	/**
	 * Method to return if we are checking for updates
	 *
	 * @return checkForUpdate
	 */
	public boolean isCheckForUpdate() {
		return checkForUpdate;
	}

	/**
	 * Method to return if we are AutoUpdating the Config File
	 *
	 * @return autoUpdateConfig
	 */
	public boolean isAutoUpdateConfig() {
		return autoUpdateConfig;
	}

	/**
	 * Method to return if we are saving the config automatically
	 *
	 * @return saveConfig
	 */
	public boolean isSaveConfig() {
		return saveConfig;
	}

	/**
	 * Method to return if we need to update the config
	 *
	 * @return configRequiresUpdate
	 */
	public boolean isConfigRequiresUpdate() {
		return configRequiresUpdate;
	}

	public String getConfigCurrent() {
		return configCurrent;
	}

	public boolean isDifferentPluginAvailable() {
		return differentPluginAvailable;
	}

	/**
	 * Parse the Authors Array into a readable String with ',' and 'and'.
	 * taken from MultiVerse-core https://github.com/Multiverse/Multiverse-Core
	 *
	 * @return
	 */
	public String getAuthors() {
		String authors = "";
		ArrayList<String> auths = plugin.getDescription().getAuthors();
		if (auths.size() == 0) {
			return "";
		}

		if (auths.size() == 1) {
			return auths.get(0);
		}

		for (int i = 0; i < auths.size(); i++) {
			if (i == plugin.getDescription().getAuthors().size() - 1) {
				authors += " and " + plugin.getDescription().getAuthors().get(i);
			} else {
				authors += ", " + plugin.getDescription().getAuthors().get(i);
			}
		}
		return authors.substring(2);
	}

	// And the rest

// Setting up the config

	/**
	 * Method to setup the configuration.
	 * If the configuration file doesn't exist it will be created by {@link #defaultConfig()}
	 * After that the configuration is loaded {@link #loadConfig()}
	 * We than check if an configuration update is necessary {@link #updateNecessary()}
	 * and if {@link #autoUpdateConfig} is true we update the configuration {@link #updateConfig()}
	 * If {@link #checkForUpdate} is true we check if there is a new version of the plugin {@link #versionCheck()}
	 * and set {@link #configAvailable} to true
	 *
	 * @param fileConfiguration
	 * @param plugin            references the plugin for this configuration
	 *
	 * @see #defaultConfig()
	 * @see #loadConfig()
	 * @see #updateNecessary()
	 * @see #updateConfig()
	 * @see #versionCheck()
	 */

	public void setupConfig(FileConfiguration fileConfiguration, Plugin plugin) {

		this.config = fileConfiguration;
		this.plugin = plugin;
// Checking if config file exists, if not create it
		if (!(new File(plugin.getDataFolder(), configFile)).exists()) {
			if (checkForUpdate) {
				PingManager.created();
			}
			log.info("Creating default configuration file");
			defaultConfig();
		}
		config = plugin.getConfig();
		log.debug("config", config);
// Loading the Defaults all the time do to issues with bukkit configuration class defaults
		setupCustomDefaultVariables();
		log.debug("config", config);
		customDefaultConfig();
// Loading the config from file
		loadConfig();

// Checking internal configCurrent and config file configVer

		updateNecessary();
// If config file has new options update it if enabled
		if (autoUpdateConfig) {
			updateConfig();
		}
// Also check for New Version of the plugin
		if (checkForUpdate) {
			versionCheck();
		}
		configAvailable = true;
	}


// Creating the defaults

// Configuring the Default options..

	/**
	 * Method to write and create the default configuration.
	 * The custom configuration variables are added via #setupCustomDefaultVariables()
	 * Than we write the configuration to disk  #writeConfig()
	 * Than we get the config object from disk
	 * We are adding the default configuration for the variables and load the
	 * defaults for the custom variables  #customDefaultConfig()
	 *
	 * @see #setupCustomDefaultVariables()
	 * @see #customDefaultConfig()
	 */

	private void defaultConfig() {
		setupCustomDefaultVariables();
		if (!writeConfig()) {
			log.info("Using internal Defaults!");
		}
		config = plugin.getConfig();
		config.addDefault("configVer", configVer);
		config.addDefault("informationLogEnabled", informationLogEnabled);
		config.addDefault("DebugLogEnabled", debugLogEnabled);
		config.addDefault("checkForUpdate", checkForUpdate);
		config.addDefault("autoUpdateConfig", autoUpdateConfig);
		config.addDefault("saveConfig", saveConfig);
		customDefaultConfig();
	}


// Loading the configuration

	/**
	 * Method for loading the configuration from disk.
	 * First we get the config object from disk, than we
	 * read in the standard configuration part.
	 * We also log a message if #debugLogEnabled
	 * and we produce some debug logs.
	 * After that we load the custom configuration part #loadCustomConfig()
	 *
	 * @see #loadCustomConfig()
	 */

	private void loadConfig() {

		// Starting to update the standard configuration
		configVer = config.getString("configVer");
		// Workaround for old errorLogEnabled
		if (config.contains("errorLogEnabled")) {
			informationLogEnabled = config.getBoolean("errorLogEnabled");
		} else {
			informationLogEnabled = config.getBoolean("informationLogEnabled");
		}
		debugLogEnabled = config.getBoolean("DebugLogEnabled");
		checkForUpdate = config.getBoolean("checkForUpdate");
		autoUpdateConfig = config.getBoolean("autoUpdateConfig");
		saveConfig = config.getBoolean("saveConfig");
		// Debug OutPut NOW!
		if (debugLogEnabled) {
			log.info("Debug Logging is enabled!");
		}
		log.debug("configCurrent", configCurrent);
		log.debug("configVer", configVer);
		log.debug("informationLogEnabled", informationLogEnabled);
		log.debug("checkForUpdate", checkForUpdate);
		log.debug("autoUpdateConfig", autoUpdateConfig);
		log.debug("saveConfig", saveConfig);

		loadCustomConfig();

		log.info("Configuration v." + configVer + " loaded.");
	}


//  Writing the config file

	/**
	 * Method for writing the configuration file.
	 * First we write the standard configuration part, than we
	 * write the custom configuration part via #writeCustomConfig()
	 *
	 * @return true if writing the config was successful
	 *
	 * @see #writeCustomConfig(java.io.PrintWriter)
	 */

	private boolean writeConfig() {
		boolean success = false;
		try {
			PrintWriter stream;
			File folder = plugin.getDataFolder();
			if (folder != null) {
				folder.mkdirs();
			}
			String pluginPath = plugin.getDataFolder() + System.getProperty("file.separator");
			PluginDescriptionFile pdfFile = plugin.getDescription();
			String authors = getAuthors();
			pluginName = pdfFile.getName();
			pluginVersion = pdfFile.getVersion();
			stream = new PrintWriter(pluginPath + configFile);
//Let's write our config ;)
			stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + authors);
			stream.println("#");
			stream.println("# Configuration File for " + pluginName + ".");
			stream.println("#");
			stream.println("# For detailed assistance please visit: " + pluginSlug);
			stream.println();
			stream.println("#------- Default Configuration");
			stream.println();
			stream.println("# Configuration Version");
			stream.println("configVer: \"" + configVer + "\"");
			stream.println();
			stream.println("# Informational Log Enabled");
			stream.println("# Enable logging to server console");
			stream.println("# Gives out some more informational messages.");
			stream.println("informationLogEnabled: " + informationLogEnabled);
			stream.println();
			stream.println("# Debug Log Enabled");
			stream.println("# Enable more logging.. could be messy!");
			stream.println("DebugLogEnabled: " + debugLogEnabled);
			stream.println();
			stream.println("# Check for Update");
			stream.println("# Will check if there is a new version of the plugin out.");
			stream.println("# Please note: This will also track usage of reloads, config creation and updates of this version of the plugin via");
			stream.println("# " + PINGS.BUNDLE.getURL());
			stream.println("# Please disable this feature if you don't like this!");
			stream.println("checkForUpdate: " + checkForUpdate);
			stream.println();
			stream.println("# Auto Update Config");
			stream.println("# This will overwrite any changes outside the configuration parameters!");
			stream.println("autoUpdateConfig: " + autoUpdateConfig);
			stream.println();
			stream.println("# Save Config");
			stream.println("# This will overwrite any changes outside the configuration parameters!");
			stream.println("# Only needed if you use ingame commands to change the configuration.");
			stream.println("saveConfig: " + saveConfig);
			stream.println();

// Getting the custom config information from the top of the class
			writeCustomConfig(stream);

			stream.println();

			stream.close();

			success = true;

		} catch (FileNotFoundException e) {
			log.warning("Error saving the " + configFile + ".");
		}
		log.debug("DefaultConfig written", success);
		return success;
	}


// Checking if the configVersions differ

	/**
	 * Method to check if the configuration version are different.
	 * will set #configRequiresUpdate to true if versions are different
	 */
	private void updateNecessary() {
		if (configVer.equalsIgnoreCase(configCurrent)) {
			log.info("Config is up to date");
		} else {
			log.warning("Config is not up to date!");
			log.warning("Config File Version: " + configVer);
			log.warning("Internal Config Version: " + configCurrent);
			log.warning("It is suggested to update the config.yml!");
			configRequiresUpdate = true;
		}
	}

// Checking the Current Version via the Web

	/**
	 * Method to figure out if we are out of date or running on an older / newer build of CB
	 */

	private boolean versionCheck() {

		differentPluginAvailable = false;
		boolean errorAccessingGithub = false;
		int pluginMajor;
		int pluginMinor;
		int pluginDev;
		int pluginCB;
		int githubMajor;
		int githubMinor;
		int githubDev;
		int githubCB;
		String plVersion;
		String ghVersion;
		String cbVersion = plugin.getServer().getVersion();
		int cbVer;
		if (cbVersion.contains("++")) {
			log.info("You are running a CB ++ Version, everything should be fine.");
			log.info("Except Version checking.");
			cbVer = 0;
		} else {
			int versionPos = cbVersion.indexOf("-b") + 2;
			cbVersion = cbVersion.substring(versionPos, versionPos + 4);
			log.debug("bukkitVersion", cbVersion);
			cbVer = Integer.parseInt(cbVersion);
		}
		String pluginVersion = plugin.getDescription().getVersion();
		log.debug("pluginVersion", pluginVersion);
		plVersion = pluginVersion.replace(".", ":");
		String[] thisVersion = plVersion.split(":");
		pluginMajor = Integer.parseInt(thisVersion[0].toString());
		pluginMinor = Integer.parseInt(thisVersion[1].toString());
		pluginDev = Integer.parseInt(thisVersion[2].toString());
		pluginCB = Integer.parseInt(thisVersion[3].toString());
		URL url;
		String newVersion = "";
		try {
			url = new URL(versionURL);
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			while ((line = in.readLine()) != null) {
				newVersion += line;
			}
			in.close();
		} catch (MalformedURLException ex) {
			log.warning("Error accessing update URL.", ex);
			errorAccessingGithub = true;
		} catch (IOException ex) {
			log.warning("Error checking for update.", ex);
			errorAccessingGithub = true;
		}
		log.debug("newVersion", newVersion);
		if (!errorAccessingGithub) {
			ghVersion = newVersion.replace(".", ":");
			String[] githubVersion = ghVersion.split(":");
			githubMajor = Integer.parseInt(githubVersion[0]);
			githubMinor = Integer.parseInt(githubVersion[1]);
			if (githubVersion.length > 3) {
				githubDev = Integer.parseInt(githubVersion[2]);
				githubCB = Integer.parseInt(githubVersion[3]);
			} else {
				githubDev = 0;
				githubCB = Integer.parseInt(githubVersion[2]);
			}

			if (cbVer < pluginCB) {
				log.warning("You are running on an older CB Version");
				log.warning("There might be issues, just letting you know");
			}
			if (cbVer > pluginCB) {
				if (githubCB >= cbVer) {
					log.warning("There is a new version available for your CB Version");
					log.warning("You might want to update!");
				} else {
					log.warning("You are running on an newer CB Version");
					log.warning("There might be issues, just letting you know");
				}
			}

			if (newVersion.equals(pluginVersion)) {
				log.info("is up to date at version "
						+ pluginVersion + ".");
				return true;
			}

			if (githubCB < pluginCB) {
				log.warning("You are running a testbuild for CB: " + pluginCB);
			}
			if (githubCB > pluginCB) {
				log.info("There is a new Version available for CB: " + githubCB);
			}

			if ((githubMajor < pluginMajor) || githubMinor < pluginMinor || githubDev < pluginDev || pluginDev > 0) {
				log.warning("You are running an dev-build. Be sure you know what you are doing!");
				log.warning("Please report any bugs via issues or tickets!");
				if (githubDev > pluginDev) {
					log.warning("There is a NEWER dev-build available!");
				}
				if (githubDev < pluginDev) {
					log.severe("WOW! Where did you get THIS version from?");
					log.severe("You like living on the edge, do you?");
				}
			}
			if ((githubMajor > pluginMajor) || githubMinor > pluginMinor) {

				log.warning("is out of date!");
				log.warning("This version: " + pluginVersion + "; latest version: " + newVersion + ".");
				plugin.getServer().broadcast("A new Version of DeathTpPlus is available!", "deathtpplus.admin.version");
				differentPluginAvailable = true;
			}
		} else {
			log.info("I have no idea if I'm uptodate, sorry!");
		}
		PingManager.enabled();
		return false;
	}

// Updating the config

	/**
	 * Method to update the configuration if it is necessary.
	 */
	private void updateConfig() {
		if (configRequiresUpdate) {
			if (checkForUpdate) {
				PingManager.update();
			}
			configVer = configCurrent;
			if (writeConfig()) {
				log.info("Configuration was updated with new default values.");
				log.info("Please change them to your liking.");
			} else {
				log.warning("Configuration file could not be auto updated.");
				log.warning("Please rename " + configFile + " and try again.");
			}
		}
	}

// Reloading the config

	/**
	 * Method to reload the configuration.
	 *
	 * @return msg with the status of the reload
	 */

	public String reloadConfig() {
		String msg;
		if (configAvailable) {
			loadConfig();
			log.info("Config reloaded");
			msg = "Config was reloaded";
		} else {
			log.severe("Reloading Config before it exists.");
			log.severe("Flog the developer!");
			msg = "Something terrible terrible did go really really wrong, see console log!";
		}
		return msg;
	}
// Saving the config


	/**
	 * Method to save the config to file.
	 *
	 * @return true if the save was successful
	 */
	public boolean saveConfig() {
		boolean saved = false;
		if (saveConfig) {
			saved = writeConfig();
		}
		return saved;
	}


}
