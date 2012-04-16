package org.simiancage.DeathTpPlus.helpers;
/**
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * The DeathMessagesDTP Class allows you to write a custom deathMessageFileConfig file to store all death messages for DeathTpPlus.
 *
 * @author Don Redhorse
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DeathMessagesDTP {

	/**
	 * Bukkit Death Events
	 */

	// ToDo add new DeathMethods if they come up
	public static enum DeathEventType {
		BLOCK_EXPLOSION, ENTITY_EXPLOSION, CAVE_SPIDER, CONTACT, CREEPER, DROWNING, ENDERMAN, FALL, FIRE, FIRE_TICK, GHAST, GIANT, LAVA, LIGHTNING, MONSTER, PIG_ZOMBIE, PVP, PVP_FISTS, PVP_TAMED, SILVERFISH, SKELETON, SLIME, SPIDER, STARVATION, SUFFOCATION, SUICIDE, UNKNOWN, VOID, WOLF, ZOMBIE, BLAZE, MAGMA_CUBE, ENDERDRAGON, DISPENSER, POISON, MAGIC, IRON_GOLEM
	}

	/**
	 * Default Death Message
	 */
	private static final String DEFAULT_DEATH_MESSAGE = "%n died from unknown causes";

	/**
	 * Instance of the DeathMessagesDTP Class
	 */
	private static DeathMessagesDTP instance = null;

// Nothing to change from here to ==>>>
	/**
	 * Object to handle the configuration
	 *
	 * @see org.bukkit.configuration.file.FileConfiguration
	 */
	private FileConfiguration deathMessageFileConfig;
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

	/**
	 * Random Number
	 */
	private static Random random = new Random();

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

	// ToDo Change the deathMessagesCurrent if the deathMessageFileConfig changes!
	/**
	 * This is the internal deathMessageFileConfig version
	 */
	private final String deathMessagesCurrent = "3.4";
	/**
	 * This is the DEFAULT for the deathMessageFileConfig file version, should be the same as deathMessagesCurrent. Will afterwards be changed
	 */
	private String deathMessagesVer = deathMessagesCurrent;


// and now the real stuff


// ********************************************************************************************************************

// Helper Variables

	/**
	 * Array which holds default Death Streak messages
	 */
	private String[] defaultDeathStreaks;
	/**
	 * Array which holds default Kill Streak messages
	 */
	private String[] defaultKillStreaks;
	/**
	 * Array which holds default Multi Kill messages
	 */
	private String[] defaultMultiKill;
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
	 * Array which holds default PVP-Fist messages
	 */
	private String[] defaultPVPFistMessages;
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
	 * Array which holds default PVP Tamed messages
	 */
	private String[] defaultPVPTamedMessages;
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

	/**
	 * Array which holds default Dispenser messages
	 */
	private String[] defaultDispenserMessages;

	/**
	 * Array which holds default Posion messages
	 */
	private String[] defaultPosionMessages;

	/**
	 * Array which holds default Magic messages
	 */
	private String[] defaultMagicMessages;

	// ToDo add new variables on top

// Messages  for DeathTpPlus


// Streaks

	/**
	 * Kill Streak Messages
	 * format <#of kills>: <text to display> %n = player getting the message (in this case, the one on a killStreakMessages).
	 */
	private static List<String> killStreakMessages;
	/**
	 * Death Streak Messages
	 * format <#of kills>: <text to display> %n = player getting the message (in this case, the one on a deathStreakMessages).
	 */
	private static List<String> deathStreakMessages;
	/**
	 * Multi Kill Messages
	 * format <#of kills>: <text to display>
	 */
	private static List<String> multiKillMessages;

// Deathmessages
	/**
	 * Must contain at least 1 line. If there are more, it will appear randomly when a person dies.
	 * %n for player who died
	 * %a name of player who attacked in pvp deaths
	 * %i for item a player was using to kill someone else
	 * <p/>
	 * Colors
	 * <p/>
	 * &0 Black
	 * &1 Navy
	 * &2 Green
	 * &3 Blue
	 * &4 Red
	 * &5 Purple
	 * &6 Gold
	 * &7 LightGray
	 * &8 Gray
	 * &9 DarkPurple
	 * &a LightGreen
	 * &3 LightBlue
	 * &c Rose
	 * &d LightPurple
	 * &e Yellow
	 * &f White
	 */
	private static HashMap<DeathEventType, List<String>> deathMessages = new HashMap<DeathEventType, List<String>>();


// *******************************************************************************************************************


/*  Here comes the custom deathMessageFileConfig, the default deathMessageFileConfig is later on in the class
Keep in mind that you need to create your deathMessageFileConfig file in a way which is
afterwards parsable again from the configuration class of bukkit
*/

// First we have the default part..
// Which is devided in setting up some variables first

	/**
	 * Method to setup the deathMessageFileConfig variables with default values
	 */

	private void setupCustomDefaultVariables() {

// Default Streak Messages

		/** Creating the default kill streak messages*/
		defaultKillStreaks = new String[]{
				"5:&2[%n] 5 enemies killed! You're thinning the numbers!",
				"10:&2[%n] 10 killed! Rampage!",
				"15:&2[%n] 15 kills! Dominating all kinds of mobs!",
				"20:&2[%n] 20 kills! Here is your gift card for a killing spree!",
				"25:&2[%n] So many kills in a row! God Like!"
		};
		killStreakMessages = Arrays.asList(defaultKillStreaks);
		/** Creating the default death streak messages*/
		defaultDeathStreaks = new String[]{
				"5:&5%n&c has died, like 5 times.",
				"10:&5%n&c, craft a sword or something.",
				"15:&5%n&c is dead more than alive.",
				"20:&5%n&c is just pathetic.",
				"30:&5%n&c wants to read all the death streak messages."
		};
		deathStreakMessages = Arrays.asList(defaultDeathStreaks);

// Default Multi Kill Messages
		defaultMultiKill = new String[]{
				"2:&cDouble Kill!",
				"3:&cMulti Kill!",
				"4:&cMega Kill!",
				"5:&cUltra Kill!",
				"6:&cMonster Kill!",
				"7:&cLudicrous Kill!",
				"8:&cHoly S**t!"
		};
		multiKillMessages = Arrays.asList(defaultMultiKill);

// Default Death Messages

		/** Creating the default fall messages*/
		defaultFallMessages = new String[]{
				"&5%n&7 tripped and fell...down a cliff.",
				"&5%n&7 leapt before looking.",
				"&5%n&7 forgot to bring a parachute!",
				"&5%n&7 learned to fly...briefly...",
				"&5%n&7 felt the full effect of gravity.",
				"&5%n&7 just experienced physics in action.",
				"&5%n&7 fell to his death.",
				"&5%n&7 forgot to look out below!",
				"&5%n&7 got a little too close to the edge!",
				"&5%n&7, gravity is calling your name!",
				"&5%n&7 face planted into the ground!",
				"&5%n&7 yells, 'Geronimo!'....*thud*",
				"What goes up must come down, right &5%n&7?",
				"&5%n&7 must have had their shoelaces tied together."
		};
		deathMessages.put(DeathEventType.FALL, Arrays.asList(defaultFallMessages));
		/** Creating the default drowning messages*/
		defaultDrowningMessages = new String[]{
				"&5%n&7 drowned",
				"&5%n&7 become one with the ocean!",
				"&5%n&7 sunk to the bottom of the ocean.",
				"&5%n&7 went diving but forgot the diving gear!",
				"&5%n&7 needs swimming lessons.",
				"&5%n 's&7 lungs have been replaced with H20.",
				"&5%n&7 forgot to come up for air!",
				"&5%n&7 is swimming with the fishes!",
				"&5%n&7 had a surfing accident!",
				"&5%n&7 tried to walk on water.",
				"&5%n&7 set a record for holding breath under water."
		};
		deathMessages.put(DeathEventType.DROWNING, Arrays.asList(defaultDrowningMessages));
		/** Creating the default fire messages*/
		defaultFireMessages = new String[]{
				"&5%n&7 burned to death.",
				"&5%n&7 was set on fire!",
				"&5%n&7 is toast! Literally...",
				"&5%n&7 just got barbequed!",
				"&5%n&7 forgot to stop, drop, and roll!",
				"&5%n&7 is extra-crispy!",
				"&5%n&7 spontaneously combusted!",
				"&5%n&7 put his hands in the toaster!",
				"&5%n&7 just got burned!"
		};
		deathMessages.put(DeathEventType.FIRE, Arrays.asList(defaultFireMessages));
		/** Creating the default fire_tick messages*/
		defaultFireTickMessages = new String[]{
				"&5%n&7 burned to death.",
				"&5%n&7 was set on fire!",
				"&5%n&7 is toast! Literally...",
				"&5%n&7 just got barbequed!",
				"&5%n&7 forgot to stop, drop, and roll!",
				"%n&7 likes it extra-crispy!",
				"&5%n&7 spontaneously combusted!",
				"&5%n&7 put his hands in the toaster!",
				"&5%n&7 just got burned!"
		};
		deathMessages.put(DeathEventType.FIRE_TICK, Arrays.asList(defaultFireTickMessages));
		/** Creating the default lava messages*/
		defaultLavaMessages = new String[]{
				"&5%n&7 became obsidian.",
				"&5%n&7 was caught in an active volcanic eruption!",
				"&5%n&7 tried to swim in a pool of lava.",
				"&5%n&7 was killed by a lava eruption!",
				"&5%n&7 was forged into obsidian by molten lava.",
				"&5%n&7 took a dip in the wrong kind of pool!",
				"&5%n&7 found out how to encase himself in carbonite.",
				"&5%n&7! the floor is lava! The floor is lava!"

		};
		deathMessages.put(DeathEventType.LAVA, Arrays.asList(defaultLavaMessages));
		/** Creating the default creeper messages*/
		defaultCreeperMessages = new String[]{
				"&5%n&7 was creeper bombed!",
				"A creeper exploded on &5%n&7!",
				"A creeper snuck up on &5%n&7!",
				"A creeper tried to make love with &5%n&7...mmm.",
				"&5%n&7 just got the KiSSssss of death!",
				"&5%n&7 tried to hug a creeper!",
				"&5%n&7 is frowning like a creeper now!"
		};
		deathMessages.put(DeathEventType.CREEPER, Arrays.asList(defaultCreeperMessages));
		/** Creating the default skeleton messages*/
		defaultSkeletonMessages = new String[]{
				"A skeleton shot &5%n&7 to death!",
				"&5%n&7 was on the wrong end of the bow. ",
				"&5%n&7 had a skeleton in the closet...",
				"&5%n&7! strafe the arrows! Strafe the arrows!",
				"&7A skeleton just got a head shot on &5%n&7!"
		};
		deathMessages.put(DeathEventType.SKELETON, Arrays.asList(defaultSkeletonMessages));
		/** Creating the default spider messages*/
		defaultSpiderMessages = new String[]{
				"&5%n&7 is all webbed up.",
				"&5%n&7 got trampled by arachnids!",
				"&5%n&7 got jumped by a spidah!",
				"Spiders just climbed all over &5%n&7!",
				"&5%n&7 forgot spiders could climb!"
		};
		deathMessages.put(DeathEventType.SPIDER, Arrays.asList(defaultSpiderMessages));
		/** Creating the default zombie messages*/
		defaultZombieMessages = new String[]{
				"&5%n&7 was punched to death by zombies!",
				"&5%n&7 was bitten by a zombie!",
				"&5%n&7 fell to the hunger of the horde!",
				"&5%n&7 Hasn't played enough L4D2!",
				"&5%n&7 couldn't run faster than the zombie!",
				"&5%n&7 should have invested in a shotgun."
		};
		deathMessages.put(DeathEventType.ZOMBIE, Arrays.asList(defaultZombieMessages));
		/** Creating the default pvp messages*/
		defaultPVPMessages = new String[]{
				"&f%a&7 killed &5%n&7 using a(n) &3%i&7!",
				"&f%a&7 slays &5%n&7 with a &3%i&7!",
				"&f%a&7 hunts &5%n&7 down with a &3%i&7!",
				"&5%n&7 was killed by a &3%i&7 wielding %a!",
				"&f%a&7 leaves &5%n&7 a bloody mess!",
				"&f%a&7 uses a &3%i&7 to end &5%n''s&7 life!",
				"&5%n&7 collapses due to &3%i&7 attacks from %a!",
				"&5%n&7 is now a bloody mess thanks to %a''s &3%i&7!",
				"&f%a&7 beats &5%n&7 with a &3%i&7!",
				"&5%n&7 was killed by %a''s &3%i&7 attack!",
				"&f%a&7 defeats &5%n&7 with a &3%i&7 attack!",
				"&f%a&7 raises a &3%i&7 and puts and end to &5%n''s&7 life!",
				"&f%a&7 took out &5%n&7 with a &3%i&7!",
				"&5%n&7 was victimized by &f%a''s&7 &3%i&7!",
				"&5%n&7 was eliminated by %a''s &3%i&7!",
				"&f%a&7 executes &5%n&7 with a &3%i&7!",
				"&f%a&7 finishes &5%n&7 with a &3%i&7!",
				"&f%a&7's &3%i&7 has claimed &5%n&7 as another victim!",
				"&5%n&7 lost a savage duel to %a!",
				"&f%a&7 has beaten &5%n&7 to a pulp!",
				"&f%a&7 pwns &5%n&7 in a vicious duel!",
				"&5Score %a 1 - &5%n&7 0!",
				"&f%a&7 has defeated &5%n&7 in battle!",
				"&5%n&7 was slain by &f%a&7!",
				"&f%a&7 emerges victorious in a duel with &5%n&7!",
				"&5%n&7 was pwned by &f%a&7!",
				"&5%n&7 was killed by %a!",
				"&5%n&7 was dominated by %a!",
				"&5%n&7 was fragged by %a!",
				"&5%n&7 needs more practice and was killed by %a!",
				"&5%n&7 was beheaded by %a!"
		};
		deathMessages.put(DeathEventType.PVP, Arrays.asList(defaultPVPMessages));
		/** Creating the default pvp-fists messages*/
		defaultPVPFistMessages = new String[]{
				"&f%a&7 pummeled &5%n&7 to death",
				"&f%a&7 crusted &5%n&7 with their bare hands"
		};
		deathMessages.put(DeathEventType.PVP_FISTS, Arrays.asList(defaultPVPFistMessages));
		/** Creating the default block_explosion messages*/
		defaultBlockExplosionMessages = new String[]{
				"Careful &5%n&7, TNT goes boom!",
				"&5%n&7 was last seen playing with dynamite.",
				"&5%n&7 exploded into a million pieces!",
				"&5%n&7 cut the wrong wire!",
				"&5%n&7 left his (bloody) mark on the world.",
				"&5%n&7 attempted to exterminate gophers with dynamite!",
				"&5%n&7 played land mine hop scotch!",
				"&5%n&7 stuck his head in a microwave!"
		};
		deathMessages.put(DeathEventType.BLOCK_EXPLOSION, Arrays.asList(defaultBlockExplosionMessages));
		/** Creating the default entity_explosion messages*/
		defaultEntityExplosionMessages = new String[]{
				"Well... something exploded on &5%n&7!"
		};
		deathMessages.put(DeathEventType.ENTITY_EXPLOSION, Arrays.asList(defaultEntityExplosionMessages));
		/** Creating the default contact messages*/
		defaultContactMessages = new String[]{
				"&5%n&7 got a little too close to a cactus!",
				"&5%n&7 tried to hug a cactus!",
				"&5%n&7 needs to be more careful around cactuses!",
				"&5%n&7 felt the wrath of Cactus Jack!",
				"&5%n&7 learned the result of rubbing a cactus!",
				"&5%n&7 died from cactus injuries!",
				"&5%n&7 poked himself with a cactus...and died.",
				"&5%n&7 ran into some pointy green stuff that wasn't grass.",
				"&5%n&7 was distracted by a tumble weed and died by cactus."
		};
		deathMessages.put(DeathEventType.CONTACT, Arrays.asList(defaultContactMessages));
		/** Creating the default ghast messages*/
		defaultGhastMessages = new String[]{
				"&5%n&7 was blown to bits by a ghast.",
				"Those aren't babies you hear, &5%n&7!",
				"&5%n&7 was killed by a ghostly hadouken!",
				"&5%n&7 just got exploded by a fireball!",
				"&5%n&7 got too comfy in the Nether!"
		};
		deathMessages.put(DeathEventType.GHAST, Arrays.asList(defaultGhastMessages));
		/** Creating the default slime messages*/
		defaultSlimeMessages = new String[]{
				"A slime found &5%n&7. The slime won.",
				"&5%n&7 wanted to play with slime. The slime wasn't happy.",
				"&5%n&7 was killed for saying, \"Eeeeehhhh, he slimed me!\"",
				"&5%n&7 crossed the streams."
		};
		deathMessages.put(DeathEventType.SLIME, Arrays.asList(defaultSlimeMessages));
		/** Creating the default suffocation messages*/
		defaultSuffocationMessages = new String[]{
				"&5%n&7 suffocated.",
				"&5%n&7 was looking up while digging!",
				"&5%n&7 choked to death on earth!",
				"&5%n&7 choked on a ham sandwich"
		};
		deathMessages.put(DeathEventType.SUFFOCATION, Arrays.asList(defaultSuffocationMessages));
		/** Creating the default pigzombie messages*/
		defaultPigzombieMessages = new String[]{
				"&5%n&7 lost a fight against a zombie pig.",
				"&5%n&7, touching a zombie pig is never a good idea.",
				"&5%n&7, looked at a pigzombie the wrong way."
		};
		deathMessages.put(DeathEventType.PIG_ZOMBIE, Arrays.asList(defaultPigzombieMessages));
		/** Creating the default void messages*/
		defaultVoidMessages = new String[]{
				"&5%n&7 died in The Void."
		};
		deathMessages.put(DeathEventType.VOID, Arrays.asList(defaultVoidMessages));
		/** Creating the default Wolfs messages*/
		defaultWolfMessages = new String[]{
				"&5%n&7 became a wolf's lunch.",
				"&5%n&7 couldn't howl with the wolfs."
		};
		deathMessages.put(DeathEventType.WOLF, Arrays.asList(defaultWolfMessages));
		/** Creating the default lightning messages*/
		defaultLightningMessages = new String[]{
				"&5%n&7 was struck down by Zeus' bolt.",
				"&5%n&7 was electrecuted.",
				"&5%n&7 figured out that it wasn't a pig's nose in the wall."
		};
		/** Creating the default lightning messages*/
		deathMessages.put(DeathEventType.LIGHTNING, Arrays.asList(defaultLightningMessages));
		defaultSuicideMessages = new String[]{
				"&5%n&7 took matters into his own hands.",
				"&5%n&7 isn't causing NPE''s anymore."
		};
		/** Creating the default suicide messages*/
		deathMessages.put(DeathEventType.SUICIDE, Arrays.asList(defaultSuicideMessages));
		defaultUnknownMessages = new String[]{
				"&5%n&7 died from unknown causes.",
				"&5%n&7 imploded into nothingness",
				"&5%n&7 was vaporized",
				"&5%n&7 died from explosive diarrhea",
				"&5%n&7 was killed by Chuck Norris",
				"&5%n&7 was running with scissors...now he runs no more",
				"&5%n&7 was hit by a falling piano",
				"&5%n&7 was assasinated by a shuriken headshot from the shadow",
				"&5%n&7 was barrel rolling...and died",
				"&5%n&7 was killed by Cthulhu",
				"&5%n&7 forgot to wear his spacesuit",
				"&5%n&7 choked on a ham sandwich",
				"&5%n&7 died at the hands of ninja assassins"
		};
		/** Creating the default unknown messages*/
		deathMessages.put(DeathEventType.UNKNOWN, Arrays.asList(defaultUnknownMessages));
		defaultStarvationMessages = new String[]{
				"&5%n&7 did forget to eat his lunch.",
				"&5%n&7 didn't find the next Burger.",
				"&5%n&7 became a skeleton.",
				"&5%n&7 TALKS ALL CAPITALS NOW.",
				"&5%n&7 should have packed a lunch."
		};
		/** Creating the default starvation messages*/
		deathMessages.put(DeathEventType.STARVATION, Arrays.asList(defaultStarvationMessages));
		/** Creating the default enderman messages*/
		defaultEndermanMessages = new String[]{
				"&5%n&7 looked at a enderman the wrong way.",
				"An enderman pulled &5%n&7 leg..... off!"
		};
		deathMessages.put(DeathEventType.ENDERMAN, Arrays.asList(defaultEndermanMessages));
		/** Creating the default cavespider messages*/
		defaultCaveSpiderMessages = new String[]{
				"&5%n&7 will never sing itsybitsyspider again",
				"&5%n&7 is all webbed up.",
				"&5%n&7 was trampled by arachnids!",
				"&5%n&7 was jumped by a spidah!",
				"Spiders just climbed all over &5%n&7!",
				"&5%n&7 forgot spiders could climb!"
		};
		deathMessages.put(DeathEventType.CAVE_SPIDER, Arrays.asList(defaultCaveSpiderMessages));
		/** Creating the default silverfish messages*/
		defaultSilverfishMessages = new String[]{
				"&5%n&7 was killed by a silverfish!",
				"&5%n&7 found something hidden below a rock",
				"&5%n&7 You can't stuff that many fish into your mouth!",
				"&5%n&7 activated a silverfish trap",
				"&54%n''s&7 last words  'Oh god they''re coming out of the walls!'"
		};
		deathMessages.put(DeathEventType.SILVERFISH, Arrays.asList(defaultSilverfishMessages));
		/** Creating the default PVP tamed messages*/
		defaultPVPTamedMessages = new String[]{
				"&5%n&7 was mauled by &f%a's&7 &3%i",
				"&5%n''s&7 hand was bitten by &f%a's&7 &3%i"
		};
		deathMessages.put(DeathEventType.PVP_TAMED, Arrays.asList(defaultPVPTamedMessages));
		/** Creating the default Giant messages*/
		defaultGiantMessages = new String[]{
				"&5%n&7 was stomped by a giant!",
				"&5%n&7 was flattened by a giant!",
				"&5%n&7 shouldn't have climbed the bean stalk."
		};
		deathMessages.put(DeathEventType.GIANT, Arrays.asList(defaultGiantMessages));
		/** Creating the default Blaze messages*/
		defaultBlazeMessages = new String[]{
				"&5%n&7 was set on fire at a blaze, well.. by a blaze!",
				"&5%n&7 was airbombed!",
				"&5%n&7, not everything on fire is a player!",
				"&5%n&7 nope, that wasn't a rocket."
		};
		deathMessages.put(DeathEventType.BLAZE, Arrays.asList(defaultBlazeMessages));
		/** Creating the default Enderdragon messages*/
		defaultEnderDragonMessages = new String[]{
				"&5%n&7 died at the end... IN the end.",
				"&5%n&7 looking up would have helped.",
				"Well, Anne McCaffrey didn't talk about that kind of Dragon, right &5%n&7?",
				"No egg for you, &5%n&7.",
				"&5%n&7 took the easy way out of \"The End\".",
				"&5%n&7 will never get to read that end poem!",
				"&5%n&7 made a generous donation to the Ender Dragon's hoard."
		};
		deathMessages.put(DeathEventType.ENDERDRAGON, Arrays.asList(defaultEnderDragonMessages));
		/** Creating the default MagmaCube messages*/
		defaultMagmaCubeMessages = new String[]{
				"&5%n&7 didn't expect this kind of slinky!",
				"&5%n&7 got eaten by a cube.",
				"&5%n&7 got coombad by a cube."


		};
		deathMessages.put(DeathEventType.MAGMA_CUBE, Arrays.asList(defaultMagmaCubeMessages));
		/** Creating the default Dispenser Kill messages*/
		defaultDispenserMessages = new String[]{
				"&5%n&7 got shot in the back by a dispenser!",
				"Again the wrong weight Indi? ähm. &5%n&7",
				"&5%n&7 thinks he is Indiana Jones.",
				"&5%n&7 felt for the booby trap."
		};
		deathMessages.put(DeathEventType.DISPENSER, Arrays.asList(defaultDispenserMessages));

		/** Creating the default Posion Kill messages*/
		defaultPosionMessages = new String[]{
				"&5%n&7 swalloed the wrong stuff!",
				"There was a reason the bottle had a skull on it, &5%n&7",
				"&5%n&7 should have asked Flavia de Luce before taking that.",
				"&5%n&7 shouldn't drink tea with the Brewsters.",
				"&5%n&7 is now part of the locks of Panama.",
				"&5%n&7 said: aarrgghhhh."
		};
		deathMessages.put(DeathEventType.POISON, Arrays.asList(defaultPosionMessages));

		/** Creating the default Magic Kill messages*/
		defaultMagicMessages = new String[]{
				"&5%n&7 got killed by a Harry Potter lookalike!",
				"It was: Klaatu barada nikto. &5%n&7",
				"&5%n&7 felt the force.",
				"&5%n&7 thinks that there should be more to magic than just shizzle",
				"&5%n&7 should ask Rincewind the next time"
		};
		deathMessages.put(DeathEventType.MAGIC, Arrays.asList(defaultMagicMessages));

		// ToDo add new messages on top
	}

// And than we add the defaults

	/**
	 * Method to add the deathMessageFileConfig variables to the default configuration
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
	 * Method to load the configuration into the deathMessageFileConfig variables
	 */

	private void loadCustomConfig() {

// Kill Streak Messages
		killStreakMessages = (List<String>) (List<?>) deathMessageFileConfig.getList("killstreak", Arrays.asList(defaultKillStreaks));
		log.informational(killStreakMessages.size() + " messages loaded for killstreak");
// Death Streak Messages
		deathStreakMessages = (List<String>) (List<?>) deathMessageFileConfig.getList("deathstreak", Arrays.asList(defaultDeathStreaks));
		log.informational(deathStreakMessages.size() + " messages loaded for deathstreak");
// Multi Kill Messages
		multiKillMessages = (List<String>) (List<?>) deathMessageFileConfig.getList("mulitkill", Arrays.asList(defaultMultiKill));
		log.informational(multiKillMessages.size() + " messages loaded for multikill");
// DeathTp Messages
		// Workaround for NPE as Monster isn't defined in the deathmessages
		deathMessages.put(DeathEventType.MONSTER, Arrays.asList(defaultUnknownMessages));
		// Normal Death Messages
		log.info("Loading death messages...");
		deathMessages.put(DeathEventType.FALL, (List<String>) (List<?>) deathMessageFileConfig.getList("fall", Arrays.asList(defaultFallMessages)));
		deathMessages.put(DeathEventType.DROWNING, (List<String>) (List<?>) deathMessageFileConfig.getList("drowning", Arrays.asList(defaultDrowningMessages)));
		deathMessages.put(DeathEventType.FIRE, (List<String>) (List<?>) deathMessageFileConfig.getList("fire", Arrays.asList(defaultFireMessages)));
		deathMessages.put(DeathEventType.FIRE_TICK, (List<String>) (List<?>) deathMessageFileConfig.getList("fire_tick", Arrays.asList(defaultFireTickMessages)));
		deathMessages.put(DeathEventType.LAVA, (List<String>) (List<?>) deathMessageFileConfig.getList("lava", Arrays.asList(defaultLavaMessages)));
		deathMessages.put(DeathEventType.CREEPER, (List<String>) (List<?>) deathMessageFileConfig.getList("creeper", Arrays.asList(defaultCreeperMessages)));
		deathMessages.put(DeathEventType.SKELETON, (List<String>) (List<?>) deathMessageFileConfig.getList("skeleton", Arrays.asList(defaultSkeletonMessages)));
		deathMessages.put(DeathEventType.SPIDER, (List<String>) (List<?>) deathMessageFileConfig.getList("spider", Arrays.asList(defaultSpiderMessages)));
		deathMessages.put(DeathEventType.ZOMBIE, (List<String>) (List<?>) deathMessageFileConfig.getList("zombie", Arrays.asList(defaultZombieMessages)));
		deathMessages.put(DeathEventType.PVP, (List<String>) (List<?>) deathMessageFileConfig.getList("pvp", Arrays.asList(defaultPVPMessages)));
		deathMessages.put(DeathEventType.PVP_FISTS, (List<String>) (List<?>) deathMessageFileConfig.getList("pvp-fists", Arrays.asList(defaultPVPFistMessages)));
		deathMessages.put(DeathEventType.BLOCK_EXPLOSION, (List<String>) (List<?>) deathMessageFileConfig.getList("block_explosion", Arrays.asList(defaultBlockExplosionMessages)));
		deathMessages.put(DeathEventType.CONTACT, (List<String>) (List<?>) deathMessageFileConfig.getList("contact", Arrays.asList(defaultContactMessages)));
		deathMessages.put(DeathEventType.GHAST, (List<String>) (List<?>) deathMessageFileConfig.getList("ghast", Arrays.asList(defaultGhastMessages)));
		deathMessages.put(DeathEventType.SLIME, (List<String>) (List<?>) deathMessageFileConfig.getList("slime", Arrays.asList(defaultSlimeMessages)));
		deathMessages.put(DeathEventType.SUFFOCATION, (List<String>) (List<?>) deathMessageFileConfig.getList("suffocation", Arrays.asList(defaultSuffocationMessages)));
		deathMessages.put(DeathEventType.PIG_ZOMBIE, (List<String>) (List<?>) deathMessageFileConfig.getList("pigzombie", Arrays.asList(defaultPigzombieMessages)));
		deathMessages.put(DeathEventType.VOID, (List<String>) (List<?>) deathMessageFileConfig.getList("void", Arrays.asList(defaultVoidMessages)));
		deathMessages.put(DeathEventType.WOLF, (List<String>) (List<?>) deathMessageFileConfig.getList("wolf", Arrays.asList(defaultWolfMessages)));
		deathMessages.put(DeathEventType.LIGHTNING, (List<String>) (List<?>) deathMessageFileConfig.getList("lightning", Arrays.asList(defaultLightningMessages)));
		deathMessages.put(DeathEventType.SUICIDE, (List<String>) (List<?>) deathMessageFileConfig.getList("suicide", Arrays.asList(defaultSuicideMessages)));
		deathMessages.put(DeathEventType.UNKNOWN, (List<String>) (List<?>) deathMessageFileConfig.getList("unknown", Arrays.asList(defaultUnknownMessages)));
		deathMessages.put(DeathEventType.STARVATION, (List<String>) (List<?>) deathMessageFileConfig.getList("starvation", Arrays.asList(defaultStarvationMessages)));
		deathMessages.put(DeathEventType.ENDERMAN, (List<String>) (List<?>) deathMessageFileConfig.getList("enderman", Arrays.asList(defaultEndermanMessages)));
		deathMessages.put(DeathEventType.CAVE_SPIDER, (List<String>) (List<?>) deathMessageFileConfig.getList("cavespider", Arrays.asList(defaultCaveSpiderMessages)));
		deathMessages.put(DeathEventType.SILVERFISH, (List<String>) (List<?>) deathMessageFileConfig.getList("silverfish", Arrays.asList(defaultSilverfishMessages)));
		deathMessages.put(DeathEventType.PVP_TAMED, (List<String>) (List<?>) deathMessageFileConfig.getList("pvp-tamed", Arrays.asList(defaultPVPTamedMessages)));
		deathMessages.put(DeathEventType.GIANT, (List<String>) (List<?>) deathMessageFileConfig.getList("giant", Arrays.asList(defaultGiantMessages)));
		deathMessages.put(DeathEventType.BLAZE, (List<String>) (List<?>) deathMessageFileConfig.getList("blaze", Arrays.asList(defaultBlazeMessages)));
		deathMessages.put(DeathEventType.ENDERDRAGON, (List<String>) (List<?>) deathMessageFileConfig.getList("enderdragon", Arrays.asList(defaultEnderDragonMessages)));
		deathMessages.put(DeathEventType.MAGMA_CUBE, (List<String>) (List<?>) deathMessageFileConfig.getList("magmacube", Arrays.asList(defaultMagmaCubeMessages)));
		deathMessages.put(DeathEventType.DISPENSER, (List<String>) (List<?>) deathMessageFileConfig.getList("dispenser", Arrays.asList(defaultDispenserMessages)));
		deathMessages.put(DeathEventType.POISON, (List<String>) (List<?>) deathMessageFileConfig.getList("poison", Arrays.asList(defaultPosionMessages)));
		deathMessages.put(DeathEventType.MAGIC, (List<String>) (List<?>) deathMessageFileConfig.getList("magic", Arrays.asList(defaultMagicMessages)));

		//ToDo add new deathMessages to the top
		for (DeathEventType deathEventType : DeathEventType.values()) {
			log.debug("deathEventType", deathEventType);
			log.informational(deathMessages.get(deathEventType).size() + " messages loaded for " + deathEventType);
		}
// Debugging

		log.debug("killStreakMessages", killStreakMessages);
		log.debug("deathStreakMessages", deathStreakMessages);
		log.debug("multiKillMessages", multiKillMessages);
		log.debug("deathMessages", deathMessages);

	}

// And than we write it....


	/**
	 * Method to write the custom deathMessageFileConfig variables into the deathMessageFileConfig file
	 *
	 * @param stream will be handed over by  writeConfig
	 */

	private void writeCustomDeathMessages(PrintWriter stream) {
//Start here writing your deathMessageFileConfig variables into the deathMessageFileConfig file inkl. all comments

		stream.println("#--------- Messages  for DeathTpPlus");
		stream.println();
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
		stream.println("# &3 LightBlue");
		stream.println("# &c Rose");
		stream.println("# &d LightPurple");
		stream.println("# &e Yellow");
		stream.println("# &f White");
		stream.println("#");
		stream.println("# Make sure you enclose the messages in \"");

		stream.println("#");
		stream.println();
		stream.println("#--------- Streaks");
		stream.println();
		stream.println("# Kill Streak Messages");
		stream.println("# format <#of kills>: <text to display> %n: = player getting the message (in this case, the one on a killStreakMessages).");
		stream.println("killstreak:");
		for (String msg : killStreakMessages) {
			msg = msg.replace("''", "'");
			stream.println("    - \"" + msg.replace("\"", "'") + "\"");
		}
		stream.println("#");
		stream.println("# Death Streak Messages");
		stream.println("# format <#of kills>: <text to display> %n: = player getting the message (in this case, the one on a deathStreakMessages)");
		stream.println("deathstreak:");
		for (String msg : deathStreakMessages) {
			msg = msg.replace("''", "'");
			stream.println("    - \"" + msg.replace("\"", "'") + "\"");
		}
		stream.println("#");
		stream.println("# Multi Killl Messages");
		stream.println("# format <#of kills>: <text to display> ");
		stream.println("multikill:");
		for (String msg : multiKillMessages) {
			msg = msg.replace("''", "'");
			stream.println("    - \"" + msg.replace("\"", "'") + "\"");
		}
		stream.println("#");
		stream.println("#--------- Deathmessages");
		stream.println("# Must contain at least 1 line. If there are more, it will appear randomly when a person dies.");
		stream.println("# %n for player who died");
		stream.println("# %a name of player who attacked in pvp deaths");
		stream.println("# %i for item a player was using to kill someone else");
		stream.println("#");

		for (DeathEventType deathEventType : DeathEventType.values()) {
			// Workaround for missing Monster Death Messages
			if (deathEventType != DeathEventType.MONSTER) {
				stream.println(mapTypeToNodeName(deathEventType) + ":");

				for (String msg : deathMessages.get(deathEventType)) {
					msg = msg.replace("''", "'");
					stream.println("    - \"" + msg.replace("\"", "'") + "\"");
				}
			}
		}


	}


// *******************************************************************************************************

// And now you need to create the getters and setters if needed for your deathMessageFileConfig variables


// The plugin specific getters start here!

	public List<String> getKillstreak() {
		return killStreakMessages;
	}

	public List<String> getDeathstreak() {
		return deathStreakMessages;
	}

	public HashMap<DeathEventType, List<String>> getDeathMessages() {
		return deathMessages;
	}


// Plugin Specific Helper Methods

	static String mapTypeToNodeName(DeathEventType deathEventType) {
		if (deathEventType == DeathEventType.CAVE_SPIDER) {
			return "cavespider";
		} else if (deathEventType == DeathEventType.PIG_ZOMBIE) {
			return "pigzombie";
		}

		String nodeName = deathEventType.toString().toLowerCase();
		if (!deathEventType.toString().equals("BLOCK_EXPLOSION") && !deathEventType.toString().equals("FIRE_TICK")) {
			nodeName = nodeName.replace("_", "-");
		}

		return nodeName;
	}

	public static String getDeathMessage(DeathDetailDTP deathDetail) {
		String message;
		List<String> messages = deathMessages.get(deathDetail.getCauseOfDeath());

		if (messages == null) {
			message = DEFAULT_DEATH_MESSAGE;
		} else {
			if (config.isUseDisplayNameforBroadcasts()) {
				message = messages.get(random.nextInt(messages.size())).replace("%n", deathDetail.getPlayer().getDisplayName());
			} else {
				message = messages.get(random.nextInt(messages.size())).replace("%n", deathDetail.getPlayer().getName());
			}
		}

		if (deathDetail.isPVPDeath()) {
			if (config.isUseDisplayNameforBroadcasts()) {
				message = message.replace("%i", deathDetail.getMurderWeapon()).replace("%a", deathDetail.getKiller().getDisplayName());
			} else {
				message = message.replace("%i", deathDetail.getMurderWeapon()).replace("%a", deathDetail.getKiller().getName());
			}
		}

		return UtilsDTP.convertColorCodes(message);
	}

	public static String getDeathStreakMessage(Integer deathCount) {
		for (String message : deathStreakMessages) {
			String parts[] = message.split(":");
			if (Integer.parseInt(parts[0]) == -deathCount) {
				return UtilsDTP.convertColorCodes(parts[1]);
			}
		}

		return null;
	}

	public static String getKillStreakMessage(Integer killCount) {
		for (String message : killStreakMessages) {
			String parts[] = message.split(":");
			if (Integer.parseInt(parts[0]) == killCount) {
				return UtilsDTP.convertColorCodes(parts[1]);
			}
		}

		return null;
	}

	public static String getMultiKillMessage(Integer killCount) {
		for (String message : multiKillMessages) {
			String parts[] = message.split(":");
			if (Integer.parseInt(parts[0]) == killCount) {
				return UtilsDTP.convertColorCodes(parts[1]);
			}
		}

		return null;
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
	 * Method to get the Instance of the Class and pass over a different name for the deathMessageFileConfig file, if the class
	 * hasn't been initialized yet it will.
	 *
	 * @param deathMessageFileName name of the deathMessageFileConfig file
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
	 * Parse the Authors Array into a readable String with ',' and 'and'.
	 * taken from MultiVerse-core https://github.com/Multiverse/Multiverse-Core
	 *
	 * @return
	 */
	public String getAuthors() {
		String authors = "";
		List<String> auths = plugin.getDescription().getAuthors();
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
	public String deathMessagesVer() {
		return deathMessagesVer;
	}

	public boolean isDeathMessagesRequiresUpdate() {
		return deathMessagesRequiresUpdate;
	}

// And the rest

// Setting up the deathMessageFileConfig

	/**
	 * Method to setup the configuration.
	 * If the configuration file doesn't exist it will be created by {@link #defaultDeathMessages()}
	 * After that the configuration is loaded {@link #loadDeathMessages()}
	 * We than check if an configuration update is necessary {@link #updateNecessary()}
	 * and if {@link org.simiancage.DeathTpPlus.helpers.ConfigDTP#isAutoUpdateConfig()} is true we update the configuration {@link #updateDeathMessages()}
	 * and finally set {@link #deathMessagesAvailable} to true
	 *
	 * @param plugin references the plugin for this configuration
	 *
	 * @see #defaultDeathMessages()
	 * @see #loadDeathMessages()
	 * @see #updateNecessary()
	 * @see #updateDeathMessages()
	 */

	public void setupDeathMessages(Plugin plugin) {

		this.deathMessageFileConfig = new YamlConfiguration();
		log.debug("deathMessageFileConfig", deathMessageFileConfig);
		this.plugin = plugin;
		log.debug("deathMessageFileName", deathMessageFileName);
// Checking if deathMessageFileConfig file exists, if not create it
		if (!(new File(plugin.getDataFolder(), deathMessageFileName)).exists()) {
			log.info("Creating default deathmessages file");
			defaultDeathMessages();
		}

		deathMessageFile = new File(plugin.getDataFolder(), deathMessageFileName);
		log.debug("deathMessageFile", deathMessageFile);
		try {
			deathMessageFileConfig.load(deathMessageFile);
		} catch (IOException e) {
			log.warning("Error loading deathmessages file", e);
		} catch (InvalidConfigurationException e) {
			log.warning("Error in the deathmessages configuration", e);
		}


// Loading the deathMessageFileConfig from file
		loadDeathMessages();

// Checking internal deathMessagesCurrent and deathMessageFileConfig file deathMessagesVer

		updateNecessary();
// If deathMessageFileConfig file has new options update it if enabled
		if (config.isAutoUpdateConfig()) {
			updateDeathMessages();
		}
		deathMessagesAvailable = true;
	}


// Creating the defaults

// Configuring the Default options..

	/**
	 * Method to write and create the default configuration.
	 * The custom configuration variables are added via #setupCustomDefaultVariables()
	 * Than we write the configuration to disk  #writeConfig()
	 * Than we get the deathMessageFileConfig object from disk
	 * We are adding the default configuration for the variables and load the
	 * defaults for the custom variables  #customDefaultConfig()
	 *
	 * @see #setupCustomDefaultVariables()
	 * @see #customDefaultConfig()
	 */

	private void defaultDeathMessages() {
		setupCustomDefaultVariables();
		if (!writeDeathMessages()) {
			log.info("Using internal Defaults!");
		}
		deathMessageFile = new File(plugin.getDataFolder(), deathMessageFileName);
		log.debug("deathMessageFile", deathMessageFile);

		try {
			deathMessageFileConfig.load(deathMessageFile);
			log.debug("deathMessageFileConfig", deathMessageFileConfig);
		} catch (IOException e) {
			log.debug("Error loading deathmessages file", e);
		} catch (InvalidConfigurationException e) {
			log.debug("Error in the deathmessages configuration", e);
		}
		deathMessageFileConfig.addDefault("deathMessagesVer", deathMessagesVer);
		customDefaultConfig();
	}


// Loading the configuration

	/**
	 * Method for loading the configuration from disk.
	 * First we get the deathMessageFileConfig object from disk, than we
	 * read in the standard configuration part.
	 * We also log a message if #debugLogEnabled
	 * and we produce some debug logs.
	 * After that we load the custom configuration part #loadCustomConfig()
	 *
	 * @see #loadCustomConfig()
	 */

	private void loadDeathMessages() {

		// Starting to update the standard configuration
		deathMessagesVer = deathMessageFileConfig.getString("deathMessagesVer");
		// Debug OutPut NOW!
		log.debug("deathMessagesCurrent", deathMessagesCurrent);
		log.debug("deathMessagesVer", deathMessagesVer);
		setupCustomDefaultVariables();
		loadCustomConfig();

		log.info("Deathmessages v." + deathMessagesVer + " loaded.");
	}


//  Writing the deathMessageFileConfig file

	/**
	 * Method for writing the deathmessages file.
	 * First we write the standard configuration part, than we
	 * write the custom configuration part via #writeCustomConfig()
	 *
	 * @return true if writing the deathMessageFileConfig was successful
	 *
	 * @see #writeCustomDeathMessages(java.io.PrintWriter)
	 */

	private boolean writeDeathMessages() {
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
			OutputStream outputStream = new FileOutputStream(pluginPath + deathMessageFileName);
			stream = new PrintWriter(new OutputStreamWriter(outputStream, "utf-8"));
//Let's write our deathMessageFileConfig ;)
			stream.println("# " + pluginName + " " + pdfFile.getVersion() + " by " + authors);
			stream.println("#");
			stream.println("# Deathmessage File for " + pluginName + ".");
			stream.println("#");
			stream.println("# DeathMessages Version");
			stream.println("deathMessagesVer: \"" + deathMessagesVer + "\"");
			stream.println();

// Getting the custom deathMessageFileConfig information from the top of the class
			writeCustomDeathMessages(stream);

			stream.println();

			stream.close();

			success = true;

		} catch (FileNotFoundException e) {
			log.warning("Error saving the " + deathMessageFileName + ".");
		} catch (UnsupportedEncodingException e) {
			log.warning("Error saving the " + deathMessageFileName + ".");
		}
		log.debug("Default DeathMessages written", success);
		return success;
	}


// Checking if the deathMessagesVersions differ

	/**
	 * Method to check if the configuration version are different.
	 * will set #deathMessagesRequiresUpdate to true if versions are different
	 */
	private void updateNecessary() {
		if (deathMessagesVer.equalsIgnoreCase(deathMessagesCurrent)) {
			log.informational("Deathmessages are up to date");
		} else {
			log.warning("Deathmessages are not up to date!");
			log.warning("Deathmessages File Version: " + deathMessagesVer);
			log.warning("Internal Deathmessages Version: " + deathMessagesCurrent);
			log.warning("It is suggested to update the deathMessageFileConfig.yml!");
			deathMessagesRequiresUpdate = true;
		}
	}


// Updating the deathMessageFileConfig

	/**
	 * Method to update the configuration if it is necessary.
	 */
	private void updateDeathMessages() {
		if (deathMessagesRequiresUpdate) {
			deathMessagesVer = deathMessagesCurrent;
			if (writeDeathMessages()) {
				log.info("Deathmessages were updated with new default values.");
				log.info("Please change them to your liking.");
			} else {
				log.warning("Deathmessages file could not be auto updated.");
				log.warning("Please rename " + deathMessageFileName + " and try again.");
			}
		}
	}

// Reloading the deathMessageFileConfig

	/**
	 * Method to reload the configuration.
	 *
	 * @return msg with the status of the reload
	 */

	public String reloadDeathMessages() {
		String msg;
		if (deathMessagesAvailable) {
			loadDeathMessages();
			log.info("Deathmessages reloaded");
			msg = "Deathmessages were reloaded";
		} else {
			log.severe("Reloading Deathmessages before they exists.");
			log.severe("Flog the developer!");
			msg = "Something terrible terrible did go really really wrong, see console log!";
		}
		return msg;
	}
// Saving the deathMessageFileConfig


	/**
	 * Method to save the deathMessageFileConfig to file.
	 *
	 * @return true if the save was successful
	 */
	public boolean saveDeathMessages() {
		boolean saved = false;
		if (config.isSaveConfig()) {
			saved = writeDeathMessages();
		}
		return saved;
	}

}
