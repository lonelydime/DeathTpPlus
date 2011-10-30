package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.DTPTombBlock;

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

    public DTPTimeCommand(DeathTpPlus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (!plugin.hasPerm(sender, "deathtpplus.tombstonetime", false)) {
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
        long secTimeLeft = (tBlock.getTime() + plugin.securityTimeout) - cTime;
        long remTimeLeft = (tBlock.getTime() + plugin.removeTime) - cTime;

        if (plugin.securityRemove && secTimeLeft > 0)
            plugin.sendMessage(p,
                    "Security will be removed from your Tombstone in "
                            + secTimeLeft + " seconds.");

        if (plugin.TombstoneRemove & remTimeLeft > 0)
            plugin.sendMessage(p, "Your Tombstone will break in " + remTimeLeft
                    + " seconds");
        if (plugin.removeWhenEmpty && plugin.keepUntilEmpty)
            plugin.sendMessage(
                    p,
                    "Break override: Your Tombstone will break when it is emptied, but will not break until then.");
        else {
            if (plugin.removeWhenEmpty)
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will break when it is emptied.");
            if (plugin.keepUntilEmpty)
                plugin.sendMessage(p,
                        "Break override: Your Tombstone will not break until it is empty.");
        }

        return true;
    }

}
