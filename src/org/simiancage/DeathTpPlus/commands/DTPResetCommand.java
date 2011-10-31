package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: DeathTpPlus
 * Class: DTPResetCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPResetCommand implements CommandExecutor {

    private DeathTpPlus plugin;

    public DTPResetCommand(DeathTpPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (!plugin.hasPerm(sender, "deathtpplus.tombstone.reset", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        p.setCompassTarget(p.getWorld().getSpawnLocation());
        return true;
    }

}
