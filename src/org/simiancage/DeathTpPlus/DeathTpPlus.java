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
 * Original Copyright (C) 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


//Register
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;


//craftirc
import com.ensifera.animosity.craftirc.CraftIRC;

// importing commands and listeners
import org.simiancage.DeathTpPlus.commands.*;
import org.simiancage.DeathTpPlus.listeners.DTPBlockListener;
import org.simiancage.DeathTpPlus.listeners.DTPEntityListener;
import org.simiancage.DeathTpPlus.listeners.DTPPlayerListener;
import org.simiancage.DeathTpPlus.listeners.DTPServerListener;

//importing Lockette
import org.yi.acru.bukkit.Lockette.Lockette;

// importing LWC

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.model.ProtectionTypes;

public class DeathTpPlus extends JavaPlugin{
    // listeners
    private final DTPEntityListener entityListener = new DTPEntityListener(this);
    private final DTPBlockListener blockListener = new DTPBlockListener(this);
    private final DTPServerListener serverListener = new DTPServerListener(this);
    private final DTPPlayerListener playerListener = new DTPPlayerListener(this);

    // Configuration Version and PluginVersion
    // ToDo Did I changed this after making changes to the config?
    // ToDo Did I also change /resources/deathtpplus.ver after making these changes?

    public String configCurrent = "2.00";
    public String configVer;



    //plugin variables
    public Logger log;
    private DeathTpPlus plugin = this;
    public static HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();
    public static HashMap<String, String> deathconfig = new HashMap<String, String>();
    protected static File configFile;
    public static File locsName;
    public static File streakFile;
    public static File deathlogFile;
    public static String logName;
    public static String warnLogName;
    protected static String pluginName;
    protected static String pluginVersion;
    protected static ArrayList<String> pluginAuthor;
    protected static String pluginPath;
    protected static PluginManager pm;
    protected boolean worldTravel = false;
    protected FileConfiguration configuration;
    public LWCPlugin lwcPlugin = null;
    public Lockette LockettePlugin = null;
    public ConcurrentLinkedQueue<DTPTombBlock> tombList = new ConcurrentLinkedQueue<DTPTombBlock>();
    public HashMap<Location, DTPTombBlock> tombBlockList = new HashMap<Location, DTPTombBlock>();
    public HashMap<String, ArrayList<DTPTombBlock>> playerTombList = new HashMap<String, ArrayList<DTPTombBlock>>();
    protected HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();
    public String[] signMessage = new String[] { "{name}", "RIP", "{date}","{time}" };
    // private DTPTomb plugin;

    // Vault
    public boolean useVault = false;
    public Economy economy = null;

    //Register
    public boolean useRegister = false;



    //craftirc
    public static CraftIRC craftircHandle = null;

    public void onDisable() {
        for (World w : getServer().getWorlds())
        {
            saveTombStoneList(w.getName());
        }
        log.info(logName + "Disabled");
    }

    public void onEnable() {
        log = Bukkit.getServer().getLogger();
        pluginName = getDescription().getName();
        warnLogName = "[" + pluginName + "] ";
        logName = "   " + warnLogName;
        pluginVersion = getDescription().getVersion();
        pluginAuthor = getDescription().getAuthors();
        pluginPath = getDataFolder() + System.getProperty("file.separator");
        configFile = new File(pluginPath+"config.yml");
        locsName = new File(pluginPath+"locs.txt");
        streakFile = new File(pluginPath+"streak.txt");
        deathlogFile = new File(pluginPath+"deathlog.txt");
        pm = this.getServer().getPluginManager();


        // Todo write Helper Class for this

        if (!configFile.exists()) {
            new File(getDataFolder().toString()).mkdir();
            try {
                JarFile jar = new JarFile("plugins" + System.getProperty("file.separator") +getDescription().getName() + ".jar");
                ZipEntry config = jar.getEntry("config.yml");
                InputStream in = new BufferedInputStream(jar.getInputStream(config));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
                int c;
                while((c = in.read()) != -1){
                    out.write(c);
                }
                out.flush();
                out.close();
                in.close();
                jar.close();
                log.info(logName + "Default config created successfully!");
            } catch (Exception e)  {
                log.warning(warnLogName + "Default config could not be created!");

            }
        }
        configuration  = this.getConfig();
        if (!locsName.exists()) {
            CreateDefaultFile(locsName);
        }

        if (!streakFile.exists()) {
            CreateDefaultFile(streakFile);
        }

        if (!deathlogFile.exists()) {
            CreateDefaultFile(deathlogFile);
        }

        DefaultConfiguration();
        //Death Event nodes
        deathevents.put("FALL", (List<String>) configuration.getList("fall"));
        deathevents.put("DROWNING", (List<String>) configuration.getList("drowning"));
        deathevents.put("FIRE", (List<String>) configuration.getList("fire"));
        deathevents.put("FIRE_TICK", (List<String>) configuration.getList("fire_tick"));
        deathevents.put("LAVA", (List<String>) configuration.getList("lava"));
        deathevents.put("BLOCK_EXPLOSION", (List<String>) configuration.getList("block_explosion"));
        deathevents.put("CREEPER", (List<String>) configuration.getList("creeper"));
        deathevents.put("SKELETON", (List<String>) configuration.getList("skeleton"));
        deathevents.put("SPIDER", (List<String>) configuration.getList("spider"));
        deathevents.put("ZOMBIE", (List<String>) configuration.getList("zombie"));
        deathevents.put("CONTACT", (List<String>) configuration.getList("contact"));
        deathevents.put("PIGZOMBIE", (List<String>) configuration.getList("pigzombie"));
        deathevents.put("GHAST", (List<String>) configuration.getList("ghast"));
        deathevents.put("SLIME", (List<String>) configuration.getList("slime"));
        deathevents.put("PVP", (List<String>) configuration.getList("pvp"));
        deathevents.put("FISTS", (List<String>) configuration.getList("pvp-fists"));
        deathevents.put("SUFFOCATION", (List<String>) configuration.getList("suffocation"));
        deathevents.put("VOID", (List<String>) configuration.getList("void"));
        deathevents.put("WOLF", (List<String>) configuration.getList("wolf"));
        deathevents.put("LIGHTNING", (List<String>) configuration.getList("lightning"));
        deathevents.put("SUICIDE", (List<String>) configuration.getList("suicide"));
        deathevents.put("UNKNOWN", (List<String>) configuration.getList("unknown"));
        deathevents.put("STARVATION", (List<String>) configuration.getList("starvation"));
        deathevents.put("CAVESPIDER", (List<String>) configuration.getList("cavespider"));
        deathevents.put("ENDERMAN", (List<String>) configuration.getList("enderman"));
        deathevents.put("SILVERFISH", (List<String>) configuration.getList("silverfish"));
        //Configuration nodes DeathTpPlus
        deathconfig.put("VERSION_CHECK", configuration.getString("versionCheck"));
        deathconfig.put("CONFIG_VER", configuration.getString("configVer"));
        deathconfig.put("SHOW_DEATHNOTIFY", configuration.getString("show-deathnotify"));
        deathconfig.put("ALLOW_DEATHTP", configuration.getString("allow-deathtp"));
        deathconfig.put("SHOW_STREAKS", configuration.getString("show-streaks"));
        deathconfig.put("CHARGE_ITEM_ID", configuration.getString("charge-item"));
        deathconfig.put("SHOW_SIGN", configuration.getString("show-sign"));
        deathconfig.put("ECONOMY_COST", configuration.getString("deathtp-cost"));
        deathconfig.put("CRAFT_IRC_TAG", configuration.getString("deathtp-tag"));
        deathconfig.put("DEATH_LOGS", configuration.getString("allow-deathlog"));
        deathconfig.put("WORLD_TRAVEL", configuration.getString("allow-worldtravel"));
        deathconfig.put("ENABLE_DEBUG", configuration.getString("enable-debug"));
        //Configuration nodes Tombstone
        deathconfig.put("ENABLE_TOMBSTONE", configuration.getString("enable-tombstone"));
        deathconfig.put("TOMBSTONESIGN", configuration.getString("TombStoneSign"));
        deathconfig.put("NO_DESTROY", configuration.getString("noDestroy"));
        deathconfig.put("PLAYER_MESSAGE", configuration.getString("playerMessage"));
        deathconfig.put("SAVE_TOMBSTONELIST", configuration.getString("saveTombStoneList"));
        deathconfig.put("NO_INTERFERE", configuration.getString("noInterfere"));
        deathconfig.put("VOIDCHECK", configuration.getString("voidCheck"));
        deathconfig.put("CREEPER_PROTECTION", configuration.getString("creeperProtection"));
        signMessage = loadSign();
        deathconfig.put("DATE_FORMAT", configuration.getString("dateFormat"));
        deathconfig.put("TIME_FORMAT", configuration.getString("timeFormat"));
        deathconfig.put("DESTROY_QUICK_LOOT", configuration.getString("destroyQuickLoot"));
        deathconfig.put("TOMBSTONE_REMOVE", configuration.getString("TombStoneRemove"));
        deathconfig.put("REMOVE_TIME", configuration.getString("removeTime"));
        deathconfig.put("REMOVE_WHEN_EMPTY", configuration.getString("removeWhenEmpty"));
        deathconfig.put("KEEP_UNTIL_EMPTY", configuration.getString("keepUntilEmpty"));
        deathconfig.put("LOCKETTE_ENABLE", configuration.getString("LocketteEnable"));
        deathconfig.put("LWC_ENABLE", configuration.getString("lwcEnable"));
        deathconfig.put("SECURITY_REMOVE", configuration.getString("securityRemove"));
        deathconfig.put("SECURITY_TIMEOUT", configuration.getString("securityTimeout"));
        deathconfig.put("LWC_PUBLIC", configuration.getString("lwcPublic"));
        //Kill Streak nodes
        killstreak.put("KILL_STREAK", (List<String>) configuration.getList("killstreak"));
        //Death Streak nodes
        deathstreak.put("DEATH_STREAK", (List<String>) configuration.getList("deathstreak"));
        log.info(logName+killstreak.get("KILL_STREAK").size()+" Kill Streaks loaded.");
        log.info(logName+deathstreak.get("DEATH_STREAK").size()+" Death Streaks loaded.");

        if (deathconfig.get("REMOVE_WHEN_EMPTY").equalsIgnoreCase("true"))
        {
            log.warning(warnLogName +"RemoveWhenEmpty is enabled. This is processor intensive!");
        }

        if (deathconfig.get("KEEP_UNTIL_EMPTY").equalsIgnoreCase("true"))
        {
            log.warning(warnLogName +"KeepUntilEmpty is enabled. This is processor intensive!");
        }

        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes"))
        {
            worldTravel = true;
        }

        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes")||deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("no")||deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions"))
        {
            log.info(logName + "allow-wordtravel is: "+deathconfig.get("WORLD_TRAVEL"));
        } else {
            log.warning(warnLogName + "Wrong allow-worldtravel value of "+deathconfig.get("WORLD_TRAVEL")+". Defaulting to NO!");
            worldTravel = false;
        }
        configVer = configVer();
        if (deathconfig.get("VERSION_CHECK").equalsIgnoreCase("true"))
        {
            if (!(configVer.equalsIgnoreCase(configCurrent)))
            {
                log.warning(warnLogName + "Your config file is out of date! Rename your config and reload to see the new options.");
                log.warning(warnLogName + "Proceeding using set options from config file and defaults for new options...");
            }

            versionCheck(true);
        }



        //Create the pluginmanager pm.
        PluginManager pm = getServer().getPluginManager();

        // register entityListener
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);

        // register entityListener for Deathnotify
        if (deathconfig.get("SHOW_DEATHNOTIFY").equalsIgnoreCase("true")) {
            pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        }
        // register entityListener for Deathnotify and Show Streaks
        if (deathconfig.get("SHOW_DEATHNOTIFY").equalsIgnoreCase("true") || deathconfig.get("SHOW_STREAKS").equalsIgnoreCase("true") ) {
            pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        }
        // register entityListener for Enable Tombstone
        if (deathconfig.get("ENABLE_TOMBSTONE").equalsIgnoreCase("true"))
        {
            pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
            lwcPlugin = (LWCPlugin) checkPlugin("LWC");
            LockettePlugin = (Lockette) checkPlugin("Lockette");
        }

        //Register Server Listener
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new DTPServerListener(this), Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new DTPServerListener(this), Priority.Monitor, this);


        //craftirc
        Plugin checkCraftIRC = this.getServer().getPluginManager().getPlugin("CraftIRC");
        if (checkCraftIRC != null) {
            try {
                craftircHandle = (CraftIRC) checkCraftIRC;
                //Todo Enable Logger
                log.info(logName+"CraftIRC Support Enabled.");
            }
            catch (ClassCastException ex) {
            }
        }

        // reading in Tomblist

        for (World w : getServer().getWorlds())
        {
            loadTombList(w.getName());
        }

        // starting Removal Thread

        if (deathconfig.get("SECURITY_REMOVE").equalsIgnoreCase("true") || deathconfig.get("TOMBSTONE_REMOVE").equalsIgnoreCase("true"))
        {
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new DTPTombThread(plugin), 0L, 100L);
        }

        // registering commands

        this.addCommands();

        // print success
        PluginDescriptionFile pdfFile = this.getDescription();
        // Todo Enable Logger
        log.info(logName + "version " + pdfFile.getVersion() + " is enabled!");
    }

    private void CreateDefaultFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            // Todo Enable Logger
            log.warning(warnLogName+ "Cannot create file "+file.getPath()+"/"+file.getName());
        }
    }

    private String[] loadSign() {
        String[] msg = signMessage;
        msg[0] = configuration.getString("Sign.Line1", signMessage[0]);
        msg[1] = configuration.getString("Sign.Line2", signMessage[1]);
        msg[2] = configuration.getString("Sign.Line3", signMessage[2]);
        msg[3] = configuration.getString("Sign.Line4", signMessage[3]);
        return msg;
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
            log.info(logName +"Found " + plugin.getDescription().getName()
                    + " (v" + plugin.getDescription().getVersion() + ")");
            if (locketteEnable())
            {
                log.info(logName + "configured to use Lockette");
            }
            if (lwcEnable())
            {
                log.info(logName + "configured to use LWC");
            }
            return plugin;
        }
        return null;
    }

    // Load Tomblist
    public void loadTombList(String world) {
        if (!deathconfig.get("SAVE_TOMBSTONELIST").equals("true"))
        {
            return;
        }
        try {
            File fh = new File(this.getDataFolder().getPath(), "tombList-"
                    + world + ".db");
            if (!fh.exists())
                return;
            Scanner scanner = new Scanner(fh);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] split = line.split(":");
                // block:lblock:sign:time:name:lwc
                Block block = readBlock(split[0]);
                Block lBlock = readBlock(split[1]);
                Block sign = readBlock(split[2]);
                String owner = split[3];
                long time = Long.valueOf(split[4]);
                boolean lwc = Boolean.valueOf(split[5]);
                if (block == null || owner == null) {
                    log.info(logName + "Invalid entry in database "
                            + fh.getName());
                    continue;
                }
                DTPTombBlock tBlock = new DTPTombBlock(block, lBlock, sign, owner,
                        time, lwc);
                tombList.offer(tBlock);
                // Used for quick tombStone lookup
                tombBlockList.put(block.getLocation(), tBlock);
                if (lBlock != null)
                    tombBlockList.put(lBlock.getLocation(), tBlock);
                if (sign != null)
                    tombBlockList.put(sign.getLocation(), tBlock);
                ArrayList<DTPTombBlock> pList = playerTombList.get(owner);
                if (pList == null) {
                    pList = new ArrayList<DTPTombBlock>();
                    playerTombList.put(owner, pList);
                }
                pList.add(tBlock);
            }
            scanner.close();
        } catch (IOException e) {
            log.info(logName + "Error loading tombstone list: " + e);
        }
    }

    private Block readBlock(String b) {
        if (b.length() == 0)
            return null;
        String[] split = b.split(",");
        // world,x,y,z
        World world = getServer().getWorld(split[0]);
        if (world == null)
            return null;
        return world.getBlockAt(Integer.valueOf(split[1]),
                Integer.valueOf(split[2]), Integer.valueOf(split[3]));
    }

    // Default Configuration

    private void DefaultConfiguration() {
        configuration.addDefault("fall", "");
        configuration.addDefault("drowning", "");
        configuration.addDefault("fire", "");
        configuration.addDefault("fire_tick", "");
        configuration.addDefault("lava", "");
        configuration.addDefault("block_explosion", "");
        configuration.addDefault("creeper", "");
        configuration.addDefault("skeleton", "");
        configuration.addDefault("spider", "");
        configuration.addDefault("zombie", "");
        configuration.addDefault("contact", "");
        configuration.addDefault("pigzombie", "");
        configuration.addDefault("ghast", "");
        configuration.addDefault("slime", "");
        configuration.addDefault("pvp", "");
        configuration.addDefault("pvp-fists", "");
        configuration.addDefault("suffocation", "");
        configuration.addDefault("void", "");
        configuration.addDefault("wolf", "");
        configuration.addDefault("lightning", "");
        configuration.addDefault("suicide", "");
        configuration.addDefault("unknown", "");
        configuration.addDefault("starvation", "");
        configuration.addDefault("cavespider", "");
        configuration.addDefault("enderman", "");
        configuration.addDefault("silverfish", "");
        //Configuration nodes
        configuration.addDefault("configVer", "0.00");
        configuration.addDefault("versionCheck", "true");
        configuration.addDefault("show-deathnotify", "true");
        configuration.addDefault("allow-deathtp", "true");
        configuration.addDefault("show-streaks", "true");
        configuration.addDefault("charge-item", "0");
        configuration.addDefault("show-sign", "false");
        configuration.addDefault("deathtp-cost", "0");
        configuration.addDefault("deathtp-tag", "");
        configuration.addDefault("allow-deathlog", "true");
        configuration.addDefault("allow-worldtravel", "no");
        configuration.addDefault("logevents","false");
        configuration.addDefault("enable-tombstone", "true");
        configuration.addDefault("enable-debug", "true");
        //Kill Streak nodes
        configuration.addDefault("killstreak", "");
        //Death Streak nodes
        configuration.addDefault("deathstreak", "");
        // TombStone nodes
        configuration.addDefault("TombStoneSign","true");
        configuration.addDefault("noDestroy","false");
        configuration.addDefault("saveTombStoneList","true");
        configuration.addDefault("playerMessage","true");
        configuration.addDefault("noInterfere","true");
        configuration.addDefault("voidCheck","true");
        configuration.addDefault("creeperProtection","true");
        configuration.addDefault("dateFormat","MM/dd/yyyy");
        configuration.addDefault("timeFormat","hh:mm a");
        configuration.addDefault("destroyQuickloot","true");
        configuration.addDefault("TombStoneRemove","false");
        configuration.addDefault("removeTime","18000");
        configuration.addDefault("removeWhenEmpty","false");
        configuration.addDefault("keepUntilEmpty","false");
        configuration.addDefault("LocketteEnable","true");
        configuration.addDefault("lwcEnable","false");
        configuration.addDefault("securityRemove","false");
        configuration.addDefault("securityTimeout","3600");
        configuration.addDefault("lwcPublic","false");
    }






    public String convertSamloean(String convert) {
        convert = convert.replace("&0", "§0");
        convert = convert.replace("&1", "§1");
        convert = convert.replace("&2", "§2");
        convert = convert.replace("&3", "§3");
        convert = convert.replace("&4", "§4");
        convert = convert.replace("&5", "§5");
        convert = convert.replace("&6", "§6");
        convert = convert.replace("&7", "§7");
        convert = convert.replace("&8", "§8");
        convert = convert.replace("&9", "§9");
        convert = convert.replace("&a", "§a");
        convert = convert.replace("&b", "§b");
        convert = convert.replace("&c", "§c");
        convert = convert.replace("&d", "§d");
        convert = convert.replace("&e", "§e");
        convert = convert.replace("&f", "§f");

        return convert;
    }


    // Register commands

    private void addCommands() {
        getCommand("dtplist").setExecutor(new DTPListCommand(this));
        getCommand("dtpfind").setExecutor(new DTPFindCommand(this));
        getCommand("dtptime").setExecutor(new DTPTimeCommand(this));
        getCommand("dtpreset").setExecutor(new DTPResetCommand(this));
        getCommand("dtpadmin").setExecutor(new DTPAdminCommand(this));
        getCommand("deathtp").setExecutor(new DTPDeathtpCommand(this));
        getCommand("deaths").setExecutor(new DTPDeathsCommand(this));
        getCommand("kills").setExecutor(new DTPKillsCommand(this));
        getCommand("streak").setExecutor(new DTPStreakCommand(this));
    }


    // Save TombStone List

    public void saveTombStoneList(String world) {
        if (!deathconfig.get("SAVE_TOMBSTONELIST").equalsIgnoreCase("true"))
            return;
        try {
            File fh = new File(this.getDataFolder().getPath(), "tombList-"
                    + world + ".db");
            BufferedWriter bw = new BufferedWriter(new FileWriter(fh));
            for (Iterator<DTPTombBlock> iter = tombList.iterator(); iter.hasNext();) {
                DTPTombBlock tBlock = iter.next();
                // Skip not this world
                if (!tBlock.getBlock().getWorld().getName()
                        .equalsIgnoreCase(world))
                    continue;

                StringBuilder builder = new StringBuilder();

                bw.append(printBlock(tBlock.getBlock()));
                bw.append(":");
                bw.append(printBlock(tBlock.getLBlock()));
                bw.append(":");
                bw.append(printBlock(tBlock.getSign()));
                bw.append(":");
                bw.append(tBlock.getOwner());
                bw.append(":");
                bw.append(String.valueOf(tBlock.getTime()));
                bw.append(":");
                bw.append(String.valueOf(tBlock.getLwcEnabled()));

                bw.append(builder.toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            log.info(logName + "Error saving TombStone list: " + e);
        }
    }

    private String printBlock(Block b) {
        if (b == null)
            return "";
        return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + ","
                + b.getZ();
    }


    //

    public Boolean activateLWC(Player player, DTPTombBlock tBlock) {
        if (!deathconfig.get("LWC_ENABLE").equalsIgnoreCase("true"))
            return false;
        if (lwcPlugin == null)
            return false;
        LWC lwc = lwcPlugin.getLWC();

// Register the chest + sign as private
        Block block = tBlock.getBlock();
        Block sign = tBlock.getSign();
        lwc.getPhysicalDatabase().registerProtection(block.getTypeId(),
                ProtectionTypes.PRIVATE, block.getWorld().getName(),
                player.getName(), "", block.getX(), block.getY(), block.getZ());
        if (sign != null)
            lwc.getPhysicalDatabase()
                    .registerProtection(sign.getTypeId(),
                            ProtectionTypes.PRIVATE,
                            block.getWorld().getName(), player.getName(), "",
                            sign.getX(), sign.getY(), sign.getZ());

        tBlock.setLwcEnabled(true);
        return true;
    }

    public Boolean protectWithLockette(Player player, DTPTombBlock tBlock) {
        if (!deathconfig.get("LOCKETTE_ENABLE").equalsIgnoreCase("true"))
            return false;
        if (LockettePlugin == null)
            return false;

        Block signBlock = null;

        signBlock = findPlace(tBlock.getBlock(), true);
        if (signBlock == null) {
            sendMessage(player, "No room for Lockette sign! Chest unsecured!");
            return false;
        }

        signBlock.setType(Material.AIR); // hack to prevent oddness with signs
// popping out of the ground as of
// Bukkit 818
        signBlock.setType(Material.WALL_SIGN);

        String facing = getDirection((getYawTo(signBlock.getLocation(), tBlock
                .getBlock().getLocation()) + 270) % 360);
        if (facing == "East")
            signBlock.setData((byte) 0x02);
        else if (facing == "West")
            signBlock.setData((byte) 0x03);
        else if (facing == "North")
            signBlock.setData((byte) 0x04);
        else if (facing == "South")
            signBlock.setData((byte) 0x05);
        else {
            sendMessage(player, "Error placing Lockette sign! Chest unsecured!");
            return false;
        }

        BlockState signBlockState = null;
        signBlockState = signBlock.getState();
        final Sign sign = (Sign) signBlockState;

        String name = player.getName();
        if (name.length() > 15)
            name = name.substring(0, 15);
        sign.setLine(0, "[Private]");
        sign.setLine(1, name);
        // Todo Check if replacing plugin with this is really the solution!!!
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
        {
            public void run() {
                sign.update();
            }
        });
        tBlock.setLocketteSign(sign);
        return true;
    }

    public void deactivateLWC(DTPTombBlock tBlock, boolean force) {
        if (!deathconfig.get("LWC_ENABLE").equalsIgnoreCase("true"))
            return;
        if (lwcPlugin == null)
            return;
        LWC lwc = lwcPlugin.getLWC();

// Remove the protection on the chest
        Block _block = tBlock.getBlock();
        Protection protection = lwc.findProtection(_block);
        if (protection != null) {
            lwc.getPhysicalDatabase().unregisterProtection(protection.getId());
// Set to public instead of removing completely
            if (deathconfig.get("LWC_PUBLIC").equalsIgnoreCase("true") && !force)
                lwc.getPhysicalDatabase().registerProtection(
                        _block.getTypeId(), ProtectionTypes.PUBLIC,
                        _block.getWorld().getName(), tBlock.getOwner(), "",
                        _block.getX(), _block.getY(), _block.getZ());
        }

// Remove the protection on the sign
        _block = tBlock.getSign();
        if (_block != null) {
            protection = lwc.findProtection(_block);
            if (protection != null) {
                protection.remove();
// Set to public instead of removing completely
                if (deathconfig.get("LWC_PUBLIC").equalsIgnoreCase("true") && !force)
                    lwc.getPhysicalDatabase().registerProtection(
                            _block.getTypeId(), ProtectionTypes.PUBLIC,
                            _block.getWorld().getName(), tBlock.getOwner(), "",
                            _block.getX(), _block.getY(), _block.getZ());
            }
        }
        tBlock.setLwcEnabled(false);
    }

    public void deactivateLockette(DTPTombBlock tBlock) {
        if (tBlock.getLocketteSign() == null)
            return;
        tBlock.getLocketteSign().getBlock().setType(Material.AIR);
        tBlock.removeLocketteSign();
    }

    public void removeTomb(DTPTombBlock tBlock, boolean removeList) {
        if (tBlock == null)
            return;

        tombBlockList.remove(tBlock.getBlock().getLocation());
        if (tBlock.getLBlock() != null)
            tombBlockList.remove(tBlock.getLBlock().getLocation());
        if (tBlock.getSign() != null)
            tombBlockList.remove(tBlock.getSign().getLocation());

// Remove just this tomb from tombList
        ArrayList<DTPTombBlock> tList = playerTombList.get(tBlock.getOwner());
        if (tList != null) {
            tList.remove(tBlock);
            if (tList.size() == 0) {
                playerTombList.remove(tBlock.getOwner());
            }
        }

        if (removeList)
            tombList.remove(tBlock);

        if (tBlock.getBlock() != null)
            saveTombStoneList(tBlock.getBlock().getWorld().getName());
    }

    /*
    * Check whether the player has the given permissions.
    */
    public boolean hasPerm(CommandSender sender, String label,
                           boolean consoleUse) {
        boolean perm = sender.hasPermission("deathtpplus." + label);

        if (this.console(sender)) {
            if (consoleUse)
                return true;

            log.info(logName + "This command cannot be used in console.");
            return false;
        } else {
            if (sender.isOp())
                return true;

            return perm;
        }
    }

    public boolean console(CommandSender sender) {
        if (sender instanceof Player) {
            return false;
        }
        return true;
    }

    public void sendMessage(Player p, String msg) {
        if (!deathconfig.get("PLAYER_MESSAGE").equalsIgnoreCase("true"))
            return;
        p.sendMessage(warnLogName + msg);
    }

    public void sendMessage(CommandSender p, String msg) {
        if (!deathconfig.get("PLAYER_MESSAGE").equalsIgnoreCase("true"))
            return;
        p.sendMessage(warnLogName + msg);
    }


    /**
     * Gets the Yaw from one location to another in relation to North.
     *
     */
    public double getYawTo(Location from, Location to) {
        final int distX = to.getBlockX() - from.getBlockX();
        final int distZ = to.getBlockZ() - from.getBlockZ();
        double degrees = Math.toDegrees(Math.atan2(-distX, distZ));
        degrees += 180;
        return degrees;
    }

    /**
     * Converts a rotation to a cardinal direction name. Author: sk89q -
     * Original function from CommandBook plugin
     *
     * @param rot
     * @return
     */
    public static String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North";
        } else {
            return null;
        }
    }

    /**
     *
     * Print a message to terminal if logEvents is enabled
     *
     * @param msg
     * @return
     *
     */
    public void logEvent(String msg) {
        if (!deathconfig.get("ENABLE_DEBUG").equalsIgnoreCase("true"))
            return;
        log.info(logName + msg);
    }

    /**
     * Find a block near the base block to place the tombstone
     *
     * @param base
     * @return
     */
    public Block findPlace(Block base, Boolean CardinalSearch) {
        if (canReplace(base.getType()))
            return base;
        int baseX = base.getX();
        int baseY = base.getY();
        int baseZ = base.getZ();
        World w = base.getWorld();

        if (CardinalSearch) {
            Block b;
            b = w.getBlockAt(baseX - 1, baseY, baseZ);
            if (canReplace(b.getType()))
                return b;
            b = w.getBlockAt(baseX + 1, baseY, baseZ);
            if (canReplace(b.getType()))
                return b;
            b = w.getBlockAt(baseX, baseY, baseZ - 1);
            if (canReplace(b.getType()))
                return b;
            b = w.getBlockAt(baseX, baseY, baseZ + 1);
            if (canReplace(b.getType()))
                return b;
            b = w.getBlockAt(baseX, baseY, baseZ);
            if (canReplace(b.getType()))
                return b;

            return null;
        }

        for (int x = baseX - 1; x < baseX + 1; x++) {
            for (int z = baseZ - 1; z < baseZ + 1; z++) {
                Block b = w.getBlockAt(x, baseY, z);
                if (canReplace(b.getType()))
                    return b;
            }
        }

        return null;
    }

    public Boolean canReplace(Material mat) {
        return (mat == Material.AIR || mat == Material.SAPLING
                || mat == Material.WATER || mat == Material.STATIONARY_WATER
                || mat == Material.LAVA || mat == Material.STATIONARY_LAVA
                || mat == Material.YELLOW_FLOWER || mat == Material.RED_ROSE
                || mat == Material.BROWN_MUSHROOM
                || mat == Material.RED_MUSHROOM || mat == Material.FIRE
                || mat == Material.CROPS || mat == Material.SNOW
                || mat == Material.SUGAR_CANE || mat == Material.GRAVEL || mat == Material.SAND);
    }

    public String convertTime(long s) { // TODO implement later
        long days = s / 86400;
        int hours = (int) (s % 86400 / 3600);
        int minutes = (int) (s % 86400 % 3600 / 60);
        int seconds = (int) (s % 86400 % 3600 % 60);
        return (days > 1 ? days : "") + (hours < 10 ? "0" : "") + hours + ":"
                + (minutes < 10 ? "0" : "") + minutes + ":"
                + (seconds < 10 ? "0" : "") + seconds;
    }

    public void destroyTombStone(Location loc) {
        destroyTombStone(tombBlockList.get(loc));
    }

    public void destroyTombStone(DTPTombBlock tBlock) {
        tBlock.getBlock().getWorld().loadChunk(tBlock.getBlock().getChunk());

        deactivateLWC(tBlock, true);

        if (tBlock.getSign() != null)
            tBlock.getSign().setType(Material.AIR);
        deactivateLockette(tBlock);
        tBlock.getBlock().setType(Material.AIR);
        if (tBlock.getLBlock() != null)
            tBlock.getLBlock().setType(Material.AIR);

        removeTomb(tBlock, true);

        Player p = getServer().getPlayer(tBlock.getOwner());
        if (p != null)
            sendMessage(p, "Your tombstone has been destroyed!");
    }

    public HashMap<String, ArrayList<DTPTombBlock>> getTombStoneList() {
        return playerTombList;
    }

    // ToDo Refactor the whole config design after making this work as the HashTable makes it arkward to get the values needed in the right format

    boolean logEvents () {
        return deathconfig.get("ENABLE_DEBUG").equalsIgnoreCase("true");

    }

    public boolean tombstoneSign() {
        return deathconfig.get("TOMBSTONESIGN").equalsIgnoreCase("true");
    }

    public boolean noDestroy() {
        return deathconfig.get("NO_DESTROY").equalsIgnoreCase("true");
    }

    boolean playerMessage () {
        return deathconfig.get("PLAYER_MESSAGE").equalsIgnoreCase("true");
    }

    public boolean saveTombstoneList () {
        return deathconfig.get("SAVE_TOMBSTONELIST").equalsIgnoreCase("true");
    }

    public boolean noInterfere() {
        return deathconfig.get("NO_INTERFERE").equalsIgnoreCase("true");
    }

    public boolean voidCheck() {
        return deathconfig.get("VOIDCHECK").equalsIgnoreCase("true");
    }

    public boolean creeperProtection() {
        return deathconfig.get("CREEPER_PROTECTION").equalsIgnoreCase("true");
    }

    public String dateFormat() {
        return deathconfig.get("DATE_FORMAT");
    }

    public String timeFormat() {
        return deathconfig.get("TIME_FORMAT");
    }

    public boolean destroyQuickLoot() {
        return deathconfig.get("DESTROY_QUICK_LOOT").equalsIgnoreCase("true");
    }

    public boolean tombstoneRemove() {
        return deathconfig.get("TOMBSTONE_REMOVE").equalsIgnoreCase("true");
    }

    public int removeTime() {
        return Integer.parseInt(deathconfig.get("REMOVE_TIME"));
    }

    public boolean removeWhenEmpty() {
        return deathconfig.get("REMOVE_WHEN_EMPTY").equalsIgnoreCase("true");
    }

    public boolean keepUntilEmpty() {
        return deathconfig.get("KEEP_UNTIL_EMPTY").equalsIgnoreCase("true");
    }

    boolean locketteEnable () {
        return deathconfig.get("LOCKETTE_ENABLE").equalsIgnoreCase("true");
    }

    public boolean lwcEnable() {
        return deathconfig.get("LWC_ENABLE").equalsIgnoreCase("true");
    }

    public boolean securityRemove() {
        return deathconfig.get("SECURITY_REMOVE").equalsIgnoreCase("true");
    }

    public int securityTimeout() {
        return Integer.parseInt(deathconfig.get("SECURITY_TIMEOUT"));
    }

    protected boolean lwcPublic () {
        return deathconfig.get("LWC_PUBLIC").equalsIgnoreCase("true");
    }

    public String configVer() {
        return deathconfig.get("CONFIG_VER");
    }

    public String versionCheck(Boolean printToLog) {
        String thisVersion = getDescription().getVersion();
        URL url = null;
        try {
            // ToDo change to correct URL after merging branches
            url = new URL("https://raw.github.com/dredhorse/DeathTpPlus/cenopath/Resources/deathtpplus.ver");
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String newVersion = "";
            String line;
            while ((line = in.readLine()) != null) {
                newVersion += line;
            }
            in.close();
            if (!newVersion.equals(thisVersion)) {
                if (printToLog)
                    log.warning(warnLogName + "DeathTpPlus is out of date!");
                    log.warning("This version: " + thisVersion + "; latest version: " + newVersion + ".");
                return "DeathTpPlus is out of date! This version: " + thisVersion
                        + "; latest version: " + newVersion + ".";
            } else {
                if (printToLog)
                    log.info(logName + "DeathTpPlus is up to date at version "
                            + thisVersion + ".");
                return "DeathTpPlus is up to date at version " + thisVersion + ".";
            }
        } catch (MalformedURLException ex) {
            if (printToLog)
                log.warning(warnLogName + "Error accessing update URL.");
            return "Error accessing update URL.";
        } catch (IOException ex) {
            if (printToLog)
                log.warning(warnLogName + "Error checking for update.");
            return "Error checking for update.";
        }
    }


    // Register Mesthod

    public Method getRegisterMethod(){
        try{
            return Methods.getMethod();
        } catch(NoClassDefFoundError err){
        } // ugly solution, I know ...
        return null;

    }
}
