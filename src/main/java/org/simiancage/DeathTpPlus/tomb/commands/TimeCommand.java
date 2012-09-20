package org.simiancage.DeathTpPlus.tomb.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.ConfigManager;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.models.TombStoneBlock;

import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: TimeCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:08
 */

public class TimeCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DefaultLogger log;
    private ConfigManager config;
    private TombStoneHelper tombStoneHelper;

    public TimeCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        config = ConfigManager.getInstance();
        tombStoneHelper = TombStoneHelper.getInstance();
        log.informational("dtptime command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dpttime command executing");
        if (!plugin.hasPerm(sender, "tombstone.time", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        if (args.length != 1) {
            return false;
        }
        ArrayList<TombStoneBlock> pList = tombStoneHelper.getPlayerTombStoneList(p.getName());
        if (pList == null) {
            plugin.sendMessage(p, "You have no Tombstones.");
            return true;
        }
        int slot = 0;
        try {
            slot = Integer.parseInt(args[0]);
        } catch (Exception e) {
            plugin.sendMessage(p, "Invalid Tombstone");
            return true;
        }
        slot -= 1;
        if (slot < 0 || slot >= pList.size()) {
            plugin.sendMessage(p, "Invalid Tombstone");
            return true;
        }
        long cTime = System.currentTimeMillis() / 1000;
        TombStoneBlock tStoneBlock = pList.get(slot);
        long secTimeLeft = (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut())) - cTime;
        long remTimeLeft = (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime())) - cTime;

        if (config.isRemoveTombStoneSecurity() && secTimeLeft > 0) {
            plugin.sendMessage(p,
                    "Security will be removed from your Tombstone in "
                            + secTimeLeft + " seconds.");
        }

        if (config.isRemoveTombStone() & remTimeLeft > 0) {
            plugin.sendMessage(p, "Your Tombstone will break in " + remTimeLeft
                    + " seconds");
        }
        if (config.isRemoveTombStoneWhenEmpty() && config.isKeepTombStoneUntilEmpty()) {
            plugin.sendMessage(
                    p,
                    "Break override: Your Tombstone will break when it is emptied, but will not break until then.");
        } else {
            if (config.isRemoveTombStoneWhenEmpty()) {
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will break when it is emptied.");
            }
            if (config.isKeepTombStoneUntilEmpty()) {
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will not break until it is empty.");
            }
        }

        return true;
    }

}
