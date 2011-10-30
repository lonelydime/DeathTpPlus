package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: TODO insert Pluginname here
 * Class: DTPFindCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:04
 */

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.MoofIT.Minecraft.Cenotaph.Cenotaph;
import com.MoofIT.Minecraft.Cenotaph.TombBlock;

public class DTPFindCommand implements CommandExecutor {

private Cenotaph plugin;

public DTPFindCommand(Cenotaph instance) {
this.plugin = instance;
}

@Override
public boolean onCommand(CommandSender sender, Command cmd, String label,
String[] args) {
if (!plugin.hasPerm(sender, "cmd.cenotaphfind", false)) {
plugin.sendMessage(sender, "Permission Denied");
return true;
}
if (args.length != 1)
return false;
ArrayList<TombBlock> pList = plugin.playerTombList
.get(sender.getName());
if (pList == null) {
plugin.sendMessage(sender, "You have no cenotaphs.");
return true;
}
int slot = 0;
Player p = (Player) sender;
try {
slot = Integer.parseInt(args[0]);
} catch (Exception e) {
plugin.sendMessage(sender, "Invalid cenotaph");
return true;
}
slot -= 1;
if (slot < 0 || slot >= pList.size()) {
plugin.sendMessage(sender, "Invalid cenotaph");
return true;
}
TombBlock tBlock = pList.get(slot);
double degrees = (plugin.getYawTo(tBlock.getBlock().getLocation(),
p.getLocation()) + 270) % 360;
p.setCompassTarget(tBlock.getBlock().getLocation());
plugin.sendMessage(
sender,
"Your cenotaph #"
+ args[0]
+ " is to the "
+ Cenotaph.getDirection(degrees)
+ ". Your compass has been set to point at its location. Use /cenreset to reset it to your spawn point.");
return true;
}

}

