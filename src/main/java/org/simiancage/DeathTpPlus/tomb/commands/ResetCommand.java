package org.simiancage.DeathTpPlus.tomb.commands;

/**
 * PluginName: DeathTpPlus
 * Class: ResetCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.DefaultLogger;

public class ResetCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DefaultLogger log;

    public ResetCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        log.informational("dtpreset command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpreset command executing");
        if (!plugin.hasPerm(sender, "tombstone.reset", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        p.setCompassTarget(p.getWorld().getSpawnLocation());
        plugin.sendMessage(sender, "Your compass has been reset to the spawn location!");
        return true;
    }

}
