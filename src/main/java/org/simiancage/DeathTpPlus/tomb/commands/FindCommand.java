package org.simiancage.DeathTpPlus.tomb.commands;

/**
 * PluginName: DeathTpPlus
 * Class: FindCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:04
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.models.TombStoneBlock;

import java.util.ArrayList;

public class FindCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DefaultLogger log;
    private TombStoneHelper tombStoneHelper;

    public FindCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        tombStoneHelper = TombStoneHelper.getInstance();
        log.informational("dtpfind command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpfind command executing");
        if (!plugin.hasPerm(sender, "tombstone.find", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        ArrayList<TombStoneBlock> pList = tombStoneHelper.getPlayerTombStoneList(sender.getName());
        if (pList == null) {
            plugin.sendMessage(sender, "You have no tombstones.");
            return true;
        }
        int slot = 0;
        Player p = (Player) sender;
        try {
            slot = Integer.parseInt(args[0]);
        } catch (Exception e) {
            plugin.sendMessage(sender, "Invalid tombstone");
            return true;
        }
        slot -= 1;
        if (slot < 0 || slot >= pList.size()) {
            plugin.sendMessage(sender, "Invalid tombstone");
            return true;
        }
        TombStoneBlock tStoneBlock = pList.get(slot);
        double degrees = (tombStoneHelper.getYawTo(tStoneBlock.getBlock().getLocation(),
                p.getLocation()) + 270) % 360;
        p.setCompassTarget(tStoneBlock.getBlock().getLocation());
        plugin.sendMessage(
                sender,
                "Your tombstone #"
                        + args[0]
                        + " is to the "
                        + TombStoneHelper.getDirection(degrees)
                        + ". Your compass has been set to point at its location. Use /dtpreset to reset it to your spawn point.");
        return true;
    }

}

