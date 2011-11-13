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
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

public class DTPResetCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPResetCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        log.info("dtpreset command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpreset command executing");
        if (!plugin.hasPerm(sender, "tombstone.reset", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        p.setCompassTarget(p.getWorld().getSpawnLocation());
        return true;
    }

}
