package org.simiancage.DeathTpPlus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * PluginName: DeathTpPlus
 * Class: DTPStreakCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class DTPStreakCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPStreakCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
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
                    // File streakFile = new File("plugins/DeathTpPlus/streak.txt");
                    String line;
                    String[] splittext;
                    Player check;
                    String playername = "player";

                    if (args.length > 1) {
                        playername = args[0];
                    }
                    else {
                        if (sender instanceof Player) {
                            check = (Player)sender;
                            playername = check.getName();
                        }
                    }

                    List<Player> lookup = plugin.getServer().matchPlayer(playername);

                    if (lookup.size() == 0) {
                        sender.sendMessage(ChatColor.RED+ "No matching player.");
                        return true;
                    }
                    else if (lookup.size() != 1) {
                        sender.sendMessage(ChatColor.RED+ "Matched more than one player! Be more specific!");
                        return true;
                    }
                    else {
                        check = lookup.get(0);

                        try {
                            FileReader fr = new FileReader(plugin.streakFile);
                            BufferedReader br = new BufferedReader(fr);
                            boolean entryfound = false;
                            while((line = br.readLine()) != null) {
                                if (!line.startsWith("#")) {
                                    splittext = line.split(":");
                                    if (check.getName().matches(splittext[0])) {
                                        if (Integer.parseInt(splittext[1]) < 0) {
                                            sender.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1].replace("-", "")+" Death(s)");
                                        }
                                        else {
                                            sender.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1]+" Kill(s)");
                                        }

                                        entryfound = true;
                                    }
                                }
                            }
                            if (!entryfound) {
                                sender.sendMessage("No streak found");
                            }
                            br.close();
                            return true;
                        }
                        catch (IOException e) {
                            log.warning("Problems reading the Streak File",e);
                        }
                    }


                }
            }




        return false;
    }
}
