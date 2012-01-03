package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    /**
     * List of blocks which are normally save to teleport into
     */
    private List<Integer> saveBlocks = new ArrayList<Integer>(Arrays.asList(new Integer[]{
            0, 6, 8, 9, 10, 11, 37, 38, 39, 40, 50, 51, 55, 59, 69, 76
    }));

    public DeathtpCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathLocationLog = plugin.getDeathLocationLog();
        log.informational("deathtp command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean canUseCommand = false;
        boolean worldTravel = false;
        log.debug("deathtp command executing");


        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove debug in 3.5
            if (player.hasPermission("deathtpplus.deathtp")) {
                log.debug("old permission found: deathtpplus.deathtp for player " + player.getName());
                log.debug("please use: deathtpplus.deathtp.deathtp");
            }

            canUseCommand = (player.hasPermission("deathtpplus.deathtp.deathtp") || config.isAllowDeathtp());


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
                    log.debug("locationRecord", locationRecord);
                    Location deathLocation = locationRecord.getLocation();
                    log.debug("deathLocation", deathLocation);
                    World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
                    // Added chunkload when chunk not loaded, code from Tele++
                    int cx = deathLocation.getBlockX() >> 4;
                    int cz = deathLocation.getBlockZ() >> 4;

                    if (!deathWorld.isChunkLoaded(cx, cz)) {
                        log.debug("Chunk at x: " + cx + " z: " + cz + " is not loaded");
                        deathWorld.loadChunk(cx, cz);
                        if (!deathWorld.isChunkLoaded(cx, cz)) {
                            log.severe("Chunk at x: " + cx + " z: " + cz + " is not loaded");
                        }
                    }

                    if (config.isTeleportToHighestBlock()) {
                        Location yLocation = deathWorld.getHighestBlockAt(locationRecord.getLocation().getBlockX(), locationRecord.getLocation().getBlockZ()).getLocation();
                        log.debug("yLocation", yLocation);
                        int y = yLocation.getBlockY() + 2;
                        int z = yLocation.getBlockZ();
                        int x = yLocation.getBlockX();
                        if (y == 2) {
                            y = 124;
                        }
                        deathLocation = deathWorld.getBlockAt(x, y, z).getLocation();
                        log.debug("deathLocation", deathLocation);
                    } else {
                        deathLocation = saveDeathLocation(locationRecord, deathWorld);
                        if (deathLocation == null) {
                            player.sendRawMessage("There is no save place to teleport you at location:");
                            player.sendRawMessage("x: " + locationRecord.getLocation().getX() + " y: " + locationRecord.getLocation().getY() + " z: " + locationRecord.getLocation().getZ() + " in world: " + locationRecord.getWorldName());
                            player.sendRawMessage("Have fun walking... sorry about that..");
                            return true;
                        }
                    }


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
            double deathTpCost = Double.valueOf(config.getDeathtpCost().trim());
            if (plugin.isEconomyActive() && deathTpCost > 0.0) {
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

    // Code from Tele++
    private Location saveDeathLocation(DeathLocationRecordDTP locationRecord, World world) {
        log.debug("world", world);
        double x = locationRecord.getLocation().getBlockX();
        double y = locationRecord.getLocation().getBlockY();
        double z = locationRecord.getLocation().getBlockZ();
        log.debug("x,y,z:", x + "," + y + "," + z);

        x = x + .5D;
        z = z + .5D;

        if (y < 1.0D) {
            y = 1.0D;
        }

        while (blockIsAboveAir(world, x, y, z)) {
            y -= 1.0D;

            if (y < -512) {
                return null;
            }
        }

        while (!blockIsSafe(world, x, y, z)) {
            y += 1.0D;

            if (y > 512) {
                return null;
            }
        }
        Location saveDeathLocation = new Location(world, x, y, z);
        log.debug("saveDeathLocation", saveDeathLocation);
        return saveDeathLocation;
    }

    private boolean blockIsAboveAir(World world, double x, double y, double z) {
        Material mat = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1.0D), (int) Math.floor(z)).getType();

        return saveBlocks.contains(mat.getId());
    }

    public boolean blockIsSafe(Block block) {
        return blockIsSafe(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public boolean blockIsSafe(World world, double x, double y, double z) {
        Material mat1 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType();
        Material mat2 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + 1.0D), (int) Math.floor(z)).getType();

        return (saveBlocks.contains(mat1.getId())) && (saveBlocks.contains(mat2.getId()));
    }

}

