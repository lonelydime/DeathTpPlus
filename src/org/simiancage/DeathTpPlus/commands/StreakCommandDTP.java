package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.StreakLogDTP;
import org.simiancage.DeathTpPlus.models.StreakRecordDTP;

/**
 * PluginName: DeathTpPlus
 * Class: StreakCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class StreakCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private StreakLogDTP streakLog;

    public StreakCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        streakLog = plugin.getStreakLog();
        log.info("streak command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("streak command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player)sender;
            canUseCommand =  player.hasPermission("deathtpplus.streak");

        }

        if (canUseCommand) {
            if (config.isShowStreaks() ) {

                StreakRecordDTP streak = DeathTpPlus.streakLog.getRecord(args.length > 0 ? args[0] : ((Player) sender).getName());

                if (streak != null) {
                    if (streak.getCount() < 0) {
                        if (args.length > 0) {
                            sender.sendMessage(String.format("%s is on a %d death streak.", args[0], streak.getCount() * -1));
                        }
                        else {
                            sender.sendMessage(String.format("You are on a %d death streak.", streak.getCount() * -1));
                        }
                    }
                    else {
                        if (args.length > 0) {
                            sender.sendMessage(String.format("%s is on a %d kill streak.", args[0], streak.getCount()));
                        }
                        else {
                            sender.sendMessage(String.format("You are on a %d kill streak.", streak.getCount()));
                        }
                    }
                }
                else {
                    sender.sendMessage("No record found.");
                }


            }
            return true;
        }


        return false;
    }
}
