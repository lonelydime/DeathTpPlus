package org.simiancage.DeathTpPlus.commands;

/**
 * PluginName: DeathTpPlus
 * Class: FindCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:04
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.util.ArrayList;

public class FindCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;

    public FindCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.info("dtpfind command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpfind command executing");
        if (!plugin.hasPerm(sender, "tombstone.find", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        if (args.length != 1)
            return false;
        ArrayList<TombStoneBlockDTP> pList = plugin.playerTombList
                .get(sender.getName());
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
        TombStoneBlockDTP tStoneBlockDTP = pList.get(slot);
        double degrees = (plugin.getYawTo(tStoneBlockDTP.getBlock().getLocation(),
                p.getLocation()) + 270) % 360;
        p.setCompassTarget(tStoneBlockDTP.getBlock().getLocation());
        plugin.sendMessage(
                sender,
                "Your tombstone #"
                        + args[0]
                        + " is to the "
                        + DeathTpPlus.getDirection(degrees)
                        + ". Your compass has been set to point at its location. Use /dtpreset to reset it to your spawn point.");
        return true;
    }

}

