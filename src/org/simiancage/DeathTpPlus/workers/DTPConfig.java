package org.simiancage.DeathTpPlus.workers;
/**
 *
 * PluginName: DeathTpPlus
 * Class: DTPConfig
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
import java.util.*;


/**
 * The DTPConfig Class allows you to write a custom config file for craftbukkit plugins incl. comments.
 * It allows autoupdating config changes, checking for plugin updates and writing back the configuration.
 * Please note that writing to the config file will overwrite any manual changes.<p>
 * You NEED to fix all ToDos, otherwise the class will NOT work!<p>
 *
 * @author Don Redhorse
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DTPConfig {

    /**
     * Instance of the Configuration Class
     */
    private static DTPConfig instance = null;

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
    private boolean errorLogEnabled = true;
    /**
     * Enable more logging.. could be messy!
     */
    private boolean debugLogEnabled = true;
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
    /**
     * Reference of the DTPLogger class.
     *
     * @see DTPLogger
     */
    private static DTPLogger log;

    // ToDo Change the configCurrent if the config changes!
    /**
     * This is the internal config version
     */
    private final String configCurrent = "3.0";
    /**
     * This is the DEFAULT for the config file version, should be the same as configCurrent. Will afterwards be changed
     */
    private String configVer = "3.0";


// and now the real stuff


// ********************************************************************************************************************

// Helper Variables

    /** Array which holds default Death Streak messages*/
    private String[] defaultDeathStreaks;
    /** Array which holds default Kill Streak messages*/
    private String[] defaultKillStreaks;
    /** Array which holds default fall messages*/
    private String[] defaultFallMessages;
    /** Array which holds default drowning messages*/
    private String[] defaultDrowningMessages;
    /** Array which holds default fire messages*/
    private String[] defaultFireMessages;
    /** Array which holds default fire_tick messages*/
    private String[] defaultFireTickMessages;
    /** Array which holds default Lava Messages*/
    private String[] defaultLavaMessages;
    /** Array which holds default creeper messages*/
    private String[] defaultCreeperMessages;
    /** Array which holds default skeleton messages*/
    private String[] defaultSkeletonMessages;
    /** Array which holds default spider messages*/
    private String[] defaultSpiderMessages;
    /** Array which holds default zombie messages*/
    private String[] defaultZombieMessages;
    /** Array which holds default PVP messages*/
    private String[] defaultPVPMessages;
    /** Array which holds default PVP-Fist messages*/
    private String[] defaultPVPFistMessages;
    /** Array which holds default block_explosion messages*/
    private String[] defaultBlockExplosionMessages;
    /** Array which holds default contact messages*/
    private String[] defaultContactMessages;
    /** Array which holds default ghast messages*/
    private String[] defaultGhastMessages;
    /** Array which holds default slime messages*/
    private String[] defaultSlimeMessages;
    /** Array which holds default suffocation messages*/
    private String[] defaultSuffocationMessages;
    /** Array which holds default pigzombie messages*/
    private String[] defaultPigzombieMessages;
    /** Array which holds default void messages*/
    private String[] defaultVoidMessages;
    /** Array which holds default wolf messages*/
    private String[] defaultWolfMessages;
    /** Array which holds default Lightning messages*/
    private String[] defaultLightningMessages;
    /** Array which holds default suicide messages*/
    private String[] defaultSuicideMessages;
    /** Array which holds default unknown messages*/
    private String[] defaultUnknownMessages;
    /** Array which holds default starvation messages*/
    private String[] defaultStarvationMessages;
    /** Array which holds default enderman messages*/
    private String[] defaultEndermanMessages;
    /** Array which holds default CaveSpider messages*/
    private String[] defaultCaveSpiderMessages;
    /** Array which holds default silverfish messages*/
    private String[] defaultSilverfishMessages;


// Default Config Variables start here!

    /** Flag for CraftIrc */
    private String ircDeathTpTag = "all";
    /** Date Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html) */
    private String dateFormat = "MM/dd/yyyy";
    /** Time Format (For formatting, see http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html) */
    private String timeFormat = "hh:mm a";
    /** Enable Lockette Support*/
    private boolean enableLockette = true;
    /** Enable LWC Support*/
    private boolean enableLWC = true;
    /** Set LWC Protection to public instead of removing it*/
    private boolean lwcPublic = false;
    /** Locale Version for translation features*/
    private String locale = "English";
    /** Allow World Travel = yes, no, permissions*/
    private String allowWorldTravel = "no";


// DeathTp Features

    /** Enable DeathTp Features*/
    private boolean enableDeathtp = true;
    /** Show the death messages on the server*/
    private boolean showDeathNotify = true;
    /** Allow players to access the deathtp command (Override permissions)*/
    private boolean allowDeathtp = false;
    /** Log the deaths to file*/
    private boolean allowDeathLog = true;
    /**Show kill or death streaks read in from killstreak.txt and deathstrek.txt*/
    private boolean showStreaks = true;
    /** Item ID of the item you must have in your hand to teleport. Will remove 1 of these when the command is given. Leave 0 for free teleports */
    private String chargeItem ="0";
    /** Show Deathsign (aka Tombstone Signs) upon death*/
    private boolean showDeathSign = true;
    /** Economy costs for deathtp command, leave 0 if you don't want to charge*/
    private String deathtpCost = "10";


// TombStone Features (General)

    /** Enable TombStone Feature*/
    private boolean enableTombStone = true;
    /** Place a Sign on the TombStone*/
    private boolean showTombStoneSign = true;
    /**  Prevent non-Op players from destroying TombStones if true (destroyQuickLoot overrides)*/
    private boolean allowTombStoneDestroy = false;
    /** Show status messages to the player*/
    private boolean showTombStoneStatusMessage = true;
    /** Save TombStone Listing between server reloads*/
    private boolean saveTombStoneList = true;
    /** Stop TombStone creation next to existing chests if true (IF set to true it could allow users to circumvent chest protection.)*/
    private boolean allowInterfere = false;
    /**
     *  we normally checks to make sure we aren't trying to create a chest in the void.
     *  If you handle or modify the void with another plugin, you can disable that check here.
     *  This option should be true for most servers.
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
    private String[] tombStoneSign = new String[] { "{name}", "RIP", "{date}","{time}" };

// TombStone features (Removal)

    /** Destroy Tombstone on player quickloot*/
    private boolean destroyOnQuickLoot = true;
    /** Remove TombStone after RemoveTime*/
    private boolean removeTombStone = true;
    /** Remove the Tombstone after x Seconds if removeTombStone is true*/
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

    /** Remove security after timeout*/
    private boolean removeTombStoneSecurity = false;
    /** Timeout for Security Removal in seconds*/
    private String removeTombStoneSecurityTimeOut = "3600";

// Tomb Features

    /** Enable the Tomb feature*/
    private boolean enableTomb = true;
    /** Price for createing a Tomb*/
    private String tombCost = "10";
    /** Amount of Tombs a player can have*/
    private int maxTomb = 1;
    /** Use the Tomb as a respawn point*/
    private boolean useTombAsRespawnPoint = false;
    /** Keyword used to detect a Tobmb*/
    private String tombKeyWord= "[Tomb]";
    /**
     *  Number of death before destruction of every tomb of the player
     *  without resetting the counter. If set to 2, every 2 deaths, the tombs are destroyed. (Sign is dropped) 0 = Disabled
     */
    private int maxDeaths = 0;
    /** When a Tomb is destroyed, the respawn point is reset.*/
    private boolean resetTombRespawn = false;

// Messages  for DeathTpPlus


// Streaks

    /**
     *  Kill Streak Messages
     *  format <#of kills> <text to display> %n = player getting the message (in this case, the one on a killstreak).
     */
    private HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    /**
     *  Death Streak Messages
     *  format <#of kills> <text to display> %n = player getting the message (in this case, the one on a deathstreak).
     */
    private HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();

// Deathmessages
    /**
     * Must contain at least 1 line. If there are more, it will appear randomly when a person dies.
     *  %n for player who died
     *  %a name of player who attacked in pvp deaths
     *  %i for item a player was using to kill someone else
     *
     *  Colors
     *
     *  &0 Black
     *  &1 Navy
     *  &2 Green
     *  &3 Blue
     *  &4 Red
     *  &5 Purple
     *  &6 Gold
     *  &7 LightGray
     *  &8 Gray
     *  &9 DarkPurple
     *  &a LightGreen
     *  &b LightBlue
     *  &c Rose
     *  &d LightPurple
     *  &e Yellow
     *  &f White
     */
    private HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();

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

// Default Streak Messages

        /** Creating the default kill streak messages*/
        defaultKillStreaks = new String[]{
                "5:&2[%n] 5 enemies killed! You''re thinning the numbers!",
                "10:&2[%n] 10 killed! Rampage!",
                "15:&2[%n] 15 kills! Dominating all kinds of mobs!",
                "20:&2[%n] 20 kills! Here is your gift card for a killing spree!",
                "25:&2[%n] So many kills in a row! God Like!"
        };
        killstreak.put("KILL_STREAK", Arrays.asList(defaultKillStreaks));
        /** Creating the default death streak messages*/
        defaultDeathStreaks = new String[]{
                "5:&b%n has died, like 5 times.",
                "10:&b%n, craft a sword or something.",
                "15:&b%n is dead more than alive.",
                "20:&b%n is just pathetic.",
                "30:&b%n is clearly playing Minecraft to see what it says when he dies."
        };
        deathstreak.put("DEATH_STREAK", Arrays.asList(defaultDeathStreaks));

// Default Death Messages

        /** Creating the default fall messages*/
        defaultFallMessages = new String[]{
                "&4%n tripped and fell...down a cliff.",
                "&4%n leapt before looking.",
                "&4%n forgot to bring a parachute!",
                "&4%n learned to fly...briefly...",
                "&4%n felt the full effect of gravity.",
                "&4%n just experienced physics in action.",
                "&4%n fell to his death.",
                "&4%n forgot to look out below!",
                "&4%n got a little too close to the edge!",
                "&4%n, gravity is calling your name!",
                "&4%n faceplanted into the ground!",
                "&4%n yells, \"Geronimo!\"....*thud*",
                "&4What goes up must come down, right %n?"
        };
        deathevents.put("FALL",Arrays.asList(defaultFallMessages));
        /** Creating the default drowning messages*/
        defaultDrowningMessages = new String[]{
                "&4%n has drowned.",
                "&4%n has become one with the ocean!",
                "&4%n sunk to the bottom of the ocean.",
                "&4%n went diving but forgot the diving gear!",
                "&4%n needs swimming lessons.",
                "&4%n ''s lungs have been replaced with H20.",
                "&4%n forgot to come up for air!",
                "&4%n is swimming with the fishes!",
                "&4%n had a surfing accident!",
                "&4%n tried to walk on water.",
                "&4%n set a record for holding breath underwater."
        };
        deathevents.put("DROWNING", Arrays.asList(defaultDrowningMessages));
        /** Creating the default fire messages*/
        defaultFireMessages = new String[]{
                "&4%n burned to death.",
                "&4%n has been set on fire!",
                "&4%n is toast! Literally...",
                "&4%n just got barbequed!",
                "&4%n forgot to stop, drop, and roll!",
                "&4%n is extra-crispy!",
                "&4%n spontaneously combusted!",
                "&4%n put his hands in the toaster!",
                "&4%n just got burned!"
        };
        deathevents.put("FIRE", Arrays.asList(defaultFireMessages));
        /** Creating the default fire_tick messages*/
        defaultFireTickMessages = new String[]{
                "&4%n burned to death.",
                "&4%n has been set on fire!",
                "&4%n is toast! Literally...",
                "&4%n just got barbequed!",
                "&4%n forgot to stop, drop, and roll!",
                "%n likes it extra-crispy!",
                "&4%n spontaneously combusted!",
                "&4%n put his hands in the toaster!",
                "&4%n just got burned!"
        };
        deathevents.put("FIRE_TICK", Arrays.asList(defaultFireTickMessages));
        /** Creating the default lava messages*/
        defaultLavaMessages = new String[]{
                "&4%n became obsidian.",
                "&4%n was caught in an active volanic eruption!",
                "&4%n tried to swim in a pool of lava.",
                "&4%n was killed by a lava eruption!",
                "&4%n was forged into obsidian by molten lava.",
                "&4%n took a dip in the wrong kind of pool!",
                "&4%n found out how to encase himself in carbonite.",
                "&4%n, the floor is lava! The floor is lava!"

        };
        deathevents.put("LAVA",Arrays.asList(defaultLavaMessages));
        /** Creating the default creeper messages*/
        defaultCreeperMessages = new String[]{
                "&4%n was creeper bombed!",
                "&4A creeper exploded on %n!",
                "&4A creeper snuck up on %n!",
                "&4A creeper tried to make love with %n...mmm.",
                "&4%n just got the KiSSssss of death!",
                "&4%n tried to hug a creeper!",
                "&4%n is frowning like a creeper now!"
        };
        deathevents.put("CREEPER",Arrays.asList(defaultCreeperMessages));
        /** Creating the default skeleton messages*/
        defaultSkeletonMessages = new String[] {
                "&4A skeleton shot %n to death!",
                "&4A%n was on the wrong end of the bow. ",
                "&4A%n has a skeleton in the closet...",
                "&4%n, strafe the arrows! Strafe the arrows!",
                "&4A skeleton just got a headshot on %n!"
        };
        deathevents.put("SKELETON",Arrays.asList(defaultSkeletonMessages));
        /** Creating the default spider messages*/
        defaultSpiderMessages = new String[]{
                "&4%n is all webbed up.",
                "&4%n got trampled by arachnids!",
                "&4%n got jumped by a spidah!",
                "&4Spiders just climbed all over %n!",
                "&4%n forgot spiders could climb!"
        };
        deathevents.put("SPIDER",Arrays.asList(defaultSpiderMessages));
        /** Creating the default zombie messages*/
        defaultZombieMessages = new String[]{
                "&4%n was punched to death by zombies!",
                "&4%n was bitten by a zombie!",
                "&4%n fell to the hunger of the horde!",
                "&4%n Hasn''t played enough L4D2!",
                "&4%n couldn''t run faster than the zombie!"
        };
        deathevents.put("ZOMBIE",Arrays.asList(defaultZombieMessages));
        /** Creating the default pvp messages*/
        defaultPVPMessages = new String[] {
                "&4%a killed %n using a(n) %i!",
                "&4%a slays %n with a %i!",
                "&4%a hunts %n down with a %i!",
                "&4%n was killed by a %i wielding %a!",
                "&4%a leaves %n a bloody mess!",
                "&4%a uses a %i to end %n''s life!",
                "&4%n collapses due to %i attacks from %a!",
                "&4%n is now a bloody mess thanks to %a''s %i!",
                "&4%a beats %n with a %i!",
                "&4%n was killed by %a''s %i attack!",
                "&4%a defeats %n with a %i attack!",
                "&4%a raises a %i and puts and end to %n''s life!",
                "&4%a took out %n with a %i!",
                "&4%n was victimised by %a''s %i!",
                "&4%n was eliminated by %a''s %i!",
                "&4%a executes %n with a %i!",
                "&4%a finishes %n with a %i!",
                "&4%a''s %i has claimed %n as another victim!",
                "&4%n lost a savage duel to %a!",
                "&4%a has beaten %n to a pulp!",
                "&4%a pwns %n in a vicious duel!",
                "&4Score %a 1 - %n 0!",
                "&4%a has defeated %n in battle!",
                "&4%n has been slain by %a!",
                "&4%a emerges victorious in a duel with %n!",
                "&4%n has been pwned by %a!",
                "&4%n was killed by %a!",
                "&4%n was dominated by %a!",
                "&4%n was fragged by %a!",
                "&4%n needs more practice and was killed by %a!",
                "&4%n was beheaded by %a!"
        };
        deathevents.put("PVP",Arrays.asList(defaultPVPMessages));
        /** Creating the default pvp-fists messages*/
        defaultPVPFistMessages = new String[]{
                "&4%a pummeled %n to death",
                "&4%a crusted %n with their bare hands"
        };
        deathevents.put("FISTS",Arrays.asList(defaultPVPFistMessages));
        /** Creating the default block_explosion messages*/
        defaultBlockExplosionMessages = new String[]{
                "&4Careful %n, TNT goes boom!",
                "&4%n was last seen playing with dynamite.",
                "&4%n exploded into a million pieces!",
                "&4%n cut the wrong wire!",
                "&4%n has left his (bloody) mark on the world.",
                "&4%n was attempting to exterminate gophers with dynamite!",
                "&4%n was playing landmine hopscotch!",
                "&4%n stuck his head in a microwave!"
        };
        deathevents.put("BLOCK_EXPLOSION",Arrays.asList(defaultBlockExplosionMessages));
        /** Creating the default contact messages*/
        defaultContactMessages = new String[]{
                "&4%n got a little too close to a cactus!",
                "&4%n tried to hug a cactus!",
                "&4%n needs to be more careful around cactuses!",
                "&4%n feels the wrath of cactusjack!",
                "&4%n learns the results of rubbing a cactus!",
                "&4%n died from cactus injuries!",
                "&4%n poked himself with a cactus...and died.",
                "&4%n ran into some pointy green stuff that wasn''t grass.",
                "&4%n was distracted by a tumbleweed and died by cactus."
        };
        deathevents.put("CONTACT",Arrays.asList(defaultContactMessages));
        /** Creating the default ghast messages*/
        defaultGhastMessages = new String[]{
                "&4%n was blown to bits by a ghast.",
                "&4 Those aren''t babies you hear, %n!",
                "&4%n was killed by a ghostly hadouken!",
                "&4%n just got exploded by a fireball!",
                "&4%n got too comfy in the Nether!"
        };
        deathevents.put("GHAST",Arrays.asList(defaultGhastMessages));
        /** Creating the default slime messages*/
        defaultSlimeMessages = new String[]{
                "&4A slime found %n. The slime won.",
                "&4%n just was playing with slime. The slime ain''t happy."
        };
        deathevents.put("SLIME",Arrays.asList(defaultSlimeMessages));
        /** Creating the default suffocation messages*/
        defaultSuffocationMessages = new String[]{
                "&4%n suffocated.",
                "&4%n was looking up while digging!",
                "&4%n choked to death on earth!",
                "&4%n choked on a ham sandwich"
        };
        deathevents.put("SUFFOCATION",Arrays.asList(defaultSuffocationMessages));
        /** Creating the default pigzombie messages*/
        defaultPigzombieMessages = new String[]{
                "&4%n lost a fight against a zombie pig.",
                "&4%n, touching a zombie pig is never a good idea.",
                "&4%n, looked at a pigzombie the wrong way."
        };
        deathevents.put("PIGZOMBIE",Arrays.asList(defaultPigzombieMessages));
        /** Creating the default void messages*/
        defaultVoidMessages = new String[]{
                "&4%n died in The Void."
        };
        deathevents.put("VOID",Arrays.asList(defaultVoidMessages));
        /** Creating the default Wolfs messages*/
        defaultWolfMessages = new String[]{
                "&4%n became a wolf''s lunch.",
                "&4%n couldn't howl with the wolfs."
        };
        deathevents.put("WOLF",Arrays.asList(defaultWolfMessages));
        /** Creating the default lightning messages*/
        defaultLightningMessages = new String[]{
                "&4%n was struck down by Zeus'' bolt.",
                "&4%n was electrecuted.",
                "&4%n figured out that it wasn't a pig's nose in the wall."
        };
        /** Creating the default lightning messages*/
        deathevents.put("LIGHTNING",Arrays.asList(defaultLightningMessages));
        defaultSuicideMessages = new String[]{
                "&4%n took matters into his own hands.",
                "&4%n isn't causing NPE's anymore."
        };
        /** Creating the default suicide messages*/
        deathevents.put("SUICIDE",Arrays.asList(defaultSuicideMessages));
        defaultUnknownMessages = new String[]{
                "&4%n died from unknown causes.",
                "&4%n has imploded into nothingness",
                "&4%n has been vaporized",
                "&4%n died from explosive diarrhea",
                "&4%n was killed by Chuck Norris",
                "&4%n was running with scissors...now he runs no more",
                "&4%n was hit by a falling piano",
                "&4%n was assasinated by a shuriken headshot from the shadow",
                "&4%n was barrel rolling...and died",
                "&4%n was killed by Cthulhu",
                "&4%n forgot to wear his spacesuit",
                "&4%n choked on a ham sandwich",
                "&4%n died at the hands of ninja assassins"
        };
        /** Creating the default unknown messages*/
        deathevents.put("UNKNOWN",Arrays.asList(defaultUnknownMessages));
        defaultStarvationMessages = new String[]{
                "&4%n did forget to eat his lunch.",
                "&4%n didn''t find the next Burger.",
                "&4%n became a skeleton.",
                "&4%n TALKS ALL CAPITALS NOW."
        };
        /** Creating the default starvation messages*/
        deathevents.put("STARVATION",Arrays.asList(defaultStarvationMessages));
        /** Creating the default enderman messages*/
        defaultEndermanMessages = new String[]{
                "&4%n, looked at a enderman the wrong way.",
                "&4An enderman pulled %n leg..... off!"
        };
        deathevents.put("ENDERMAN",Arrays.asList(defaultEndermanMessages));
        /** Creating the default cavespider messages*/
        defaultCaveSpiderMessages = new String[]{
                "&4%n will never say itsybitsyspider again.",
                "&4%n is all webbed up.",
                "&4%n got trampled by arachnids!",
                "&4%n got jumped by a spidah!",
                "&4Spiders just climbed all over %n!",
                "&4%n forgot spiders could climb!"
        };
        deathevents.put("CAVESPIDER",Arrays.asList(defaultCaveSpiderMessages));
        /** Creating the default silverfish messages*/
        defaultSilverfishMessages = new String[]{
                "&4%n was killed by a silverfish!",
                "&4%n found something hidden below a rock"
        };
        deathevents.put("SILVERFISH",Arrays.asList(defaultSilverfishMessages));
    }

// And than we add the defaults

    /**
     * Method to add the config variables to the default configuration
     */
    private void customDefaultConfig() {

// Default DeathTpPlus Variables
        config.addDefault("ircDeathTpTag", ircDeathTpTag);
        config.addDefault("dateFormat", dateFormat);
        config.addDefault("timeFormat", timeFormat);
        config.addDefault("enableLockette", enableLockette);
        config.addDefault("enableLWC", enableLWC);
        config.addDefault("locale", locale);
        config.addDefault("allowWorldTravel",allowWorldTravel);
// DeathTp Features Variables
        config.addDefault("enableDeathtp", enableDeathtp);
        config.addDefault("showDeathNotify", showDeathNotify);
        config.addDefault("allowDeathtp", allowDeathtp);
        config.addDefault("allowDeathLog", allowDeathLog);
        config.addDefault("showStreaks", showStreaks);
        config.addDefault("chargeItem", chargeItem);
        config.addDefault("showDeathSign", showDeathSign);
        config.addDefault("deathtpCost", deathtpCost);
        config.addDefault("lwcPublic",lwcPublic);
// TombStone Features (General)
        config.addDefault("enableTombStone", enableTombStone);
        config.addDefault("showTombStoneSign",showTombStoneSign);
        config.addDefault("allowTombStoneDestroy", allowTombStoneDestroy);
        config.addDefault("showTombStoneStatusMessage", showTombStoneStatusMessage);
        config.addDefault("saveTombStoneList", saveTombStoneList);
        config.addDefault("allowInterfere", allowInterfere);
        config.addDefault("voidCheck", voidCheck);
        config.addDefault("creeperProtection", creeperProtection);
// TombStone Features (Removal)
        config.addDefault("destroyOnQuickLoot", destroyOnQuickLoot);
        config.addDefault("removeTombStone", removeTombStone);
        config.addDefault("removeTombStoneTime", removeTombStoneTime);
        config.addDefault("removeTombStoneWhenEmpty", removeTombStoneWhenEmpty);
        config.addDefault("keepTombStoneUntilEmpty", keepTombStoneUntilEmpty);
// TombStone Features (Security)
        config.addDefault("removeTombStoneSecurity", removeTombStoneSecurity);
        config.addDefault("removeTombStoneSecurityTimeOut", removeTombStoneSecurityTimeOut);
// Tomb Features
        config.addDefault("enableTomb", enableTomb);
        config.addDefault("tombCost",tombCost);
        config.addDefault("maxTomb", maxTomb);
        config.addDefault("useTombAsRespawnPoint", useTombAsRespawnPoint);
        config.addDefault("tombKeyWord", tombKeyWord);
        config.addDefault("maxDeaths", maxDeaths);
        config.addDefault("resetTombRespawn", resetTombRespawn);
// Kill Streak Messages
        // Will be added in loadCustomConfig
// Death Streak Messages
        // Will be added in loadCustomConfig
// Death Messages
        // Will be added in loadCustomConfig
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
        locale = config.getString("locale");
        allowWorldTravel = config.getString("allowWorldTravel");
// DeathTpPlus Features
        enableDeathtp = config.getBoolean("enableDeathtp");
        showDeathNotify = config.getBoolean("showDeathNotify");
        allowDeathtp = config.getBoolean("allowDeathtp");
        allowDeathLog = config.getBoolean("allowDeathLog");
        showStreaks = config.getBoolean("showStreaks");
        chargeItem = config.getString("chargeItem");
        showDeathSign = config.getBoolean("showDeathSign");
        deathtpCost = config.getString("deathtpCost");
        lwcPublic = config.getBoolean("lwcPublic");
// Tombstone Features (General)
        enableTombStone = config.getBoolean("enableTombStone");
        showTombStoneSign = config.getBoolean("showTombStoneSign");
        allowTombStoneDestroy = config.getBoolean("allowTombStoneDestroy");
        showTombStoneStatusMessage = config.getBoolean("showTombStoneStatusMessage");
        saveTombStoneList = config.getBoolean("saveTombStoneList");
        allowInterfere = config.getBoolean("allowInterfere");
        voidCheck = config.getBoolean("voidCheck");
        creeperProtection = config.getBoolean("creeperProtection");
        tombStoneSign[0] = config.getString("tombStoneSign.Line1","{name}");
        tombStoneSign[1] = config.getString("tombStoneSign.Line2","RIP");
        tombStoneSign[2] = config.getString("tombStoneSign.Line3","{date}");
        tombStoneSign[3] = config.getString("tombStoneSign.Line4","{time}");
// Tombstone Features (Removal)
        destroyOnQuickLoot = config.getBoolean("destroyOnQuickLoot");
        removeTombStone = config.getBoolean("removeTombStone");
        removeTombStoneTime = config.getString("removeTombStoneTime");
        removeTombStoneWhenEmpty = config.getBoolean("removeTombStoneWhenEmpty");
        keepTombStoneUntilEmpty = config.getBoolean("keepTombStoneUntilEmpty");
// Tombstone Features (Security)
        removeTombStoneSecurity = config.getBoolean("removeTombStoneSecurity");
        removeTombStoneSecurityTimeOut = config.getString("removeTombStoneSecurityTimeOut");
// Tomb Features
        enableTomb = config.getBoolean("enableTomb");
        tombCost = config.getString("tombCost");
        maxTomb = config.getInt("maxTomb");
        useTombAsRespawnPoint = config.getBoolean("useTombAsRespawnPoint");
        tombKeyWord = config.getString("tombKeyWord");
        maxDeaths = config.getInt("maxDeaths");
        resetTombRespawn = config.getBoolean("resetTombRespawn");
// Kill Streak Messages
        killstreak.put("KILL_STREAK",  config.getList ("killstreak", Arrays.asList(defaultKillStreaks)));
// Death Streak Messages
        deathstreak.put("DEATH_STREAK",  config.getList ("deathstreak", Arrays.asList(defaultDeathStreaks)));
// DeathTp Messages
        deathevents.put("FALL",  config.getList ("fall", Arrays.asList(defaultFallMessages)));
        deathevents.put("DROWNING",  config.getList ("drowning", Arrays.asList(defaultDrowningMessages)));
        deathevents.put("FIRE",  config.getList ("fire", Arrays.asList(defaultFireMessages)));
        deathevents.put("FIRE_TICK",  config.getList ("fire_tick", Arrays.asList(defaultFireTickMessages)));
        deathevents.put("LAVA",  config.getList ("lava", Arrays.asList(defaultLavaMessages)));
        deathevents.put("CREEPER",  config.getList ("creeper", Arrays.asList(defaultCreeperMessages)));
        deathevents.put("SKELETON",  config.getList ("skeleton", Arrays.asList(defaultSkeletonMessages)));
        deathevents.put("SPIDER",  config.getList ("spider", Arrays.asList(defaultSpiderMessages)));
        deathevents.put("ZOMBIE",  config.getList ("zombie", Arrays.asList(defaultZombieMessages)));
        deathevents.put("PVP",  config.getList ("pvp", Arrays.asList(defaultPVPMessages)));
        deathevents.put("FISTS",  config.getList ("pvp-fists", Arrays.asList(defaultPVPFistMessages)));
        deathevents.put("BLOCK_EXPLOSION",  config.getList ("block_explosion", Arrays.asList(defaultBlockExplosionMessages)));
        deathevents.put("CONTACT",  config.getList ("contact", Arrays.asList(defaultContactMessages)));
        deathevents.put("GHAST",  config.getList ("ghast", Arrays.asList(defaultGhastMessages)));
        deathevents.put("SLIME",  config.getList ("slime", Arrays.asList(defaultSlimeMessages)));
        deathevents.put("SUFFOCATION",  config.getList ("suffocation", Arrays.asList(defaultSuffocationMessages)));
        deathevents.put("PIGZOMBIE",  config.getList ("pigzombie", Arrays.asList(defaultPigzombieMessages)));
        deathevents.put("VOID",  config.getList ("void", Arrays.asList(defaultVoidMessages)));
        deathevents.put("WOLF",  config.getList ("wolf", Arrays.asList(defaultWolfMessages)));
        deathevents.put("LIGHTNING",  config.getList ("lightning", Arrays.asList(defaultLightningMessages)));
        deathevents.put("SUICIDE",  config.getList ("suicide", Arrays.asList(defaultSuicideMessages)));
        deathevents.put("UNKNOWN",  config.getList ("unknown", Arrays.asList(defaultUnknownMessages)));
        deathevents.put("STARVATION",  config.getList ("starvation", Arrays.asList(defaultStarvationMessages)));
        deathevents.put("ENDERMAN",  config.getList ("enderman", Arrays.asList(defaultEndermanMessages)));
        deathevents.put("CAVESPIDER",  config.getList ("cavespider", Arrays.asList(defaultCaveSpiderMessages)));
        deathevents.put("SILVERFISH",  config.getList ("silverfish", Arrays.asList(defaultSilverfishMessages)));

// Debugging

        log.debug("ircDeathTpTag",ircDeathTpTag );
        log.debug("dateFormat",dateFormat );
        log.debug("timeFormat",timeFormat );
        log.debug("enableLockette",enableLockette );
        log.debug("enableLWC",enableLWC );
        log.debug("locale",locale );
        log.debug("allowWordTravel", allowWorldTravel );
        log.debug("enableDeathtp",enableDeathtp );
        log.debug("showDeathNotify",showDeathNotify );
        log.debug("allowDeathtp",allowDeathtp);
        log.debug("allowDeathLog",allowDeathLog);
        log.debug("showStreaks",showStreaks);
        log.debug("chargeItem",chargeItem );
        log.debug("showDeathSign",showDeathSign );
        log.debug("deathtpCost",deathtpCost );
        log.debug("lwcPublic",lwcPublic );
        log.debug("enableTombStone", enableTombStone);
        log.debug("showTombStoneSign",showTombStoneSign );
        log.debug("allowTombStoneDestroy",allowTombStoneDestroy);
        log.debug("showTombStoneStatusMessage",showTombStoneStatusMessage);
        log.debug("saveTombStoneList",saveTombStoneList);
        log.debug("allowInterfere",allowInterfere);
        log.debug("voidCheck",voidCheck);
        log.debug("creeperProtection",creeperProtection );
        log.debug("tombStoneSign",tombStoneSign );
        log.debug("destroyOnQuickLoot",destroyOnQuickLoot);
        log.debug("removeTombStone",removeTombStone);
        log.debug("removeTombStoneTime", removeTombStoneTime);
        log.debug("removeTombStoneWhenEmpty",removeTombStoneWhenEmpty);
        log.debug("keepTombStoneUntilEmpty",keepTombStoneUntilEmpty);
        log.debug("removeTombStoneSecurity",removeTombStoneSecurity);
        log.debug("removeTombStoneSecurityTimeOut",removeTombStoneSecurityTimeOut);
        log.debug("enableTomb",enableTomb);
        log.debug("tombCost",tombCost );
        log.debug("maxTomb",maxTomb);
        log.debug("useTombAsRespawnPoint",useTombAsRespawnPoint );
        log.debug("tombKeyWord", tombKeyWord);
        log.debug("maxDeaths",maxDeaths );
        log.debug("resetTombRespawn",resetTombRespawn );
        log.debug("killstreak",killstreak);
        log.debug("deathstreak",deathstreak );
        log.debug("deathevents", deathevents);

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
        stream.println("ircDeathTpTag: '" + ircDeathTpTag+ "'");
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
        stream.println("# Locale Version for translation features");
        stream.println("locale: '" + locale + "'");
        stream.println();
        stream.println("# Allow World Travel: yes, no, permissions");
        stream.println("allowWorldTravel: '" + allowWorldTravel + "'");
        stream.println();
        stream.println("#--------- DeathTp Features");
        stream.println();
        stream.println("# Enable DeathTp Features");
        stream.println("enableDeathtp: " + enableDeathtp);
        stream.println();
        stream.println("# Show the death messages on the server");
        stream.println("showDeathNotify: " + showDeathNotify);
        stream.println();
        stream.println("# Allow players to access the deathtp command (Override permissions)");
        stream.println("allowDeathtp: " + allowDeathtp);
        stream.println();
        stream.println("# Log the deaths to file");
        stream.println("allowDeathLog: " + allowDeathLog);
        stream.println();
        stream.println("#Show kill or death streaks read in from killstreak.txt and deathstrek.txt");
        stream.println("showStreaks: " + showStreaks);
        stream.println();
        stream.println("# Item ID of the item you must have in your hand to teleport. Will remove 1 of these when the command is given. Leave 0 for free teleports ");
        stream.println("chargeItem: '" + chargeItem + "'");
        stream.println();
        stream.println("# Show Deathsign (aka Tombstone Signs) upon death");
        stream.println("showDeathSign: " + showDeathSign);
        stream.println();
        stream.println("# Economy costs for deathtp command, leave 0 if you don't want to charge");
        stream.println("deathtpCost: '" + deathtpCost + "'");
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
        stream.println("# We normally checks to make sure we aren't trying to create a chest in the void.");
        stream.println("# If you handle or modify the void with another plugin, you can disable that check here.");
        stream.println("# This option should be true for most servers.");
        stream.println("voidCheck: " + voidCheck);
        stream.println();
        stream.println("# If you are not locking your chests with Lockette or LWC but still want them to be");
        stream.println("# protected against Creeper explosions, or you want your chests to be protected even");
        stream.println("# after they are unlocked, enable this");
        stream.println("creeperProtection: " + creeperProtection);
        stream.println();
        stream.println("# Each line may be one of any custom text OR:");
        stream.println("# {name} for player name");
        stream.println("# {date} for day of death");
        stream.println("# {time} for time of death (server time)");
        stream.println("# {reason} for cause of death");
        stream.println("# REMEMBER: LINES ARE LIMITED TO 15 CHARACTERS, AND DON'T FORGET THE QUOTES!");
        stream.println("tombStoneSign:");
        stream.println("   Line1: '" + tombStoneSign[0] + "'");
        stream.println("   Line2: '" + tombStoneSign[1] + "'");
        stream.println("   Line3: '" + tombStoneSign[2] + "'");
        stream.println("   Line4: '" + tombStoneSign[3] + "'");
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
        stream.println("removeTombStoneTime: '" + removeTombStoneTime + "'");
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
        stream.println("removeTombStoneSecurityTimeOut: '" +removeTombStoneSecurityTimeOut +"'");
        stream.println();
        stream.println("#--------- Tomb Features");
        stream.println();
        stream.println("# Enable the Tomb feature");
        stream.println("enableTomb: " + enableTomb);
        stream.println();
        stream.println("# Price for createing a Tomb");
        stream.println("tombCost: '" + tombCost + "'");
        stream.println();
        stream.println("# Amount of Tombs a player can have");
        stream.println("maxTomb: " + maxTomb);
        stream.println();
        stream.println("# Use the Tomb as a respawn point");
        stream.println("useTombAsRespawnPoint: " + useTombAsRespawnPoint);
        stream.println();
        stream.println("# Keyword used to detect a Tobmb");
        stream.println("tombKeyWord: '" + tombKeyWord + "'");
        stream.println();
        stream.println("# Number of death before destruction of every tomb of the player");
        stream.println("# without resetting the counter. If set to 2, every 2 deaths, the tombs are destroyed. (Sign is dropped) 0: = Disabled");
        stream.println("maxDeaths: " + maxDeaths);
        stream.println();
        stream.println("# When a Tomb is destroyed, the respawn point is reset.");
        stream.println("resetTombRespawn: " + resetTombRespawn);
        stream.println();
        stream.println("#--------- Messages  for DeathTpPlus");
        stream.println();
        stream.println();
        stream.println("#--------- Streaks");
        stream.println();
        stream.println("# Kill Streak Messages");
        stream.println("# format <#of kills> <text to display> %n: = player getting the message (in this case, the one on a killstreak).");
        stream.println("killstreak:");
        for (String msg : killstreak.get("KILL_STREAK")) {
            stream.println("    -'" + msg + "'");
        }
        stream.println("#");
        stream.println("# Death Streak Messages");
        stream.println("# format <#of kills> <text to display> %n: = player getting the message (in this case, the one on a deathstreak)");
        stream.println("deathstreak:");
        for (String msg : deathstreak.get("DEATH_STREAK")) {
            stream.println("    -'" + msg + "'");
        }
        stream.println("#");
        stream.println("#--------- Deathmessages");
        stream.println("# Must contain at least 1 line. If there are more, it will appear randomly when a person dies.");
        stream.println("# %n for player who died");
        stream.println("# %a name of player who attacked in pvp deaths");
        stream.println("# %i for item a player was using to kill someone else");
        stream.println("#");
        stream.println("# Colors");
        stream.println("#");
        stream.println("# &0 Black");
        stream.println("# &1 Navy");
        stream.println("# &2 Green");
        stream.println("# &3 Blue");
        stream.println("# &4 Red");
        stream.println("# &5 Purple");
        stream.println("# &6 Gold");
        stream.println("# &7 LightGray");
        stream.println("# &8 Gray");
        stream.println("# &9 DarkPurple");
        stream.println("# &a LightGreen");
        stream.println("# &b LightBlue");
        stream.println("# &c Rose");
        stream.println("# &d LightPurple");
        stream.println("# &e Yellow");
        stream.println("# &f White");
        stream.println("#");

        List<String> events = new ArrayList<String>(deathevents.keySet());
        for (String event : events) {
            stream.println(event.toLowerCase() +":");
            for (String msg : deathevents.get(event)) {
                stream.println("    -'" + msg + "'");
            }
        }

    }


// *******************************************************************************************************

// And now you need to create the getters and setters if needed for your config variables    


// The plugin specific getters start here!


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

    public String getLocale() {
        return locale;
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

    public HashMap<String, List<String>> getKillstreak() {
        return killstreak;
    }

    public HashMap<String, List<String>> getDeathstreak() {
        return deathstreak;
    }

    public HashMap<String, List<String>> getDeathevents() {
        return deathevents;
    }



    /**
     * Method to get the Instance of the Class, if the class hasn't been initialized yet it will.
     *
     * @return instance of class
     */
    public static DTPConfig getInstance() {
        if (instance == null) {
            instance = new DTPConfig();
        }
        log = DTPLogger.getLogger();
        return instance;
    }

    /**
     * Method to get the Instance of the Class and pass over a different name for the config file, if the class
     * hasn't been initialized yet it will.
     *
     * @param configuratonFile name of the config file
     *
     * @return instance of class
     */
    public static DTPConfig getInstance(String configuratonFile, Plugin plugin) {
        if (instance == null) {
            instance = new DTPConfig();
            DTPConfig.plugin = plugin;
        }
        log = DTPLogger.getLogger();
        configFile = configuratonFile;
        return instance;
    }


// Well that's it.... at least in this class... thanks for reading...


// NOTHING TO CHANGE NORMALLY BELOW!!!

// ToDo.... NOTHING.. you are DONE!    


// *******************************************************************************************************************
// Other Methods no change normally necessary


// The class stuff first


    private DTPConfig() {

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

    public boolean isErrorLogEnabled() {
        return errorLogEnabled;
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
     * @param config references the config file
     * @param plugin references the plugin for this configuration
     *
     * @see #defaultConfig()
     * @see #loadConfig()
     * @see #updateNecessary()
     * @see #updateConfig()
     * @see #versionCheck()
     */

    public void setupConfig(FileConfiguration config, Plugin plugin) {

        this.config = config;
        this.plugin = plugin;
// Checking if config file exists, if not create it
        if (!(new File(plugin.getDataFolder(), configFile)).exists()) {
            log.info("Creating default configuration file");
            defaultConfig();
        }
// Loading the Defaults all the time do to issues with bukkit configuration class defaults
        setupCustomDefaultVariables();
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
        config.addDefault("errorLogEnabled", errorLogEnabled);
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
        config = plugin.getConfig();
        // Starting to update the standard configuration
        configVer = config.getString("configVer");
        errorLogEnabled = config.getBoolean("errorLogEnabled");
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
        log.debug("errorLogEnabled", errorLogEnabled);
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
            PluginDescriptionFile pdfFile = this.plugin.getDescription();
            pluginName = pdfFile.getName();
            pluginVersion = pdfFile.getVersion();
            stream = new PrintWriter(pluginPath + configFile);
//Let's write our config ;)
            stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + pdfFile.getAuthors().toString());
            stream.println("#");
            stream.println("# Configuration File for " + pluginName + ".");
            stream.println("#");
            stream.println("# For detailed assistance please visit: " + pluginSlug);
            stream.println();
            stream.println("#------- Default Configuration");
            stream.println();
            stream.println("# Configuration Version");
            stream.println("configVer: '" + configVer + "'");
            stream.println();
            stream.println("# Error Log Enabled");
            stream.println("# Enable logging to server console");
            stream.println("# Warning and Severe will still be logged.");
            stream.println("errorLogEnabled: " + errorLogEnabled);
            stream.println();
            stream.println("# Debug Log Enabled");
            stream.println("# Enable more logging.. could be messy!");
            stream.println("DebugLogEnabled: " + debugLogEnabled);
            stream.println();
            stream.println("# Check for Update");
            stream.println("# Will check if there is a new version of the plugin out.");
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
     * Method to check if there is a newer version of the plugin available.
     */
    private void versionCheck() {
        differentPluginAvailable = false;
        String thisVersion = plugin.getDescription().getVersion();
        URL url;
        try {
            url = new URL(versionURL);
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String newVersion = "";
            String line;
            while ((line = in.readLine()) != null) {
                newVersion += line;
            }
            in.close();
            if (newVersion.equals(thisVersion)) {
                log.info("is up to date at version "
                        + thisVersion + ".");

            } else {
                log.warning("is out of date!");
                log.warning("This version: " + thisVersion + "; latest version: " + newVersion + ".");
                differentPluginAvailable = true;
            }
        } catch (MalformedURLException ex) {
            log.warning("Error accessing update URL.", ex);
        } catch (IOException ex) {
            log.warning("Error checking for update.", ex);
        }
    }

// Updating the config

    /**
     * Method to update the configuration if it is necessary.
     */
    private void updateConfig() {
        if (configRequiresUpdate) {
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
