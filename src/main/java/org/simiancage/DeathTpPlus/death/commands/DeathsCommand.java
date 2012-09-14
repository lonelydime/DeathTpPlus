package org.simiancage.DeathTpPlus.death.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecord;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecord.DeathRecordType;

/**
 * PluginName: DeathTpPlus
 * Class: DeathsCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class DeathsCommand implements CommandExecutor {

    private DefaultLogger log;

    public DeathsCommand(DeathTpPlus instance) {
        log = DefaultLogger.getLogger();
        log.debug("deaths command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("deaths command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove debug  in 3.5.1597
            if (player.hasPermission("deathtpplus.deathtp.deaths") && player.hasPermission("deathtpplus.deaths")) {
                log.debug("old permission found: deathtpplus.deaths for player " + player.getName());
                log.debug("please use: deathtpplus.deathtp.deaths");
            }
            if (player.hasPermission("deathtpplus.deathtp.deaths")) {
                canUseCommand = true;

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
                    total = DeathTpPlus.getDeathLog().getTotalByType(player.getName(), DeathRecordType.death);
                    if (total > -1) {
                        sender.sendMessage(String.format("You died %d time(s)", total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 1:
                    total = DeathTpPlus.getDeathLog().getTotalByType(args[0], DeathRecordType.death);
                    if (total > -1) {
                        sender.sendMessage(String.format("%s died %d time(s)", args[0], total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 2:
                    DeathRecord record = DeathTpPlus.getDeathLog().getRecordByType(args[0], args[1], DeathRecordType.death);
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

