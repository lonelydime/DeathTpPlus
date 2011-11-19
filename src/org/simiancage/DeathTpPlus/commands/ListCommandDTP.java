package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: DeathTpPlus
 * Class: ListCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombBlockDTP;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.util.ArrayList;

public class ListCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;

    public ListCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.info("dtplist command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtplist command executing");
        if (!plugin.hasPerm(sender, "tombstone.list", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        ArrayList<TombBlockDTP> pList = plugin.playerTombList
                .get(sender.getName());
        if (pList == null) {
            plugin.sendMessage(sender, "You have no tombstones.");
            return true;
        }
        plugin.sendMessage(sender, "Tombstone List:");
        int i = 0;
        for (TombBlockDTP tombDTP : pList) {
            i++;
            if (tombDTP.getBlock() == null)
                continue;
            int X = tombDTP.getBlock().getX();
            int Y = tombDTP.getBlock().getY();
            int Z = tombDTP.getBlock().getZ();
            plugin.sendMessage(sender, " " + i + " - World: "
                    + tombDTP.getBlock().getWorld().getName() + " @(" + X + ","
                    + Y + "," + Z + ")");
        }
        return true;
    }

}

