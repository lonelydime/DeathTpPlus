package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * PluginName: DeathTpPlus
 * Class: DTPKillsCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class DTPKillsCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    DTPLogger log;
    DTPConfig config;

    public DTPKillsCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean canUseCommand = false;
        String playername = "";
                    String username = "";
                    String line;
                    int totalnum = 0;
                    String[] splittext;
                    boolean foundrecord = false;

                    if (sender instanceof Player) {
                        Player player = (Player)sender;

                        canUseCommand =  player.hasPermission("deathtpplus.kills");
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
                            username = args[1];
                        } else {
                            return false;
                        }
                        //File deathlogFile = new File(getDataFolder()+"/deathlog.txt");
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(plugin.deathlogFile));
                            while((line = br.readLine()) != null) {
                                splittext = line.split(":");
                                //0 = name, 1 = type, 2 = cause, 3 = number
                                if (!username.matches("")) {
                                    if (splittext[0].matches(playername) && splittext[1].matches("kill") && splittext[2].matches(username)) {
                                        String times = "times";
                                        if (splittext[2] == "1")
                                            times = "time";
                                        sender.sendMessage(playername+" has killed "+username+" "+splittext[3]+" "+times);
                                        foundrecord = true;
                                    }
                                }
                                //total count
                                else {
                                    if (splittext[0].matches(playername) && splittext[1].matches("kill") ) {
                                        totalnum = totalnum + Integer.parseInt(splittext[3]);
                                    }
                                }
                            }
                            if (username.matches("")) {
                                String times = "times";
                                if (totalnum == 1) {
                                    times = "time";
                                }
                                sender.sendMessage(playername+" has killed "+totalnum+" "+times);
                            }
                            else {
                                if (!foundrecord){
                                   sender.sendMessage(playername+" has killed "+username+" 0 times");
                                }
                            }
                            return true;
                        }
                        catch(Exception e) {
                            log.warning("Error reading deathlog: "+plugin.deathlogFile, e);
                        }
                    }
                    else {
                        return true;
                    }

                    return false;
                }
}
