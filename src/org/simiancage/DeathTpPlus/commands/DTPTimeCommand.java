package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTimeCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:08
 */

public class DTPTimeCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPTimeCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        log.info("dtptime command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dpttime command executing");
        if (!plugin.hasPerm(sender, "tombstone.time", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        if (args.length != 1)
            return false;
        ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(p.getName());
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
        DTPTombBlock tBlock = pList.get(slot);
        long secTimeLeft = (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut())) - cTime;
        long remTimeLeft = (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime())) - cTime;

        if (config.isRemoveTombStoneSecurity() && secTimeLeft > 0)
            plugin.sendMessage(p,
                    "Security will be removed from your Tombstone in "
                            + secTimeLeft + " seconds.");

        if (config.isRemoveTombStone() & remTimeLeft > 0)
            plugin.sendMessage(p, "Your Tombstone will break in " + remTimeLeft
                    + " seconds");
        if (config.isRemoveTombStoneWhenEmpty() && config.isKeepTombStoneUntilEmpty())
            plugin.sendMessage(
                    p,
                    "Break override: Your Tombstone will break when it is emptied, but will not break until then.");
        else {
            if (config.isRemoveTombStoneWhenEmpty())
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will break when it is emptied.");
            if (config.isKeepTombStoneUntilEmpty())
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will not break until it is empty.");
        }

        return true;
    }

}
