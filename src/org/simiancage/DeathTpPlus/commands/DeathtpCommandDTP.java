package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.DeathLocationsLogDTP;
import org.simiancage.DeathTpPlus.models.DeathLocationRecordDTP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * PluginName: DeathTpPlus
 * Class: DeathtpCommandDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class DeathtpCommandDTP implements CommandExecutor {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private DeathLocationsLogDTP deathLocationsLog;

    public DeathtpCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathLocationsLog = plugin.getDeathLocationLog();
        log.info("deathtp command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean canUseCommand = false;
        boolean teleportok = true;
        boolean teleported = false;
        log.debug("deathtp command executing");


        if (sender instanceof Player) {
            Player player = (Player)sender;
            String thisWorld = player.getWorld().getName().toString();
            if (player.hasPermission("deathtpplus.worldtravel") && config.getAllowWorldTravel().equalsIgnoreCase("permissions"))
            {
                plugin.setWorldTravel(true);
            }
            double economyCost = Double.valueOf(config.getDeathtpCost().trim()).doubleValue();

            canUseCommand = (player.hasPermission("deathtpplus.deathtp") || config.isAllowDeathtp());

            if (canUseCommand) {
                log.debug("canUseCommand",canUseCommand );
                //costs item in inventory
                if (!config.getChargeItem().equals("0") ) {
                    if (player.getItemInHand().getType().getId() != Integer.parseInt(config.getChargeItem())) {
                        player.sendMessage("You must be holding a "+ Material.getMaterial(Integer.parseInt(config.getChargeItem())).toString()+" to teleport.");
                        teleportok = false;
                    }
                    else {
                        ItemStack currentitem = player.getItemInHand();
                        int itemnum = currentitem.getAmount();
                        itemnum--;
                        if (itemnum > 0) {
                            currentitem.setAmount(itemnum);
                            player.setItemInHand(currentitem);
                        }
                        else {
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        }
                    }
                }


                //costs Economy
                if (economyCost > 0) {
                    if (plugin.isEconomyActive()){
                        if (plugin.getEconomy().getBalance(player.getName())> economyCost) {
                            plugin.getEconomy().withdrawPlayer(player.getName(), economyCost);
                            player.sendMessage("You used "+economyCost+" to use /deathtp");
                        } else {
                            player.sendMessage("You need "+economyCost+" coins to use /deathtp");
                            teleportok = false;
                        }
                    }
                }


                if (teleportok) {

                    try {
                        String line = "";
                        String teleloc = "";
                        String[] location;
                        FileReader fr = new FileReader(plugin.getLocsName());
                        BufferedReader br = new BufferedReader(fr);

                        while((line = br.readLine()) != null) {
                            if (line.contains(player.getName()+":")) {
                                teleloc = line;
                            }
                        }

                        if (teleloc != "") {
                            location = teleloc.split(":");
                            Location sendLocation = player.getLocation();
                            double x, y, z;

                            x=Double.valueOf(location[1].trim()).doubleValue();
                            y=Double.valueOf(location[2].trim()).doubleValue();
                            z=Double.valueOf(location[3].trim()).doubleValue();
                            World deathWorld = plugin.getServer().getWorld(location[4].trim());
                            sendLocation.setX(x);
                            sendLocation.setY(y);
                            sendLocation.setZ(z);

                            boolean safeTele = false;
                            int test1=-1, test2=-1;
                            while (!safeTele) {
                                test1 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                test2 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                if (test1 == 0 && test2 == 0) {
                                    safeTele = true;
                                }

                                sendLocation.setY(sendLocation.getY()+1);
                            }

                            if (!thisWorld.equals(deathWorld.getName()))
                            {
                                if (plugin.isWorldTravel())
                                {
                                    sendLocation.setWorld(deathWorld);
                                    player.teleport(sendLocation);
                                    teleported = true;
                                } else {
                                    player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                                }
                            }
                            else {
                                player.teleport(sendLocation);
                                teleported = true;
                            }
                        }
                        else {
                            player.sendMessage("You do not have a last known death location.");
                        }

                        if (plugin.isEconomyActive() && !teleported) {
                            plugin.getEconomy().depositPlayer(player.getName(), economyCost);
                            player.sendMessage("Giving you back "+economyCost);
                        }
                    }
                    catch (IOException e) {
                      log.warning("Problems with reading the deathlocations", e);
                    }
                }
                else {
                    player.sendMessage("That command is not available");
                }

            }

            return true;
        }

        else {
            log.warning("This is only a player command.");
            return true;
        }


    }
}
