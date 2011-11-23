package org.simiancage.DeathTpPlus.helpers; /**
 *
 * PluginName: DeathTpPlus
 * Class: DeathMessagesDTP
 * User: DonRedhorse
 * Date: 23.11.11
 * Time: 22:46
 *
 */

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * The DeathMessagesDTP Class allows you to write a custom deathMessages file to store all death messages for DeathTpPlus.
 *
 * @author Don Redhorse
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DeathMessagesDTP {

    /**
     * Instance of the Configuration Class
     */
    private static DeathMessagesDTP instance = null;

// Nothing to change from here to ==>>>
    /**
     * Object to handle the configuration
     *
     * @see org.bukkit.configuration.file.FileConfiguration
     */
    private FileConfiguration deathMessages;
    /**
     * Object to handle the plugin
     */
    private Plugin plugin;
    /**
     * Configuration File Name
     */
    private static String deathMessageFileName = "deathmessages.yml";

    /**
     * Configuration File
     */

    private File deathMessageFile;

    /**
     * Is the configuration available or did we have problems?
     */
    private boolean deathMessagesAvailable = false;



    // The org.simiancage.DeathTpPlus.helpers.LoggerDTP should be renamed to the name of the class you did change the original org.simiancage.DeathTpPlus.helpers.LoggerClass too.
    /**
     * Reference of the org.simiancage.DeathTpPlus.helpers.LoggerDTP, needs to be renamed to correct name.
     *
     * @see LoggerDTP
     */
    private static LoggerDTP log;
    private static ConfigDTP config;
    private String pluginName;
    private boolean deathMessagesRequiresUpdate;

    // ToDo Change the deathMessagesCurrent if the deathMessages changes!
    /**
     * This is the internal deathMessages version
     */
    private final String deathMessagesCurrent = "3.0";
    /**
     * This is the DEFAULT for the deathMessages file version, should be the same as deathMessagesCurrent. Will afterwards be changed
     */
    private String deathMessagesVer = "3.0";


// and now the real stuff


// ********************************************************************************************************************

// Helper Variables

    //Todo create stuff for:
    // Blaze, Squid, FallingSand, EnderDragon

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


/*  Here comes the custom deathMessages, the default deathMessages is later on in the class
Keep in mind that you need to create your deathMessages file in a way which is
afterwards parsable again from the configuration class of bukkit
*/

// First we have the default part..
// Which is devided in setting up some variables first

    /**
     * Method to setup the deathMessages variables with default values
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
     * Method to add the deathMessages variables to the default configuration
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
     * Method to load the configuration into the deathMessages variables
     */

    private void loadCustomConfig() {

        // Kill Streak Messages
        killstreak.put("KILL_STREAK",  deathMessages.getList ("killstreak", Arrays.asList(defaultKillStreaks)));
// Death Streak Messages
        deathstreak.put("DEATH_STREAK",  deathMessages.getList ("deathstreak", Arrays.asList(defaultDeathStreaks)));
// DeathTp Messages
        deathevents.put("FALL",  deathMessages.getList ("fall", Arrays.asList(defaultFallMessages)));
        deathevents.put("DROWNING",  deathMessages.getList ("drowning", Arrays.asList(defaultDrowningMessages)));
        deathevents.put("FIRE",  deathMessages.getList ("fire", Arrays.asList(defaultFireMessages)));
        deathevents.put("FIRE_TICK",  deathMessages.getList ("fire_tick", Arrays.asList(defaultFireTickMessages)));
        deathevents.put("LAVA",  deathMessages.getList ("lava", Arrays.asList(defaultLavaMessages)));
        deathevents.put("CREEPER",  deathMessages.getList ("creeper", Arrays.asList(defaultCreeperMessages)));
        deathevents.put("SKELETON",  deathMessages.getList ("skeleton", Arrays.asList(defaultSkeletonMessages)));
        deathevents.put("SPIDER",  deathMessages.getList ("spider", Arrays.asList(defaultSpiderMessages)));
        deathevents.put("ZOMBIE",  deathMessages.getList ("zombie", Arrays.asList(defaultZombieMessages)));
        deathevents.put("PVP",  deathMessages.getList ("pvp", Arrays.asList(defaultPVPMessages)));
        deathevents.put("FISTS",  deathMessages.getList ("pvp-fists", Arrays.asList(defaultPVPFistMessages)));
        deathevents.put("BLOCK_EXPLOSION",  deathMessages.getList ("block_explosion", Arrays.asList(defaultBlockExplosionMessages)));
        deathevents.put("CONTACT",  deathMessages.getList ("contact", Arrays.asList(defaultContactMessages)));
        deathevents.put("GHAST",  deathMessages.getList ("ghast", Arrays.asList(defaultGhastMessages)));
        deathevents.put("SLIME",  deathMessages.getList ("slime", Arrays.asList(defaultSlimeMessages)));
        deathevents.put("SUFFOCATION",  deathMessages.getList ("suffocation", Arrays.asList(defaultSuffocationMessages)));
        deathevents.put("PIGZOMBIE",  deathMessages.getList ("pigzombie", Arrays.asList(defaultPigzombieMessages)));
        deathevents.put("VOID",  deathMessages.getList ("void", Arrays.asList(defaultVoidMessages)));
        deathevents.put("WOLF",  deathMessages.getList ("wolf", Arrays.asList(defaultWolfMessages)));
        deathevents.put("LIGHTNING",  deathMessages.getList ("lightning", Arrays.asList(defaultLightningMessages)));
        deathevents.put("SUICIDE",  deathMessages.getList ("suicide", Arrays.asList(defaultSuicideMessages)));
        deathevents.put("UNKNOWN",  deathMessages.getList ("unknown", Arrays.asList(defaultUnknownMessages)));
        deathevents.put("STARVATION",  deathMessages.getList ("starvation", Arrays.asList(defaultStarvationMessages)));
        deathevents.put("ENDERMAN",  deathMessages.getList ("enderman", Arrays.asList(defaultEndermanMessages)));
        deathevents.put("CAVESPIDER",  deathMessages.getList ("cavespider", Arrays.asList(defaultCaveSpiderMessages)));
        deathevents.put("SILVERFISH",  deathMessages.getList ("silverfish", Arrays.asList(defaultSilverfishMessages)));

// Debugging

        log.debug("killstreak",killstreak);
        log.debug("deathstreak",deathstreak );
        log.debug("deathevents", deathevents);

    }

// And than we write it....


    /**
     * Method to write the custom deathMessages variables into the deathMessages file
     *
     * @param stream will be handed over by  writeConfig
     */

    private void writeCustomConfig(PrintWriter stream) {
//Start here writing your deathMessages variables into the deathMessages file inkl. all comments

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

// And now you need to create the getters and setters if needed for your deathMessages variables


// The plugin specific getters start here!

    public HashMap<String, List<String>> getKillstreak() {
        return killstreak;
    }

    public HashMap<String, List<String>> getDeathstreak() {
        return deathstreak;
    }

    public HashMap<String, List<String>> getDeathevents() {
        return deathevents;
    }





// Last change coming up... choosing the right ClassName for the Logger..

    /**
     * Method to get the Instance of the Class, if the class hasn't been initialized yet it will.
     *
     * @return instance of class
     */

    public static DeathMessagesDTP getInstance() {
        if (instance == null) {
            instance = new DeathMessagesDTP();
        }
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        return instance;
    }

    /**
     * Method to get the Instance of the Class and pass over a different name for the deathMessages file, if the class
     * hasn't been initialized yet it will.
     *
     * @param deathMessageFileName name of the deathMessages file
     *
     * @return instance of class
     */
    public static DeathMessagesDTP getInstance(String deathMessageFileName) {
        if (instance == null) {
            instance = new DeathMessagesDTP();
        }
        log = LoggerDTP.getLogger();
        DeathMessagesDTP.deathMessageFileName = deathMessageFileName;
        config = ConfigDTP.getInstance();
        return instance;
    }


// Well that's it.... at least in this class... thanks for reading...


// NOTHING TO CHANGE NORMALLY BELOW!!!




// *******************************************************************************************************************
// Other Methods no change normally necessary


// The class stuff first


    private DeathMessagesDTP() {

    }


// than the getters


    /**
     * Method to return the Config File Version
     *
     * @return configVer  Config File Version
     */
    public String configVer() {
        return deathMessagesVer;
    }



// And the rest

// Setting up the deathMessages

    /**
     * Method to setup the configuration.
     * If the configuration file doesn't exist it will be created by {@link #defaultConfig()}
     * After that the configuration is loaded {@link #loadConfig()}
     * We than check if an configuration update is necessary {@link #updateNecessary()}
     * and if {@link #autoUpdateConfig} is true we update the configuration {@link #updateConfig()}
     * If {@link #checkForUpdate} is true we check if there is a new version of the plugin {@link #versionCheck()}
     * and set {@link #deathMessagesAvailable} to true
     *
     * @param deathmessages references the deathMessages file
     * @param plugin references the plugin for this configuration
     *
     * @see #defaultConfig()
     * @see #loadConfig()
     * @see #updateNecessary()
     * @see #updateConfig()
     * @see #versionCheck()
     */

    public void setupConfig(FileConfiguration deathmessages, Plugin plugin) {

        this.deathMessages = deathmessages;
        this.plugin = plugin;
// Checking if deathMessages file exists, if not create it
        if (!(new File(plugin.getDataFolder(), deathMessageFileName)).exists()) {
            log.info("Creating default deahtmessages file");
            defaultConfig();
        }
// Loading the deathMessages from file
        loadConfig();

// Checking internal deathMessagesCurrent and deathMessages file deathMessagesVer

        updateNecessary();
// If deathMessages file has new options update it if enabled
        if (config.isAutoUpdateConfig()) {
            updateConfig();
        }
// Also check for New Version of the plugin
        if (config.isCheckForUpdate()) {
            versionCheck();
        }
        deathMessagesAvailable = true;
    }


// Creating the defaults

// Configuring the Default options..

    /**
     * Method to write and create the default configuration.
     * The custom configuration variables are added via #setupCustomDefaultVariables()
     * Than we write the configuration to disk  #writeConfig()
     * Than we get the deathMessages object from disk
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
        deathMessageFile = new File(plugin.getDataFolder(), deathMessageFileName);
        try {
            deathMessages.load(deathMessageFile);
        } catch (IOException e) {
            log.debug("Error loading deathmessages file", e);
        } catch (InvalidConfigurationException e) {
            log.debug("Error in the deathmessages configuration", e);
        }
        deathMessages.addDefault("deathMessagesVer", deathMessagesVer);
        customDefaultConfig();
    }


// Loading the configuration

    /**
     * Method for loading the configuration from disk.
     * First we get the deathMessages object from disk, than we
     * read in the standard configuration part.
     * We also log a message if #debugLogEnabled
     * and we produce some debug logs.
     * After that we load the custom configuration part #loadCustomConfig()
     *
     * @see #loadCustomConfig()
     */

    private void loadConfig() {
        deathMessageFile = new File(plugin.getDataFolder(), deathMessageFileName);
        try {
            deathMessages.load(deathMessageFile);
        } catch (IOException e) {
            log.debug("Error loading deathmessages file", e);
        } catch (InvalidConfigurationException e) {
            log.debug("Error in the deathmessages configuration", e);
        }
        // Starting to update the standard configuration
        deathMessagesVer = deathMessages.getString("deathMessagesVer");
        // Debug OutPut NOW!
        log.debug("deathMessagesCurrent", deathMessagesCurrent);
        log.debug("deathMessagesVer", deathMessagesVer);
        loadCustomConfig();

        log.info("Deathmessages v." + deathMessagesVer + " loaded.");
    }


//  Writing the deathMessages file

    /**
     * Method for writing the configuration file.
     * First we write the standard configuration part, than we
     * write the custom configuration part via #writeCustomConfig()
     *
     * @return true if writing the deathMessages was successful
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
            stream = new PrintWriter(pluginPath + deathMessageFileName);
//Let's write our deathMessages ;)
            stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + pdfFile.getAuthors().toString());
            stream.println("#");
            stream.println("# Deathmessage File for " + pluginName + ".");
            stream.println("#");
            stream.println("# DeathMessages Version");
            stream.println("deathMessagesVer: '" + deathMessagesVer + "'");
            stream.println();

// Getting the custom deathMessages information from the top of the class
            writeCustomConfig(stream);

            stream.println();

            stream.close();

            success = true;

        } catch (FileNotFoundException e) {
            log.warning("Error saving the " + deathMessageFileName + ".");
        }
        log.debug("Default DeathMessages written", success);
        return success;
    }


// Checking if the configVersions differ

    /**
     * Method to check if the configuration version are different.
     * will set #deathMessagesRequiresUpdate to true if versions are different
     */
    private void updateNecessary() {
        if (deathMessagesVer.equalsIgnoreCase(deathMessagesCurrent)) {
            log.info("Deathmessages are up to date");
        } else {
            log.warning("Deathmessages are not up to date!");
            log.warning("Deathmessages File Version: " + deathMessagesVer);
            log.warning("Internal Deathmessages Version: " + deathMessagesCurrent);
            log.warning("It is suggested to update the deathMessages.yml!");
            deathMessagesRequiresUpdate = true;
        }
    }



// Updating the deathMessages

    /**
     * Method to update the configuration if it is necessary.
     */
    private void updateConfig() {
        if (deathMessagesRequiresUpdate) {
            deathMessagesVer = deathMessagesCurrent;
            if (writeConfig()) {
                log.info("Deathmessages were updated with new default values.");
                log.info("Please change them to your liking.");
            } else {
                log.warning("Deathmessages file could not be auto updated.");
                log.warning("Please rename " + deathMessageFileName + " and try again.");
            }
        }
    }

// Reloading the deathMessages

    /**
     * Method to reload the configuration.
     *
     * @return msg with the status of the reload
     */

    public String reloadConfig() {
        String msg;
        if (deathMessagesAvailable) {
            loadConfig();
            log.info("Deathmessages reloaded");
            msg = "Deathmessages were reloaded";
        } else {
            log.severe("Reloading Deathmessages before it exists.");
            log.severe("Flog the developer!");
            msg = "Something terrible terrible did go really really wrong, see console log!";
        }
        return msg;
    }
// Saving the deathMessages


    /**
     * Method to save the deathMessages to file.
     *
     * @return true if the save was successful
     */
    public boolean saveConfig() {
        boolean saved = false;
        if (config.isSaveConfig()) {
            saved = writeConfig();
        }
        return saved;
    }

}
