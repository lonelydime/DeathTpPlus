package org.simiancage.DeathTpPlus.death.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecordDao;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecord;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecord.DeathRecordType;

/**
 * PluginName: DeathTpPlus
 * Class: KillsCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class KillsCommand implements CommandExecutor {

    private DefaultLogger log;
    private DeathRecordDao deathLog;

    public KillsCommand(DeathTpPlus instance) {
        log = DefaultLogger.getLogger();
        log.debug("kills command registered");

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("kills command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove debug in 3.5
            if (!player.hasPermission("deathtpplus.deathtp.kills") && player.hasPermission("deathtpplus.kills")) {
                log.debug("old permission found: deathtpplus.kills for player " + player.getName());
                log.debug("please use: deathtpplus.deathtp.kills");
            }

            canUseCommand = player.hasPermission("deathtpplus.deathtp.kills");

        }

        if (canUseCommand) {

            int total;

            if (args.length > 2) {
                return false;
            }

            switch (args.length) {
                case 0:
                    Player player = (Player) sender;
                    total = deathLog.getTotalByType(player.getName(), DeathRecordType.kill);
                    if (total > -1) {
                        sender.sendMessage(String.format("You have %d kill(s)", total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 1:
                    total = deathLog.getTotalByType(args[0], DeathRecordType.kill);
                    if (total > -1) {
                        sender.sendMessage(String.format("%s has %d kill(s)", args[0], total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 2:
                    DeathRecord record = deathLog.getRecordByType(args[0], args[1], DeathRecordType.kill);
                    if (record != null) {
                        sender.sendMessage(String.format("%s killed %s %d time(s)", args[0], args[1], record.getCount()));
                    }
                    break;
            }

        }

        return true;

    }
}
