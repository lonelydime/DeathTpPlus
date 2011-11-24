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
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.model.ProtectionTypes;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.commands.*;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombMessagesDTP;
import org.simiancage.DeathTpPlus.listeners.*;
import org.simiancage.DeathTpPlus.objects.TombBlockDTP;
import org.simiancage.DeathTpPlus.workers.TombStoneThreadDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;
import org.yi.acru.bukkit.Lockette.Lockette;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

//Register
//craftirc
// importing commands and listeners
//importing Lockette
// importing LWC

public class DeathTpPlus extends JavaPlugin{
    // listeners

    private EntityListenerDTP entityListener;

    private BlockListenerDTP blockListener;

    private ServerListenerDTP serverListener;

    private PlayerListenerDTP playerListener;

    // private WorldSaveListenerDTP worldSaveListener;

    // Enum

    public enum DeathTypes {FALL, DROWNING, SUFFOCATION, FIRE_TICK, FIRE, LAVA, BLOCK_EXPLOSION, CREEPER, SKELETON, SPIDER, PIGZOMBIE, ZOMBIE, CONTACT, SLIME, VOID, GHAST, WOLF, LIGHTNING, STARVATION, CAVESPIDER, ENDERMAN, SILVERFISH, PVP, FISTS, UNKNOWN, SUICIDE;

        @Override public String toString() {
            //only capitalize the first letter
            String s = super.toString();
            return s.substring(0, 1)+s.substring(1).toLowerCase();
        }
    }

    private ConfigDTP config;
    private LoggerDTP log;
    private DeathMessagesDTP deathMessages;
    private TombMessagesDTP tombMessages;





    //plugin variables

    private DeathTpPlus plugin = this;
    public static File locsName;
    public static File streakFile;
    public static File deathlogFile;
    protected static String pluginPath;
    protected static PluginManager pm;
    private boolean worldTravel = false;
    private FileConfiguration configuration;
    public LWCPlugin lwcPlugin = null;
    public Lockette LockettePlugin = null;
    public ConcurrentLinkedQueue<TombBlockDTP> tombListDTP = new ConcurrentLinkedQueue<TombBlockDTP>();
    public HashMap<Location, TombBlockDTP> tombBlockList = new HashMap<Location, TombBlockDTP>();
    public HashMap<String, ArrayList<TombBlockDTP>> playerTombList = new HashMap<String, ArrayList<TombBlockDTP>>();
    protected HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();
    public boolean economyActive = false;
    private static Server server = null;

    // Vault
    public boolean useVault = false;
    public Economy economy = null;

    //craftirc
    public static CraftIRC craftircHandle = null;

    public void onDisable() {
        for (World w : getServer().getWorlds())
        {
            saveTombStoneList(w.getName());
        }
        if (config.isEnableTomb()){
            TombWorkerDTP.getInstance().save();
            server.getScheduler().cancelTasks(this);
           }
        log.disableMsg();
    }

    public void onEnable() {
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
        pluginPath = getDataFolder() + System.getProperty("file.separator");
        locsName = new File(pluginPath+"locs.txt");
        streakFile = new File(pluginPath+"streak.txt");
        deathlogFile = new File(pluginPath+"deathlog.txt");
        pm = this.getServer().getPluginManager();
        if (!locsName.exists()) {
            CreateDefaultFile(locsName);
        }
        if (!streakFile.exists()) {
            CreateDefaultFile(streakFile);
        }
        if (!deathlogFile.exists()) {
            CreateDefaultFile(deathlogFile);
        }
        log.info( deathMessages.getKillstreak().get("KILL_STREAK").size()+" Kill Streaks loaded.");
        log.info( deathMessages.getDeathstreak().get("DEATH_STREAK").size()+" Death Streaks loaded.");
         if ( config.isRemoveTombStoneWhenEmpty())
        {
            log.warning("RemoveWhenEmpty is enabled. This is processor intensive!");
        }
        if ( config.isKeepTombStoneUntilEmpty())
        {
            log.warning("KeepUntilEmpty is enabled. This is processor intensive!");
        }
         if ( config.getAllowWorldTravel().equalsIgnoreCase("yes"))
        {
            worldTravel = true;
        }
         if (config.getAllowWorldTravel().equalsIgnoreCase("yes")||config.getAllowWorldTravel().equalsIgnoreCase("no")||config.getAllowWorldTravel().equalsIgnoreCase("permissions"))
        {
            log.info("allow-wordtravel is: "+ config.getAllowWorldTravel());
        } else {
            log.warning("Wrong allow-worldtravel value of "+config.getAllowWorldTravel()+". Defaulting to NO!");
            worldTravel = false;
        }
//Create the pluginmanager pm.
        PluginManager pm = getServer().getPluginManager();

// register entityListener
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);

        // register entityListener for Deathnotify
        if (config.isShowDeathNotify()) {
            pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        }
        // register entityListener for Deathnotify , Show Streaks  or TombDTP
        if (config.isShowDeathNotify() || config.isShowStreaks() || config.isEnableTomb()) {
            pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        }
        //register entityListener for Enable Tombstone or Enable TombDTP
        if (config.isEnableTombStone() || config.isEnableTomb())
        {
            pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
            lwcPlugin = (LWCPlugin) checkPlugin("LWC");
            LockettePlugin = (Lockette) checkPlugin("Lockette");
        }
         // register entityListener for Enable TombDTP
        if (config.isEnableTomb())
        {
            pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Highest, this);
            pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
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

        if (config.isRemoveTombStoneSecurity() || config.isRemoveTombStone())
        {
            getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new TombStoneThreadDTP(plugin), 0L, 100L);
        }

        // registering commands

        this.addCommands();

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
            // Todo Enable Logger
            log.warning("Cannot create file "+file.getPath()+"/"+file.getName());
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
            log.info("Found " + plugin.getDescription().getName()
                    + " (v" + plugin.getDescription().getVersion() + ")");
            if (config.isEnableLockette())
            {
                log.info("configured to use Lockette");
            }
            if (config.isEnableLWC())
            {
                log.info("configured to use LWC");
            }
            return plugin;
        }
        return null;
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

    public static File getLocsName() {
        return locsName;
    }

    public static File getStreakFile() {
        return streakFile;
    }

    public static File getDeathlogFile() {
        return deathlogFile;
    }

    public boolean isWorldTravel() {
        return worldTravel;
    }

    public ConcurrentLinkedQueue<TombBlockDTP> getTombListDTP() {
        return tombListDTP;
    }

    public HashMap<Location, TombBlockDTP> getTombBlockList() {
        return tombBlockList;
    }

    public HashMap<String, ArrayList<TombBlockDTP>> getPlayerTombList() {
        return playerTombList;
    }

    public HashMap<String, EntityDamageEvent> getDeathCause() {
        return deathCause;
    }

    public boolean isEconomyActive() {
        return economyActive;
    }

    // Load Tomblist
    void loadTombList(String world) {
        if (!config.isSaveTombStoneList())
        {
            return;
        }
        try {
            File fh = new File(this.getDataFolder().getPath(), "tombListDTP-"
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
                    log.warning("Invalid entry in database "
                            + fh.getName());
                    continue;
                }
                TombBlockDTP tBlockDTP = new TombBlockDTP(block, lBlock, sign, owner,
                        time, lwc);
                tombListDTP.offer(tBlockDTP);
                // Used for quick tombStone lookup
                tombBlockList.put(block.getLocation(), tBlockDTP);
                if (lBlock != null)
                    tombBlockList.put(lBlock.getLocation(), tBlockDTP);
                if (sign != null)
                    tombBlockList.put(sign.getLocation(), tBlockDTP);
                ArrayList<TombBlockDTP> pList = playerTombList.get(owner);
                if (pList == null) {
                    pList = new ArrayList<TombBlockDTP>();
                    playerTombList.put(owner, pList);
                }
                pList.add(tBlockDTP);
            }
            scanner.close();
            log.info("Successfully loaded TombStone list");
        } catch (IOException e) {
            log.warning("Error loading TombStone list", e);
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


    // Save TombStone List

    public void saveTombStoneList(String world) {
        if (!config.isSaveTombStoneList())
            return;
        try {
            File fh = new File(this.getDataFolder().getPath(), "tombListDTP-"
                    + world + ".db");
            BufferedWriter bw = new BufferedWriter(new FileWriter(fh));
            for (Iterator<TombBlockDTP> iter = tombListDTP.iterator(); iter.hasNext();) {
                TombBlockDTP tBlockDTP = iter.next();
                // Skip not this world
                if (!tBlockDTP.getBlock().getWorld().getName()
                        .equalsIgnoreCase(world))
                    continue;

                StringBuilder builder = new StringBuilder();

                bw.append(printBlock(tBlockDTP.getBlock()));
                bw.append(":");
                bw.append(printBlock(tBlockDTP.getLBlock()));
                bw.append(":");
                bw.append(printBlock(tBlockDTP.getSign()));
                bw.append(":");
                bw.append(tBlockDTP.getOwner());
                bw.append(":");
                bw.append(String.valueOf(tBlockDTP.getTime()));
                bw.append(":");
                bw.append(String.valueOf(tBlockDTP.getLwcEnabled()));

                bw.append(builder.toString());
                bw.newLine();
            }
            bw.close();
            log.info("Successfully saved TombStone list");
        } catch (IOException e) {
            log.warning("Error saving TombStone list" , e);
        }
    }

    private String printBlock(Block b) {
        if (b == null)
            return "";
        return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + ","
                + b.getZ();
    }


    //

    public Boolean activateLWC(Player player, TombBlockDTP tBlockDTP) {
        if (!config.isEnableLWC())
            return false;
        if (lwcPlugin == null)
            return false;
        LWC lwc = lwcPlugin.getLWC();

// Register the chest + sign as private
        Block block = tBlockDTP.getBlock();
        Block sign = tBlockDTP.getSign();
        lwc.getPhysicalDatabase().registerProtection(block.getTypeId(),
                ProtectionTypes.PRIVATE, block.getWorld().getName(),
                player.getName(), "", block.getX(), block.getY(), block.getZ());
        if (sign != null)
            lwc.getPhysicalDatabase()
                    .registerProtection(sign.getTypeId(),
                            ProtectionTypes.PRIVATE,
                            block.getWorld().getName(), player.getName(), "",
                            sign.getX(), sign.getY(), sign.getZ());

        tBlockDTP.setLwcEnabled(true);
        return true;
    }

    public Boolean protectWithLockette(Player player, TombBlockDTP tBlockDTP) {
        if (!config.isEnableLockette())
            return false;
        if (LockettePlugin == null)
            return false;

        Block signBlock = null;

        signBlock = findPlace(tBlockDTP.getBlock(), true);
        if (signBlock == null) {
            sendMessage(player, "No room for Lockette sign! Chest unsecured!");
            return false;
        }

        signBlock.setType(Material.AIR); // hack to prevent oddness with signs
// popping out of the ground as of
// Bukkit 818
        signBlock.setType(Material.WALL_SIGN);

        String facing = getDirection((getYawTo(signBlock.getLocation(), tBlockDTP
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
        tBlockDTP.setLocketteSign(sign);
        return true;
    }



    public void deactivateLWC(TombBlockDTP tBlockDTP, boolean force) {
        if (!config.isEnableLWC())
            return;
        if (lwcPlugin == null)
            return;
        LWC lwc = lwcPlugin.getLWC();

// Remove the protection on the chest
        Block _block = tBlockDTP.getBlock();
        Protection protection = lwc.findProtection(_block);
        if (protection != null) {
            lwc.getPhysicalDatabase().unregisterProtection(protection.getId());
// Set to public instead of removing completely
            if (config.isLwcPublic() && !force)
                lwc.getPhysicalDatabase().registerProtection(
                        _block.getTypeId(), ProtectionTypes.PUBLIC,
                        _block.getWorld().getName(), tBlockDTP.getOwner(), "",
                        _block.getX(), _block.getY(), _block.getZ());
        }

// Remove the protection on the sign
        _block = tBlockDTP.getSign();
        if (_block != null) {
            protection = lwc.findProtection(_block);
            if (protection != null) {
                protection.remove();
// Set to public instead of removing completely
                if (config.isLwcPublic() && !force)
                    lwc.getPhysicalDatabase().registerProtection(
                            _block.getTypeId(), ProtectionTypes.PUBLIC,
                            _block.getWorld().getName(), tBlockDTP.getOwner(), "",
                            _block.getX(), _block.getY(), _block.getZ());
            }
        }
        tBlockDTP.setLwcEnabled(false);
    }

    public void deactivateLockette(TombBlockDTP tBlockDTP) {
        if (tBlockDTP.getLocketteSign() == null)
            return;
        tBlockDTP.getLocketteSign().getBlock().setType(Material.AIR);
        tBlockDTP.removeLocketteSign();
    }

    public void removeTomb(TombBlockDTP tBlockDTP, boolean removeList) {
        if (tBlockDTP == null)
            return;

        tombBlockList.remove(tBlockDTP.getBlock().getLocation());
        if (tBlockDTP.getLBlock() != null)
            tombBlockList.remove(tBlockDTP.getLBlock().getLocation());
        if (tBlockDTP.getSign() != null)
            tombBlockList.remove(tBlockDTP.getSign().getLocation());

// Remove just this tomb from tombListDTP
        ArrayList<TombBlockDTP> tList = playerTombList.get(tBlockDTP.getOwner());
        if (tList != null) {
            tList.remove(tBlockDTP);
            if (tList.size() == 0) {
                playerTombList.remove(tBlockDTP.getOwner());
            }
        }

        if (removeList)
            tombListDTP.remove(tBlockDTP);

        if (tBlockDTP.getBlock() != null)
            saveTombStoneList(tBlockDTP.getBlock().getWorld().getName());
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

            log.warning("This command cannot be used in console.");
            return false;
        } else {
            if (sender.isOp())
                return true;

            return perm;
        }
    }

    boolean console(CommandSender sender) {
        return !(sender instanceof Player);
    }

    public void sendMessage(Player p, String msg) {
        if (!config.isShowTombStoneStatusMessage())
            return;
        p.sendMessage(msg);
    }

    public void sendMessage(CommandSender p, String msg) {
        if (!config.isShowTombStoneStatusMessage())
            return;
        p.sendMessage(msg);
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

    public void destroyTombStone(TombBlockDTP tBlockDTP) {
        tBlockDTP.getBlock().getWorld().loadChunk(tBlockDTP.getBlock().getChunk());

        deactivateLWC(tBlockDTP, true);

        if (tBlockDTP.getSign() != null)
            tBlockDTP.getSign().setType(Material.AIR);
        deactivateLockette(tBlockDTP);
        tBlockDTP.getBlock().setType(Material.AIR);
        if (tBlockDTP.getLBlock() != null)
            tBlockDTP.getLBlock().setType(Material.AIR);

        removeTomb(tBlockDTP, true);

        Player p = getServer().getPlayer(tBlockDTP.getOwner());
        if (p != null)
            sendMessage(p, "Your tombstone has been destroyed!");
    }


/*    // Register Mesthod

    public Method getRegisterMethod(){
        try{
            return Methods.getMethod();
        } catch(NoClassDefFoundError err){
        } // ugly solution, I know ...
        return null;

    }*/
}
