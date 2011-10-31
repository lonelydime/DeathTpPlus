package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * PluginName: DeathTpPlus
 * Class: DTPDeathsCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class DTPDeathsCommand implements CommandExecutor {

    private DeathTpPlus plugin;

    public DTPDeathsCommand(DeathTpPlus instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
                // Todo implement logger
                plugin.log.info("[DeathTpPlus] Error reading deathlog: "+plugin.deathlogFile);
            }
        }

        else {
            return true;
        }

        return false;
    }
}