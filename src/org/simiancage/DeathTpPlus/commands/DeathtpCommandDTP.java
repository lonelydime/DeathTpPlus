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
    private DeathLocationsLogDTP deathLocationLog;

    public DeathtpCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathLocationLog = plugin.getDeathLocationLog();
        log.error("deathtp command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean canUseCommand = false;
        boolean worldTravel = false;
        log.debug("deathtp command executing");


        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove permission compability in 3.2
            canUseCommand = (player.hasPermission("deathtpplus.deathtp") || player.hasPermission("deathtpplus.deathtp.deathtp") || config.isAllowDeathtp());

            if (player.hasPermission("deathtpplus.deathtp")) {
                log.warning("old permission found: deathtpplus.deathtp for player " + player.getName());
                log.warning("please use: deathtpplus.deathtp.deathtp");
            }

            if (canUseCommand) {
                log.debug("canUseCommand", canUseCommand);
                String thisWorld = player.getWorld().getName();
                if ((player.hasPermission("deathtpplus.worldtravel") && config.getAllowWorldTravel().equalsIgnoreCase("permissions")) || config.getAllowWorldTravel().equalsIgnoreCase("yes")) {
                    worldTravel = true;
                }
                if (!canTp(player)) {
                    log.debug("canTp", "nope");
                    return true;
                }


                DeathLocationRecordDTP locationRecord = deathLocationLog.getRecord(player.getName());

                if (locationRecord != null) {

                    World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
                    Location deathLocation = deathWorld.getHighestBlockAt(locationRecord.getLocation().getBlockX(), locationRecord.getLocation().getBlockZ()).getLocation();

                    if (!thisWorld.equals(deathWorld.getName())) {
                        if (worldTravel) {
                            deathLocation.setWorld(deathWorld);
                            player.teleport(deathLocation);
                            registerTp(player);
                        } else {
                            player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                        }
                    } else {
                        player.teleport(deathLocation);
                        registerTp(player);
                    }
                }

            } else {
                player.sendMessage("That command is not available");
            }


            return true;
        } else {
            log.warning("This is only a player command.");
            return true;
        }
    }

    private Boolean canTp(Player player) {
        return hasItem(player) && hasFunds(player);
    }

    private void registerTp(Player player) {
        if (hasItem(player)) {
            if (Integer.parseInt(config.getChargeItem()) != 0) {
                ItemStack itemInHand = player.getItemInHand();

                if (itemInHand.getAmount() == 1) {
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    player.setItemInHand(itemInHand);
                }
            }
        }

        if (hasFunds(player)) {
            if (plugin.isEconomyActive()) {
                double deathTpCost = Double.valueOf(config.getDeathtpCost().trim());
                plugin.getEconomy().withdrawPlayer(player.getName(), deathTpCost);
                player.sendMessage(String.format("You used %s to use /deathtp.", plugin.getEconomy().format(deathTpCost)));
            }
        }
    }

    private Boolean hasItem(Player player) {
        int chargeItem = Integer.parseInt(config.getChargeItem());
        log.debug("chargeItem", chargeItem);
        // costs item in inventory
        if (chargeItem == 0 || chargeItem == player.getItemInHand().getType().getId()) {
            log.debug("hasItem", true);
            return true;
        }

        player.sendMessage(String.format("You must be holding a %s to teleport.", Material.getMaterial(chargeItem).toString().toLowerCase()));

        return false;
    }

    private Boolean hasFunds(Player player) {
        double deathTpCost = Double.valueOf(config.getDeathtpCost().trim());
        log.debug("deathTpCost", deathTpCost);
        if (deathTpCost == 0) {
            return true;
        }

        // costs economy
        if (plugin.isEconomyActive()) {
            log.debug("isEconomyActive", "yes");
            if (plugin.getEconomy().getBalance(player.getName()) > deathTpCost) {
                log.debug("hasFunds", true);
                return true;
            } else {
                player.sendMessage(String.format("You need %s coins to use /deathtp.", plugin.getEconomy().format(deathTpCost)));
                return false;
            }
        }
        return true;
    }

}

