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
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombStoneHelperDTP;
import org.simiancage.DeathTpPlus.objects.TombStoneBlockDTP;

import java.util.ArrayList;

public class ListCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private TombStoneHelperDTP tombStoneHelper;

    public ListCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        log.error("dtplist command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtplist command executing");
        if (!plugin.hasPerm(sender, "tombstone.list", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(sender.getName());
        if (pList == null) {
            plugin.sendMessage(sender, "You have no tombstones.");
            return true;
        }
        plugin.sendMessage(sender, "Tombstone List:");
        int i = 0;
        for (TombStoneBlockDTP tombStoneDTP : pList) {
            i++;
            if (tombStoneDTP.getBlock() == null) {
                continue;
            }
            int X = tombStoneDTP.getBlock().getX();
            int Y = tombStoneDTP.getBlock().getY();
            int Z = tombStoneDTP.getBlock().getZ();
            plugin.sendMessage(sender, " " + i + " - World: "
                    + tombStoneDTP.getBlock().getWorld().getName() + " @(" + X + ","
                    + Y + "," + Z + ")");
        }
        return true;
    }

}

