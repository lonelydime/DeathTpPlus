package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: TODO insert Pluginname here
 * Class: DTPResetCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.MoofIT.Minecraft.Cenotaph.Cenotaph;

public class DTPResetCommand implements CommandExecutor {

private Cenotaph plugin;

public DTPResetCommand(Cenotaph instance) {
this.plugin = instance;
}

@Override
public boolean onCommand(CommandSender sender, Command cmd, String label,
String[] args) {
if (!plugin.hasPerm(sender, "cmd.cenotaphreset", false)) {
plugin.sendMessage(sender, "Permission Denied");
return true;
}
Player p = (Player) sender;
p.setCompassTarget(p.getWorld().getSpawnLocation());
return true;
}

}
