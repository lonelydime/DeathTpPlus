package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: TODO insert Pluginname here
 * Class: DTPListCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPListCommand implements CommandExecutor {

    private DeathTpPlus plugin;

    public DTPListCommand(DeathTpPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (!plugin.hasPerm(sender, "deathtpplus.tombstone.list", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        ArrayList<DTPTombBlock> pList = plugin.playerTombList
                .get(sender.getName());
        if (pList == null) {
            plugin.sendMessage(sender, "You have no tombstones.");
            return true;
        }
        plugin.sendMessage(sender, "Tombstone List:");
        int i = 0;
        for (DTPTombBlock tomb : pList) {
            i++;
            if (tomb.getBlock() == null)
                continue;
            int X = tomb.getBlock().getX();
            int Y = tomb.getBlock().getY();
            int Z = tomb.getBlock().getZ();
            plugin.sendMessage(sender, " " + i + " - World: "
                    + tomb.getBlock().getWorld().getName() + " @(" + X + ","
                    + Y + "," + Z + ")");
        }
        return true;
    }

}

