package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP.DeathRecordType;

/**
 * PluginName: DeathTpPlus
 * Class: DeathsCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class DeathsCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private DeathLogDTP deathLog;

    public DeathsCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathLog = plugin.getDeathLog();
        log.info("deaths command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("deaths command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove permission compability in 3.2
            if (player.hasPermission("deathtpplus.deathtp.deaths") || player.hasPermission("deathtpplus.deaths")) {
                canUseCommand = true;
                if (player.hasPermission("deathtpplus.deaths")) {
                    log.warning("old permission found: deathtpplus.deaths for player " + player.getName());
                    log.warning("please use: deathtpplus.deathtp.deaths");
                }
            }
        }

        if (canUseCommand) {

            int total;

            if (args.length > 2) {
                return false;
            }

            switch (args.length) {
                case 0:
                    Player player = (Player) sender;
                    total = plugin.getDeathLog().getTotalByType(player.getName(), DeathRecordType.death);
                    if (total > -1) {
                        sender.sendMessage(String.format("You died %d time(s)", total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 1:
                    total = plugin.getDeathLog().getTotalByType(args[0], DeathRecordType.death);
                    if (total > -1) {
                        sender.sendMessage(String.format("%s died %d time(s)", args[0], total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 2:
                    DeathRecordDTP record = plugin.getDeathLog().getRecordByType(args[0], args[1], DeathRecordType.death);
                    if (record != null) {
                        sender.sendMessage(String.format("%s died by %s %d time(s)", args[0], args[1], record.getCount()));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
            }
        }
        return true;

    }
}

