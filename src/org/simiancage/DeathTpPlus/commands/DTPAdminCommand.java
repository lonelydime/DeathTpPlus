package org.simiancage.DeathTpPlus.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.MoofIT.Minecraft.Cenotaph.Cenotaph;
import com.MoofIT.Minecraft.Cenotaph.TombBlock;

public class DTPAdminCommand implements CommandExecutor {

private Cenotaph plugin;

public DTPAdminCommand(Cenotaph instance) {
this.plugin = instance;
}

@Override
public boolean onCommand(CommandSender sender, Command cmd, String label,
String[] args) {
if (!plugin.hasPerm(sender, "admin", false)) {
plugin.sendMessage(sender, "Permission Denied");
return true;
}
Player p = (Player) sender;
if (args.length == 0) {
plugin.sendMessage(p, "Usage: /cenadmin list");
plugin.sendMessage(p, "Usage: /cenadmin list <playerCaseSensitive>");
plugin.sendMessage(p,
"Usage: /cenadmin find <playerCaseSensitive> <#>");
plugin.sendMessage(p,
"Usage: /cenadmin remove <playerCaseSensitive> <#>");
plugin.sendMessage(p, "Usage: /cenadmin version");
return true;
}
if (args[0].equalsIgnoreCase("list")) {
if (!plugin.hasPerm(sender, "admin.list", false)) {
plugin.sendMessage(p, "Permission Denied");
return true;
}
if (args.length < 2) {
if (plugin.playerTombList.keySet().isEmpty()) {
plugin.sendMessage(p, "There are no cenotaphs.");
return true;
}
plugin.sendMessage(p, "Players with cenotaphs:");
for (String player : plugin.playerTombList.keySet()) {
plugin.sendMessage(p, player);
}
return true;
}
ArrayList<TombBlock> pList = plugin.playerTombList.get(args[1]);
if (pList == null) {
plugin.sendMessage(p, "No cenotaphs found for " + args[1] + ".");
return true;
}
plugin.sendMessage(p, "Cenotaph List:");
int i = 0;
for (TombBlock tomb : pList) {
i++;
if (tomb.getBlock() == null)
continue;
int X = tomb.getBlock().getX();
int Y = tomb.getBlock().getY();
int Z = tomb.getBlock().getZ();
plugin.sendMessage(p, " " + i + " - World: "
+ tomb.getBlock().getWorld().getName() + " @(" + X
+ "," + Y + "," + Z + ")");
}
return true;
} else if (args[0].equalsIgnoreCase("find")) {
if (!plugin.hasPerm(sender, "admin.find", false)) {
plugin.sendMessage(p, "Permission Denied");
return true;
}
ArrayList<TombBlock> pList = plugin.playerTombList.get(args[1]);
if (pList == null) {
plugin.sendMessage(p, "No cenotaphs found for " + args[1] + ".");
return true;
}
int slot = 0;
try {
slot = Integer.parseInt(args[2]);
} catch (Exception e) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
slot -= 1;
if (slot < 0 || slot >= pList.size()) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
TombBlock tBlock = pList.get(slot);
double degrees = (plugin.getYawTo(tBlock.getBlock().getLocation(),
p.getLocation()) + 270) % 360;
int X = tBlock.getBlock().getX();
int Y = tBlock.getBlock().getY();
int Z = tBlock.getBlock().getZ();
plugin.sendMessage(p, args[1] + "'s cenotaph #" + args[2]
+ " is at " + X + "," + Y + "," + Z + ", to the "
+ Cenotaph.getDirection(degrees) + ".");
return true;
} else if (args[0].equalsIgnoreCase("time")) {
if (!plugin.hasPerm(p, "admin.cenotaphtime", false)) {
plugin.sendMessage(p, "Permission Denied");
return true;
}
if (args.length != 3)
return false;
ArrayList<TombBlock> pList = plugin.playerTombList.get(args[1]);
if (pList == null) {
plugin.sendMessage(p, "No cenotaphs found for " + args[1] + ".");
return true;
}
int slot = 0;
try {
slot = Integer.parseInt(args[2]);
} catch (Exception e) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
slot -= 1;
if (slot < 0 || slot >= pList.size()) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
long cTime = System.currentTimeMillis() / 1000;
TombBlock tBlock = pList.get(slot);
long secTimeLeft = (tBlock.getTime() + plugin.securityTimeout)
- cTime;
long remTimeLeft = (tBlock.getTime() + plugin.removeTime) - cTime;
if (plugin.securityRemove && secTimeLeft > 0)
plugin.sendMessage(p, "Security removal: " + secTimeLeft
+ " seconds.");
if (plugin.cenotaphRemove & remTimeLeft > 0)
plugin.sendMessage(p, "Cenotaph removal: " + remTimeLeft
+ " seconds.");
if (plugin.keepUntilEmpty || plugin.removeWhenEmpty)
plugin.sendMessage(p, "Keep until empty:"
+ plugin.keepUntilEmpty + "; remove when empty: "
+ plugin.removeWhenEmpty);
return true;
} else if (args[0].equalsIgnoreCase("version")) {
String message;
message = plugin.versionCheck(false);
plugin.sendMessage(p, message);

if (plugin.configVer == 0) {
plugin.sendMessage(p, "Using default config.");
} else if (plugin.configVer < plugin.configCurrent) {
plugin.sendMessage(p, "Your config file is out of date.");
} else if (plugin.configVer == plugin.configCurrent) {
plugin.sendMessage(p, "Your config file is up to date.");
}
} else if (args[0].equalsIgnoreCase("remove")) {
if (!plugin.hasPerm(sender, "admin.remove", false)) {
plugin.sendMessage(p, "Permission Denied");
return true;
}
ArrayList<TombBlock> pList = plugin.playerTombList.get(args[1]);
if (pList == null) {
plugin.sendMessage(p, "No cenotaphs found for " + args[1] + ".");
return true;
}
int slot = 0;
try {
slot = Integer.parseInt(args[2]);
} catch (Exception e) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
slot -= 1;
if (slot < 0 || slot >= pList.size()) {
plugin.sendMessage(p, "Invalid cenotaph entry.");
return true;
}
TombBlock tBlock = pList.get(slot);
plugin.destroyCenotaph(tBlock);

} else {
plugin.sendMessage(p, "Usage: /cenadmin list");
plugin.sendMessage(p, "Usage: /cenadmin list <playerCaseSensitive>");
plugin.sendMessage(p,
"Usage: /cenadmin find <playerCaseSensitive> <#>");
plugin.sendMessage(p,
"Usage: /cenadmin remove <playerCaseSensitive> <#>");
plugin.sendMessage(p, "Usage: /cenadmin version");
return true;
}
return true;
}

}
