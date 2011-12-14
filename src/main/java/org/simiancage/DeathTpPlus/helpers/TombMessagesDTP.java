package org.simiancage.DeathTpPlus.helpers;

/**
 * PluginName: DeathTpPlus
 * Class: TombMessagesDTP
 * User: DonRedhorse
 * Date: 24.11.11
 * Time: 21:38
 */

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP.DeathEventType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * The TombMessagesDTP Class allows you to write a custom TombMessages file to store all tomb messages for DeathTpPlus.
 *
 * @author Don Redhorse
 */
@SuppressWarnings({"UnusedDeclaration"})
public class TombMessagesDTP {

    /**
     * Instance of the TombMessagesDTP Class
     */
    private static TombMessagesDTP instance = null;

// Nothing to change from here to ==>>>
    /**
     * Object to handle the configuration
     *
     * @see org.bukkit.configuration.file.FileConfiguration
     */
    private FileConfiguration tombMessages;
    /**
     * Object to handle the plugin
     */
    private Plugin plugin;
    /**
     * Configuration File Name
     */
    private static String tombMessageFileName = "tombmessages.yml";

    /**
     * TombMessage File
     */

    private File tombMessageFile;

    /**
     * Is the configuration available or did we have problems?
     */
    private boolean tombMessagesAvailable = false;


    // The org.simiancage.DeathTpPlus.helpers.LoggerDTP should be renamed to the name of the class you did change the original org.simiancage.DeathTpPlus.helpers.LoggerClass too.
    /**
     * Reference of the org.simiancage.DeathTpPlus.helpers.LoggerDTP, needs to be renamed to correct name.
     *
     * @see LoggerDTP
     */
    private static LoggerDTP log;
    private static ConfigDTP config;
    private String pluginName;
    private boolean tombMessagesRequiresUpdate;

    // ToDo Change the tombMessagesCurrent if the tombMessages changes!
    /**
     * This is the internal tombMessages version
     */
    private final String tombMessagesCurrent = "3.2";
    /**
     * This is the DEFAULT for the tombMessages file version, should be the same as tombMessagesCurrent. Will afterwards be changed
     */
    private String tombMessagesVer = "3.2";


// and now the real stuff


// ********************************************************************************************************************

// Helper Variables

    //Todo create stuff for:
    // Blaze, Squid, FallingSand, EnderDragon

    /**
     * Array which holds default fall messages
     */
    private String[] defaultFallMessages;
    /**
     * Array which holds default drowning messages
     */
    private String[] defaultDrowningMessages;
    /**
     * Array which holds default fire messages
     */
    private String[] defaultFireMessages;
    /**
     * Array which holds default fire_tick messages
     */
    private String[] defaultFireTickMessages;
    /**
     * Array which holds default Lava Messages
     */
    private String[] defaultLavaMessages;
    /**
     * Array which holds default creeper messages
     */
    private String[] defaultCreeperMessages;
    /**
     * Array which holds default skeleton messages
     */
    private String[] defaultSkeletonMessages;
    /**
     * Array which holds default spider messages
     */
    private String[] defaultSpiderMessages;
    /**
     * Array which holds default zombie messages
     */
    private String[] defaultZombieMessages;
    /**
     * Array which holds default PVP messages
     */
    private String[] defaultPVPMessages;
    /**
     * Array which holds default block_explosion messages
     */
    private String[] defaultBlockExplosionMessages;
    /**
     * Array which holds default contact messages
     */
    private String[] defaultContactMessages;
    /**
     * Array which holds default ghast messages
     */
    private String[] defaultGhastMessages;
    /**
     * Array which holds default slime messages
     */
    private String[] defaultSlimeMessages;
    /**
     * Array which holds default suffocation messages
     */
    private String[] defaultSuffocationMessages;
    /**
     * Array which holds default pigzombie messages
     */
    private String[] defaultPigzombieMessages;
    /**
     * Array which holds default void messages
     */
    private String[] defaultVoidMessages;
    /**
     * Array which holds default wolf messages
     */
    private String[] defaultWolfMessages;
    /**
     * Array which holds default Lightning messages
     */
    private String[] defaultLightningMessages;
    /**
     * Array which holds default suicide messages
     */
    private String[] defaultSuicideMessages;
    /**
     * Array which holds default unknown messages
     */
    private String[] defaultUnknownMessages;
    /**
     * Array which holds default starvation messages
     */
    private String[] defaultStarvationMessages;
    /**
     * Array which holds default enderman messages
     */
    private String[] defaultEndermanMessages;
    /**
     * Array which holds default CaveSpider messages
     */
    private String[] defaultCaveSpiderMessages;
    /**
     * Array which holds default silverfish messages
     */
    private String[] defaultSilverfishMessages;
    /**
     * Array which holds default entity_explosion messages
     */
    private String[] defaultEntityExplosionMessages;
    /**
     * Array which holds default Giant messages
     */
    private String[] defaultGiantMessages;
    /**
     * Array which holds default Blaze messages
     */
    private String[] defaultBlazeMessages;
    /**
     * Array which holds default Enderdragon messages
     */
    private String[] defaultEnderDragonMessages;
    /**
     * Array which holds default MagmaCube messages
     */
    private String[] defaultMagmaCubeMessages;

    // ToDo add new variables to the top

// Messages  for DeathTpPlus

// Tombs
    /**
     * Must contain at least 1 line. If there are more, it will appear randomly when a person dies.
     * don't exceed 18 characters and it must be UTF-8
     * %a name of player who attacked in pvp deaths
     */
    private HashMap<DeathEventType, List<String>> deathevents = new HashMap<DeathEventType, List<String>>();


// *******************************************************************************************************************


/*  Here comes the custom tombMessages, the default tombMessages is later on in the class
Keep in mind that you need to create your tombMessages file in a way which is
afterwards parsable again from the configuration class of bukkit
*/

// First we have the default part..
// Which is devided in setting up some variables first

    /**
     * Method to setup the tombMessages variables with default values
     */

    private void setupCustomDefaultVariables() {

// Default Death Messages

        /** Creating the default fall messages*/
        defaultFallMessages = new String[]{
                "Tried to fly",
                "Love the ground"
        };
        deathevents.put(DeathEventType.FALL, Arrays.asList(defaultFallMessages));
        /** Creating the default drowning messages*/
        defaultDrowningMessages = new String[]{
                "Has drowned",
                "Love fishing",
                "Drunk the sea"
        };
        deathevents.put(DeathEventType.DROWNING, Arrays.asList(defaultDrowningMessages));
        /** Creating the default fire messages*/
        defaultFireMessages = new String[]{
                "Toasted",
                "Burned",
                "Fire! Fire!"
        };
        deathevents.put(DeathEventType.FIRE, Arrays.asList(defaultFireMessages));
        /** Creating the default fire_tick messages*/
        defaultFireTickMessages = new String[]{
                "Toasted",
                "Burned",
                "Fire! Fire!"
        };
        deathevents.put(DeathEventType.FIRE_TICK, Arrays.asList(defaultFireTickMessages));
        /** Creating the default lava messages*/
        defaultLavaMessages = new String[]{
                "Swim in lava",
                "Now obsidian"
        };
        deathevents.put(DeathEventType.LAVA, Arrays.asList(defaultLavaMessages));
        /** Creating the default creeper messages*/
        defaultCreeperMessages = new String[]{
                "Ksss BOUM",
                "CREEEEPPPPERRR",
                "Creeper Lover"
        };
        deathevents.put(DeathEventType.CREEPER, Arrays.asList(defaultCreeperMessages));
        /** Creating the default skeleton messages*/
        defaultSkeletonMessages = new String[]{
                "Skeleton Bow",
                "Arrow in Head"
        };
        deathevents.put(DeathEventType.SKELETON, Arrays.asList(defaultSkeletonMessages));
        /** Creating the default spider messages*/
        defaultSpiderMessages = new String[]{
                "Spider venom",
                "Arachnophobia"
        };
        deathevents.put(DeathEventType.SPIDER, Arrays.asList(defaultSpiderMessages));
        /** Creating the default zombie messages*/
        defaultZombieMessages = new String[]{
                "L4D",
                "Braaaiiin",
                "Zombie rules"
        };
        deathevents.put(DeathEventType.ZOMBIE, Arrays.asList(defaultZombieMessages));
        /** Creating the default pvp messages*/
        defaultPVPMessages = new String[]{
                "Killed by %a"
        };
        deathevents.put(DeathEventType.PVP, Arrays.asList(defaultPVPMessages));
        /** Creating the default block_explosion messages*/
        defaultBlockExplosionMessages = new String[]{
                "BOUM",
                "TNT",
                "Dynamite"
        };
        deathevents.put(DeathEventType.BLOCK_EXPLOSION, Arrays.asList(defaultBlockExplosionMessages));
        /** Creating the default entity_explosion messages*/
        defaultEntityExplosionMessages = new String[]{
                "BOUM",
                "TNT",
                "Dynamite"
        };
        deathevents.put(DeathEventType.ENTITY_EXPLOSION, Arrays.asList(defaultEntityExplosionMessages));
        /** Creating the default contact messages*/
        defaultContactMessages = new String[]{
                "Cactus",
                "Poked a Cactus",
                "Hug a Cactus"
        };
        deathevents.put(DeathEventType.CONTACT, Arrays.asList(defaultContactMessages));
        /** Creating the default ghast messages*/
        defaultGhastMessages = new String[]{
                "In Nether",
                "Ghast"
        };
        deathevents.put(DeathEventType.GHAST, Arrays.asList(defaultGhastMessages));
        /** Creating the default slime messages*/
        defaultSlimeMessages = new String[]{
                "Splouched",
                "Slime won"
        };
        deathevents.put(DeathEventType.SLIME, Arrays.asList(defaultSlimeMessages));
        /** Creating the default suffocation messages*/
        defaultSuffocationMessages = new String[]{
                "Suffocated",
                "Sand''s breath",
                "Sand worm"
        };
        deathevents.put(DeathEventType.SUFFOCATION, Arrays.asList(defaultSuffocationMessages));
        /** Creating the default pigzombie messages*/
        defaultPigzombieMessages = new String[]{
                "Zombie-Pig"
        };
        deathevents.put(DeathEventType.PIG_ZOMBIE, Arrays.asList(defaultPigzombieMessages));
        /** Creating the default void messages*/
        defaultVoidMessages = new String[]{
                "Became Void",
                "End of World"
        };
        deathevents.put(DeathEventType.VOID, Arrays.asList(defaultVoidMessages));
        /** Creating the default Wolfs messages*/
        defaultWolfMessages = new String[]{
                "Eat by Wolf",
                "Wolf meal",
                "Wolf lunch"
        };
        deathevents.put(DeathEventType.WOLF, Arrays.asList(defaultWolfMessages));
        /** Creating the default lightning messages*/
        defaultLightningMessages = new String[]{
                "By Zeus",
                "By Thor",
                "Lighting"
        };
        /** Creating the default lightning messages*/
        deathevents.put(DeathEventType.LIGHTNING, Arrays.asList(defaultLightningMessages));
        defaultSuicideMessages = new String[]{
                "Bad Aim",
                "Killed Himself"
        };
        /** Creating the default suicide messages*/
        deathevents.put(DeathEventType.SUICIDE, Arrays.asList(defaultSuicideMessages));
        defaultUnknownMessages = new String[]{
                "Unknown"
        };
        /** Creating the default unknown messages*/
        deathevents.put(DeathEventType.UNKNOWN, Arrays.asList(defaultUnknownMessages));
        defaultStarvationMessages = new String[]{
                "Starved",
                "Forgot to eat"
        };
        /** Creating the default starvation messages*/
        deathevents.put(DeathEventType.STARVATION, Arrays.asList(defaultStarvationMessages));
        /** Creating the default enderman messages*/
        defaultEndermanMessages = new String[]{
                "Stolen",
                "EnderManed"
        };
        deathevents.put(DeathEventType.ENDERMAN, Arrays.asList(defaultEndermanMessages));
        /** Creating the default cavespider messages*/
        defaultCaveSpiderMessages = new String[]{
                "Spider venom",
                "Arachnophobia"
        };
        deathevents.put(DeathEventType.CAVE_SPIDER, Arrays.asList(defaultCaveSpiderMessages));
        /** Creating the default silverfish messages*/
        defaultSilverfishMessages = new String[]{
                "Fish Food"
        };
        deathevents.put(DeathEventType.SILVERFISH, Arrays.asList(defaultSilverfishMessages));
        /** Creating the default Giant Messages*/
        defaultGiantMessages = new String[]{
                "stomped",
                "By a GIANT"
        };
        deathevents.put(DeathEventType.GIANT, Arrays.asList(defaultGiantMessages));
        /** Creating the default Blaze Messages*/
        defaultBlazeMessages = new String[]{
                "airburned",
                "blazed"
        };
        deathevents.put(DeathEventType.BLAZE, Arrays.asList(defaultBlazeMessages));
        /** Creating the default EnderDragon Messages*/
        defaultEnderDragonMessages = new String[]{
                "perned",
                "wooshed",
                "By a dragon"
        };
        deathevents.put(DeathEventType.ENDERDRAGON, Arrays.asList(defaultEnderDragonMessages));
        /** Creating the default MagmaCube Messages*/
        defaultMagmaCubeMessages = new String[]{
                "splink",
                "goombad",
                "cubed",
                "By a Cube"
        };
        deathevents.put(DeathEventType.MAGMACUBE, Arrays.asList(defaultMagmaCubeMessages));


        // ToDo add new tomb messages to the top

    }

// And than we add the defaults

    /**
     * Method to add the tombMessages variables to the default configuration
     */

    private void customDefaultConfig() {

// Kill Streak Messages
        // Will be added in loadCustomConfig
// Death Streak Messages
        // Will be added in loadCustomConfig
// Death Messages
        // Will be added in loadCustomConfig


    }


// Than we load it....

    /**
     * Method to load the configuration into the tombMessages variables
     */

    private void loadCustomTombMessages() {

        log.info("Loading tomb messages..");
        deathevents.put(DeathEventType.FALL, tombMessages.getList("fall", Arrays.asList(defaultFallMessages)));
        deathevents.put(DeathEventType.DROWNING, tombMessages.getList("drowning", Arrays.asList(defaultDrowningMessages)));
        deathevents.put(DeathEventType.FIRE, tombMessages.getList("fire", Arrays.asList(defaultFireMessages)));
        deathevents.put(DeathEventType.FIRE_TICK, tombMessages.getList("fire_tick", Arrays.asList(defaultFireTickMessages)));
        deathevents.put(DeathEventType.LAVA, tombMessages.getList("lava", Arrays.asList(defaultLavaMessages)));
        deathevents.put(DeathEventType.CREEPER, tombMessages.getList("creeper", Arrays.asList(defaultCreeperMessages)));
        deathevents.put(DeathEventType.SKELETON, tombMessages.getList("skeleton", Arrays.asList(defaultSkeletonMessages)));
        deathevents.put(DeathEventType.SPIDER, tombMessages.getList("spider", Arrays.asList(defaultSpiderMessages)));
        deathevents.put(DeathEventType.ZOMBIE, tombMessages.getList("zombie", Arrays.asList(defaultZombieMessages)));
        deathevents.put(DeathEventType.PVP, tombMessages.getList("pvp", Arrays.asList(defaultPVPMessages)));
        deathevents.put(DeathEventType.BLOCK_EXPLOSION, tombMessages.getList("block_explosion", Arrays.asList(defaultBlockExplosionMessages)));
        deathevents.put(DeathEventType.ENTITY_EXPLOSION, tombMessages.getList("entity_explosion", Arrays.asList(defaultEntityExplosionMessages)));
        deathevents.put(DeathEventType.CONTACT, tombMessages.getList("contact", Arrays.asList(defaultContactMessages)));
        deathevents.put(DeathEventType.GHAST, tombMessages.getList("ghast", Arrays.asList(defaultGhastMessages)));
        deathevents.put(DeathEventType.SLIME, tombMessages.getList("slime", Arrays.asList(defaultSlimeMessages)));
        deathevents.put(DeathEventType.SUFFOCATION, tombMessages.getList("suffocation", Arrays.asList(defaultSuffocationMessages)));
        deathevents.put(DeathEventType.PIG_ZOMBIE, tombMessages.getList("pigzombie", Arrays.asList(defaultPigzombieMessages)));
        deathevents.put(DeathEventType.VOID, tombMessages.getList("void", Arrays.asList(defaultVoidMessages)));
        deathevents.put(DeathEventType.WOLF, tombMessages.getList("wolf", Arrays.asList(defaultWolfMessages)));
        deathevents.put(DeathEventType.LIGHTNING, tombMessages.getList("lightning", Arrays.asList(defaultLightningMessages)));
        deathevents.put(DeathEventType.SUICIDE, tombMessages.getList("suicide", Arrays.asList(defaultSuicideMessages)));
        deathevents.put(DeathEventType.UNKNOWN, tombMessages.getList("unknown", Arrays.asList(defaultUnknownMessages)));
        deathevents.put(DeathEventType.STARVATION, tombMessages.getList("starvation", Arrays.asList(defaultStarvationMessages)));
        deathevents.put(DeathEventType.ENDERMAN, tombMessages.getList("enderman", Arrays.asList(defaultEndermanMessages)));
        deathevents.put(DeathEventType.CAVE_SPIDER, tombMessages.getList("cavespider", Arrays.asList(defaultCaveSpiderMessages)));
        deathevents.put(DeathEventType.SILVERFISH, tombMessages.getList("silverfish", Arrays.asList(defaultSilverfishMessages)));
        deathevents.put(DeathEventType.GIANT, tombMessages.getList("giant", Arrays.asList(defaultGiantMessages)));
        deathevents.put(DeathEventType.BLAZE, tombMessages.getList("blaze", Arrays.asList(defaultBlazeMessages)));
        deathevents.put(DeathEventType.ENDERDRAGON, tombMessages.getList("enderdragon", Arrays.asList(defaultEnderDragonMessages)));
        deathevents.put(DeathEventType.MAGMACUBE, tombMessages.getList("magmacube", Arrays.asList(defaultMagmaCubeMessages)));

        // ToDo add new deathevents on top
// Debugging

        for (DeathEventType deathEventType : DeathEventType.values()) {
            if ((deathEventType != DeathEventType.MONSTER) && (deathEventType != DeathEventType.PVP_FISTS) && (deathEventType != DeathEventType.PVP_TAMED)) {
                log.debug("deathEventType", deathEventType);
                log.info(deathevents.get(deathEventType).size() + " messages loaded for " + deathEventType);
            }
        }

        log.debug("deathevents", deathevents);

    }

// And than we write it....


    /**
     * Method to write the custom tombMessages variables into the tombMessages file
     *
     * @param stream will be handed over by  writeConfig
     */

    private void writeCustomTombMessages(PrintWriter stream) {
//Start here writing your tombMessages variables into the tombMessages file inkl. all comments

        stream.println("#--------- Messages  for DeathTpPlus");
        stream.println();

        stream.println("#");
        stream.println("#--------- Tombmessages");
        stream.println("# Must contain at least 1 line. If there are more, it will appear randomly when a person dies.");
        stream.println("# don't exceed 18 characters and it must be UTF-8");
        stream.println("# %a name of player who attacked in pvp deaths");
        stream.println("#");
        stream.println("# Make sure you enclose the messages in ' and that you use double '' if you want");
        stream.println("# to have a ' inside the message. Otherwise you will get errors!");
        stream.println("#");

        /*List<String> events = new ArrayList<String>(deathevents.keySet());
        for (String event : events) {
            stream.println(event.toLowerCase() + ":");
            for (String msg : deathevents.get(event)) {
                stream.println("    - '" + msg + "'");
            }
        }*/

        for (DeathEventType deathEventType : DeathEventType.values()) {
            // Workaround for missing Monster, PVP Fists and PVP tamed  Death Messages
            if ((deathEventType != DeathEventType.MONSTER) && (deathEventType != DeathEventType.PVP_FISTS) && (deathEventType != DeathEventType.PVP_TAMED)) {
                stream.println(DeathMessagesDTP.mapTypeToNodeName(deathEventType) + ":");
                log.debug("DeathEventType", DeathMessagesDTP.mapTypeToNodeName(deathEventType));
                for (String msg : deathevents.get(deathEventType)) {
                    log.debug("msg", msg);
                    stream.println("    - '" + msg + "'");
                }
            }
        }


    }


// *******************************************************************************************************

// And now you need to create the getters and setters if needed for your tombMessages variables


// The plugin specific getters start here!


    public HashMap<DeathEventType, List<String>> getDeathevents() {
        return deathevents;
    }

    public String getPvpMessage(String killer) {
        String msg = getMessage(DeathEventType.PVP);
        return msg.replace("%a", killer);
    }

    public String getMessage(DeathEventType death) {
        Random rand = new Random();
        if (deathevents.containsKey(death)) {
            List<String> deaths = deathevents.get(death);
            return deaths.get(rand.nextInt(deaths.size()));
            /*} else if (deathevents.containsKey(DeathEventType.valueOf("UNKNOWN"))) {
           List<String> unknowns = deathevents.get(DeathEventType.UNKNOWN);
           return unknowns.get(rand.nextInt(unknowns.size()));*/
        } else {
            return "Unknown";
        }
    }


// Last change coming up... choosing the right ClassName for the Logger..

    /**
     * Method to get the Instance of the Class, if the class hasn't been initialized yet it will.
     *
     * @return instance of class
     */

    public static TombMessagesDTP getInstance() {
        if (instance == null) {
            instance = new TombMessagesDTP();
        }
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        return instance;
    }

    /**
     * Method to get the Instance of the Class and pass over a different name for the tombMessages file, if the class
     * hasn't been initialized yet it will.
     *
     * @param tombMessageFileName name of the tombMessages file
     *
     * @return instance of class
     */
    public static TombMessagesDTP getInstance(String tombMessageFileName) {
        if (instance == null) {
            instance = new TombMessagesDTP();
        }
        log = LoggerDTP.getLogger();
        TombMessagesDTP.tombMessageFileName = tombMessageFileName;
        config = ConfigDTP.getInstance();
        return instance;
    }


// Well that's it.... at least in this class... thanks for reading...


// NOTHING TO CHANGE NORMALLY BELOW!!!


// *******************************************************************************************************************
// Other Methods no change normally necessary


// The class stuff first


    private TombMessagesDTP() {

    }


// than the getters

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


    /**
     * Method to return the Config File Version
     *
     * @return configVer  Config File Version
     */
    public String tombMessagesVer() {
        return tombMessagesVer;
    }

    public boolean isTombMessagesRequiresUpdate() {
        return tombMessagesRequiresUpdate;
    }

// And the rest

// Setting up the tombMessages

    /**
     * Method to setup the configuration.
     * If the configuration file doesn't exist it will be created by {@link #defaultTombMessages()}
     * After that the configuration is loaded {@link #loadTombMessages()}
     * We than check if an configuration update is necessary {@link #updateNecessary()}
     * and if {@link org.simiancage.DeathTpPlus.helpers.ConfigDTP#isAutoUpdateConfig()} is true we update the configuration {@link #updateTombMessages()}
     * and finally set {@link #tombMessagesAvailable} to true
     *
     * @param plugin references the plugin for this configuration
     *
     * @see #defaultTombMessages()
     * @see #loadTombMessages()
     * @see #updateNecessary()
     * @see #updateTombMessages()
     */

    public void setupTombMessages(Plugin plugin) {

        this.tombMessages = new YamlConfiguration();
        this.plugin = plugin;
// Checking if tombMessages file exists, if not create it
        if (!(new File(plugin.getDataFolder(), tombMessageFileName)).exists()) {
            log.info("Creating default tombmessages file");
            defaultTombMessages();
        }
        tombMessageFile = new File(plugin.getDataFolder(), tombMessageFileName);
        try {
            tombMessages.load(tombMessageFile);
        } catch (IOException e) {
            log.debug("Error loading tombmessages file", e);
        } catch (InvalidConfigurationException e) {
            log.debug("Error in the tombmessages configuration", e);
        }
        // Loading the Defaults all the time do to issues with bukkit configuration class defaults
        setupCustomDefaultVariables();
        customDefaultConfig();
// Loading the tombMessages from file
        loadTombMessages();

// Checking internal tombMessagesCurrent and tombMessages file tombMessagesVer

        updateNecessary();
// If tombMessages file has new options update it if enabled
        if (config.isAutoUpdateConfig()) {
            updateTombMessages();
        }
        tombMessagesAvailable = true;
    }


// Creating the defaults

// Configuring the Default options..

    /**
     * Method to write and create the default configuration.
     * The custom configuration variables are added via #setupCustomDefaultVariables()
     * Than we write the configuration to disk  #writeConfig()
     * Than we get the tombMessages object from disk
     * We are adding the default configuration for the variables and load the
     * defaults for the custom variables  #customDefaultConfig()
     *
     * @see #setupCustomDefaultVariables()
     * @see #customDefaultConfig()
     */

    private void defaultTombMessages() {
        setupCustomDefaultVariables();
        if (!writeTombMessages()) {
            log.info("Using internal Defaults!");
        }
        tombMessageFile = new File(plugin.getDataFolder(), tombMessageFileName);
        log.debug("tombMessageFile", tombMessageFile);
        log.debug("tombMessages", tombMessages);
        try {
            tombMessages.load(tombMessageFile);
        } catch (IOException e) {
            log.warning("Error loading tombmessages file", e);
        } catch (InvalidConfigurationException e) {
            log.warning("Error in the tombmessages configuration", e);
        }
        tombMessages.addDefault("tombMessagesVer", tombMessagesVer);
        customDefaultConfig();
    }


// Loading the configuration

    /**
     * Method for loading the configuration from disk.
     * First we get the tombMessages object from disk, than we
     * read in the standard configuration part.
     * We also log a message if #debugLogEnabled
     * and we produce some debug logs.
     * After that we load the custom configuration part #loadCustomConfig()
     *
     * @see #loadCustomTombMessages()
     */

    private void loadTombMessages() {
        // Starting to update the standard configuration
        tombMessagesVer = tombMessages.getString("tombMessagesVer");
        // Debug OutPut NOW!
        log.debug("tombMessagesCurrent", tombMessagesCurrent);
        log.debug("tombMessagesVer", tombMessagesVer);
        setupCustomDefaultVariables();
        loadCustomTombMessages();

        log.info("Tombmessages v." + tombMessagesVer + " loaded.");
    }


//  Writing the tombMessages file

    /**
     * Method for writing the tombmessages file.
     * First we write the standard configuration part, than we
     * write the custom configuration part via #writeCustomConfig()
     *
     * @return true if writing the tombMessages was successful
     *
     * @see #writeCustomTombMessages(java.io.PrintWriter)
     */

    private boolean writeTombMessages() {
        boolean success = false;
        try {
            PrintWriter stream;
            File folder = plugin.getDataFolder();
            if (folder != null) {
                folder.mkdirs();
            }
            String pluginPath = plugin.getDataFolder() + System.getProperty("file.separator");
            PluginDescriptionFile pdfFile = this.plugin.getDescription();
            String authors = getAuthors();
            pluginName = pdfFile.getName();
            stream = new PrintWriter(pluginPath + tombMessageFileName);
//Let's write our tombMessages ;)
            stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + authors);
            stream.println("#");
            stream.println("# Tombmessage File for " + pluginName + ".");
            stream.println("#");
            stream.println("# TombMessages Version");
            stream.println("tombMessagesVer: '" + tombMessagesVer + "'");
            stream.println();

// Getting the custom tombMessages information from the top of the class
            writeCustomTombMessages(stream);

            stream.println();

            stream.close();

            success = true;

        } catch (FileNotFoundException e) {
            log.warning("Error saving the " + tombMessageFileName + ".");
        }
        log.debug("Default TombMessages written", success);
        return success;
    }


// Checking if the deathMessagesVersions differ

    /**
     * Method to check if the configuration version are different.
     * will set #tombMessagesRequiresUpdate to true if versions are different
     */
    private void updateNecessary() {
        if (tombMessagesVer.equalsIgnoreCase(tombMessagesCurrent)) {
            log.info("Tombmessages are up to date");
        } else {
            log.warning("Tombmessages are not up to date!");
            log.warning("Tombmessages File Version: " + tombMessagesVer);
            log.warning("Internal Tombmessages Version: " + tombMessagesCurrent);
            log.warning("It is suggested to update the tombMessages.yml!");
            tombMessagesRequiresUpdate = true;
        }
    }


// Updating the tombMessages

    /**
     * Method to update the configuration if it is necessary.
     */
    private void updateTombMessages() {
        if (tombMessagesRequiresUpdate) {
            tombMessagesVer = tombMessagesCurrent;
            if (writeTombMessages()) {
                log.info("Tombmessages were updated with new default values.");
                log.info("Please change them to your liking.");
            } else {
                log.warning("Tombmessages file could not be auto updated.");
                log.warning("Please rename " + tombMessageFileName + " and try again.");
            }
        }
    }

// Reloading the tombMessages

    /**
     * Method to reload the configuration.
     *
     * @return msg with the status of the reload
     */

    public String reloadTombMessages() {
        String msg;
        if (tombMessagesAvailable) {
            loadTombMessages();
            log.info("Tombmessages reloaded");
            msg = "Tombmessages were reloaded";
        } else {
            log.severe("Reloading Tombmessages before they exists.");
            log.severe("Flog the developer!");
            msg = "Something terrible terrible did go really really wrong, see console log!";
        }
        return msg;
    }
// Saving the tombMessages


    /**
     * Method to save the tombMessages to file.
     *
     * @return true if the save was successful
     */
    public boolean saveTombMessages() {
        boolean saved = false;
        if (config.isSaveConfig()) {
            saved = writeTombMessages();
        }
        return saved;
    }

}
