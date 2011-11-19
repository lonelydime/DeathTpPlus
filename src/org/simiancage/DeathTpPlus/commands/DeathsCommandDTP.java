package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;

import java.io.BufferedReader;
import java.io.FileReader;

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

    public DeathsCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.info("deaths command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("deaths command executing");
        boolean canUseCommand = false;
        String playername = "";
        String cause = "";
        String line;
        int totalnum = 0;
        String[] splittext;
        boolean foundrecord = false;

        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (player.hasPermission("deathtpplus.deaths")) {
                canUseCommand = true;
            }
        }

        if (canUseCommand) {

            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    playername = player.getName();
                }
                else {
                    return false;
                }
            }
            else if (args.length == 1) {
                playername = args[0];
            }
            else if (args.length == 2) {
                playername = args[0];
                cause = args[1];
            }
            else {
                return false;
            }

            try {
                BufferedReader br = new BufferedReader(new FileReader(plugin.deathlogFile));
                while((line = br.readLine()) != null) {
                    splittext = line.split(":");
                    //0 = name, 1 = type, 2 = cause, 3 = number
                    if (!cause.matches("")) {
                        if (splittext[0].matches(playername) && splittext[1].matches("death") && splittext[2].matches(cause.toUpperCase())) {
                            String times = "times";
                            if (splittext[2] == "1") {
                                times = "time";
                            }
                            sender.sendMessage(playername+" has died by "+cause+" "+splittext[3]+" "+times);
                            foundrecord = true;
                        }
                    }
                    //total count
                    else {
                        if (splittext[0].matches(playername) && splittext[1].matches("death") ) {
                            totalnum = totalnum + Integer.parseInt(splittext[3]);
                        }
                    }
                }
                if (cause.matches("")) {
                    String times = "times";
                    if (totalnum == 1) {
                        times = "time";
                    }
                    sender.sendMessage(playername+" has died "+totalnum+" "+times);
                }
                else {
                    if (!foundrecord) {
                        sender.sendMessage(playername+" has died by "+cause+" 0 times");
                    }
                }
                return true;
            }
            catch(Exception e) {
                log.warning("Error reading deathlog: "+plugin.deathlogFile , e);
            }
        }

        else {
            return true;
        }

        return false;
    }
}
