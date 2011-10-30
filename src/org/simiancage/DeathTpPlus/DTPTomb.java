package org.simiancage.DeathTpPlus;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTomb
 * User: DonRedhorse
 * Date: 18.10.11
 * Time: 22:33
 * based on:
 * an updated fork of Furt https://github.com/Furt of
 * Cenopath - A Dead Man's Chest plugin for Bukkit
 * By Jim Drey (Southpaw018) <moof@moofit.com>
 * Original Copyright (C) 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.MoofIT.Minecraft.Cenotaph.Commands.*;
import com.MoofIT.Minecraft.Cenotaph.Listeners.CBlockListener;
import com.MoofIT.Minecraft.Cenotaph.Listeners.CEntityListener;
import com.MoofIT.Minecraft.Cenotaph.Listeners.CPlayerListener;
import com.MoofIT.Minecraft.Cenotaph.Listeners.CServerListener;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.model.ProtectionTypes;
import org.yi.acru.bukkit.Lockette.Lockette;

public class DTPTomb extends JavaPlugin {
private final CBlockListener blockListener = new CBlockListener(this);
private final CEntityListener entityListener = new CEntityListener(this);
private final CServerListener serverListener = new CServerListener(this);
private final CPlayerListener playerListener = new CPlayerListener(this);
public static Logger log;
PluginManager pm;

public LWCPlugin lwcPlugin = null;
public Lockette LockettePlugin = null;

public ConcurrentLinkedQueue<DTPTombBlock> tombList = new ConcurrentLinkedQueue<DTPTombBlock>();
public HashMap<Location, DTPTombBlock> tombBlockList = new HashMap<Location, DTPTombBlock>();
public HashMap<String, ArrayList<DTPTombBlock>> playerTombList = new HashMap<String, ArrayList<DTPTombBlock>>();
public HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();
private Configuration config;
private DTPTomb plugin;

/**
* Configuration options - Defaults
*/
private boolean logEvents = false;
public boolean cenotaphSign = true;
public boolean noDestroy = false;
private boolean pMessage = true;
private boolean saveCenotaphList = true;
public boolean noInterfere = true;
private boolean versionCheck = true;
public boolean voidCheck = true;
public boolean creeperProtection = false;
public String signMessage[] = new String[] { "{name}", "RIP", "{date}",
"{time}" };
public String dateFormat = "MM/dd/yyyy";
public String timeFormat = "hh:mm a";
public boolean destroyQuickLoot = false;
public boolean cenotaphRemove = false;
public int removeTime = 18000;
public boolean removeWhenEmpty = false;
public boolean keepUntilEmpty = false;
private boolean LocketteEnable = true;
public boolean lwcEnable = false;
public boolean securityRemove = false;
public int securityTimeout = 3600;
private boolean lwcPublic = false;
public TreeMap<String, Object> deathMessages = new TreeMap<String, Object>() {
private static final long serialVersionUID = 1L;
{
put("Monster.Zombie", "a Zombie");
put("Monster.Skeleton", "a Skeleton");
put("Monster.Spider", "a Spider");
put("Monster.Wolf", "a Wolf");
put("Monster.Creeper", "a Creeper");
put("Monster.Slime", "a Slime");
put("Monster.Ghast", "a Ghast");
put("Monster.PigZombie", "a Pig Zombie");
put("Monster.Giant", "a Giant");
put("Monster.Other", "a Monster");

put("World.Cactus", "a Cactus");
put("World.Suffocation", "Suffocation");
put("World.Fall", "a Fall");
put("World.Fire", "a Fire");
put("World.Burning", "Burning");
put("World.Lava", "Lava");
put("World.Drowning", "Drowning");
put("World.Lightning", "Lightning");

put("Explosion.Misc", "an Explosion");
put("Explosion.TNT", "a TNT Explosion");

put("Misc.Dispenser", "a Dispenser");
put("Misc.Void", "the Void");
put("Misc.Other", "Unknown");
}
};
public int configVer = 0;
public final int configCurrent = 12;

public void onEnable() {
PluginDescriptionFile pdfFile = getDescription();
log = Logger.getLogger("Minecraft");
config = this.getConfiguration();

String thisVersion = pdfFile.getVersion();
log.info(pdfFile.getName() + " v." + thisVersion + " is enabled.");

pm = getServer().getPluginManager();
pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener,
Priority.Normal, this);
pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener,
Priority.Normal, this);
pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener,
Priority.Monitor, this);
pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
Priority.Normal, this);
pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
Priority.Highest, this);
pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener,
Priority.Monitor, this);
pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener,
Priority.Monitor, this);

lwcPlugin = (LWCPlugin) checkPlugin("LWC");
LockettePlugin = (Lockette) checkPlugin("Lockette");
plugin = this;

loadConfig();
for (World w : getServer().getWorlds())
loadTombList(w.getName());

if (versionCheck) {
versionCheck(true);
}

// Start removal timer. Run every 5 seconds (20 ticks per second)
if (securityRemove || cenotaphRemove)
getServer().getScheduler().scheduleSyncRepeatingTask(this,
new DTPTombThread(plugin), 0L, 100L);
this.addCommands();
}

public void loadConfig() {
config.load();

configVer = config.getInt("configVer", configVer);
if (configVer == 0) {
try {
log.info("[DTPTomb] Configuration error or no config file found. Downloading default config file...");
if (!new File(getDataFolder().toString()).exists()) {
new File(getDataFolder().toString()).mkdir();
}
URL config = new URL(
"https://raw.github.com/Southpaw018/DTPTomb/master/config.yml");
ReadableByteChannel rbc = Channels.newChannel(config
.openStream());
FileOutputStream fos = new FileOutputStream(this
.getDataFolder().getPath() + "/config.yml");
fos.getChannel().transferFrom(rbc, 0, 1 << 24);
} catch (MalformedURLException ex) {
log.warning("[DTPTomb] Error accessing default config file URL: "
+ ex);
} catch (FileNotFoundException ex) {
log.warning("[DTPTomb] Error accessing default config file URL: "
+ ex);
} catch (IOException ex) {
log.warning("[DTPTomb] Error downloading default config file: "
+ ex);
}

} else if (configVer < configCurrent) {
log.warning("[DTPTomb] Your config file is out of date! Delete your config and reload to see the new options. Proceeding using set options from config file and defaults for new options...");
}

// Core
logEvents = config.getBoolean("Core.logEvents", logEvents);
cenotaphSign = config.getBoolean("Core.cenotaphSign", cenotaphSign);
noDestroy = config.getBoolean("Core.noDestroy", noDestroy);
pMessage = config.getBoolean("Core.playerMessage", pMessage);
saveCenotaphList = config.getBoolean("Core.saveCenotaphList",
saveCenotaphList);
noInterfere = config.getBoolean("Core.noInterfere", noInterfere);
versionCheck = config.getBoolean("Core.versionCheck", versionCheck);
voidCheck = config.getBoolean("Core.voidCheck", voidCheck);
creeperProtection = config.getBoolean("Core.creeperProtection",
creeperProtection);
signMessage = loadSign();
dateFormat = config.getString("Core.Sign.dateFormat", dateFormat);
timeFormat = config.getString("Core.Sign.timeFormat", timeFormat);

// Removal
destroyQuickLoot = config.getBoolean("Removal.destroyQuickLoot",
destroyQuickLoot);
cenotaphRemove = config.getBoolean("Removal.cenotaphRemove",
cenotaphRemove);
removeTime = config.getInt("Removal.removeTime", removeTime);
removeWhenEmpty = config.getBoolean("Removal.removeWhenEmpty",
removeWhenEmpty);
keepUntilEmpty = config.getBoolean("Removal.keepUntilEmpty",
keepUntilEmpty);

// Security
LocketteEnable = config.getBoolean("Security.LocketteEnable",
LocketteEnable);
lwcEnable = config.getBoolean("Security.lwcEnable", lwcEnable);
securityRemove = config.getBoolean("Security.securityRemove",
securityRemove);
securityTimeout = config.getInt("Security.securityTimeout",
securityTimeout);
lwcPublic = config.getBoolean("Security.lwcPublic", lwcPublic);

// DeathMessages
try {
deathMessages = (TreeMap<String, Object>) config.getNode(
"DeathMessages").getAll();
} catch (NullPointerException e) {
log.warning("[DTPTomb] Configuration failure while loading deathMessages. Using defaults.");
}
}

public void loadTombList(String world) {
if (!saveCenotaphList)
return;
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
log.info("[DTPTomb] Invalid entry in database "
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
log.info("[DTPTomb] Error loading cenotaph list: " + e);
}
}

public void saveCenotaphList(String world) {
if (!saveCenotaphList)
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
log.info("[DTPTomb] Error saving cenotaph list: " + e);
}
}

private String printBlock(Block b) {
if (b == null)
return "";
return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + ","
+ b.getZ();
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

public void onDisable() {
for (World w : getServer().getWorlds())
saveCenotaphList(w.getName());
}

private String[] loadSign() {
String[] msg = signMessage;
msg[0] = config.getString("Core.Sign.Line1", signMessage[0]);
msg[1] = config.getString("Core.Sign.Line2", signMessage[1]);
msg[2] = config.getString("Core.Sign.Line3", signMessage[2]);
msg[3] = config.getString("Core.Sign.Line4", signMessage[3]);
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
log.info("[DTPTomb] Using " + plugin.getDescription().getName()
+ " (v" + plugin.getDescription().getVersion() + ")");
return plugin;
}
return null;
}

public Boolean activateLWC(Player player, DTPTombBlock tBlock) {
if (!lwcEnable)
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
if (!LocketteEnable)
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
getServer().getScheduler().scheduleSyncDelayedTask(plugin,
new Runnable() {
public void run() {
sign.update();
}
});
tBlock.setLocketteSign(sign);
return true;
}

public void deactivateLWC(DTPTombBlock tBlock, boolean force) {
if (!lwcEnable)
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
if (lwcPublic && !force)
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
if (lwcPublic && !force)
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
saveCenotaphList(tBlock.getBlock().getWorld().getName());
}

/*
* Check whether the player has the given permissions.
*/
public boolean hasPerm(CommandSender sender, String label,
boolean consoleUse) {
boolean perm = sender.hasPermission("cenotaph." + label);

if (this.console(sender)) {
if (consoleUse)
return true;

log.info("[DTPTomb] This command cannot be used in console.");
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
if (!pMessage)
return;
p.sendMessage("[DTPTomb] " + msg);
}

public void sendMessage(CommandSender p, String msg) {
if (!pMessage)
return;
p.sendMessage("[DTPTomb] " + msg);
}

private void addCommands() {
getCommand("cenlist").setExecutor(new CenListCommand(this));
getCommand("cenfind").setExecutor(new CenFindCommand(this));
getCommand("centime").setExecutor(new CenTimeCommand(this));
getCommand("cenreset").setExecutor(new CenResetCommand(this));
getCommand("cenadmin").setExecutor(new CenAdminCommand(this));
}

public String versionCheck(Boolean printToLog) {
String thisVersion = getDescription().getVersion();
URL url = null;
try {
url = new URL("http://www.moofit.com/minecraft/cenotaph.ver?v="
+ thisVersion);
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
log.warning("[DTPTomb] DTPTomb is out of date! This version: "
+ thisVersion
+ "; latest version: "
+ newVersion
+ ".");
return "DTPTomb is out of date! This version: " + thisVersion
+ "; latest version: " + newVersion + ".";
} else {
if (printToLog)
log.info("[DTPTomb] DTPTomb is up to date at version "
+ thisVersion + ".");
return "DTPTomb is up to date at version " + thisVersion + ".";
}
} catch (MalformedURLException ex) {
if (printToLog)
log.warning("[DTPTomb] Error accessing update URL.");
return "Error accessing update URL.";
} catch (IOException ex) {
if (printToLog)
log.warning("[DTPTomb] Error checking for update.");
return "Error checking for update.";
}
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
if (!logEvents)
return;
log.info("[DTPTomb] " + msg);
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

public void destroyCenotaph(Location loc) {
destroyCenotaph(tombBlockList.get(loc));
}

public void destroyCenotaph(DTPTombBlock tBlock) {
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
sendMessage(p, "Your cenotaph has been destroyed!");
}

public HashMap<String, ArrayList<DTPTombBlock>> getCenotaphList() {
return playerTombList;
}
}
