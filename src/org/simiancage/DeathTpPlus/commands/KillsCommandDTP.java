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
 * Class: KillsCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class KillsCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private DeathLogDTP deathLog;

    public KillsCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathLog = plugin.getDeathLog();
        log.info("kills command registered");

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("kills command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove permission compability in 3.2
            canUseCommand = player.hasPermission("deathtpplus.kills") || player.hasPermission("deathtpplus.deathtp.kills");
            if (player.hasPermission("deathtpplus.kills")) {
                log.warning("old permission found: deathtpplus.kills for player " + player.getName());
                log.warning("please use: deathtpplus.deathtp.kills");
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
                    DeathRecordDTP record = deathLog.getRecordByType(args[0], args[1], DeathRecordType.kill);
                    if (record != null) {
                        sender.sendMessage(String.format("%s killed %s %d time(s)", args[0], args[1], record.getCount()));
                    }
                    break;
            }

        }

        return true;

    }
}
