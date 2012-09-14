package org.simiancage.DeathTpPlus.teleport.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.ConfigManager;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.teleport.TeleportHelper;
import org.simiancage.DeathTpPlus.teleport.persistence.DeathLocationDao;
import org.simiancage.DeathTpPlus.teleport.persistence.DeathLocation;

/**
 * PluginName: DeathTpPlus
 * Class: DeathtpCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class DeathTpCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DefaultLogger log;
    private ConfigManager config;
    private DeathLocationDao deathLocationLog;

    /**
     * List of blocks which are normally save to teleport into
     */

    public DeathTpCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        config = ConfigManager.getInstance();
        deathLocationLog = DeathTpPlus.getDeathLocationLog();
        log.informational("deathtp command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean canUseCommand = false;
        log.debug("deathtp command executing");


        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ToDo remove debug in 3.5
            if (!player.hasPermission("deathtpplus.deathtp.deathtp") && player.hasPermission("deathtpplus.deathtp")) {
                log.debug("old permission found: deathtpplus.deathtp for player " + player.getName());
                log.debug("please use: deathtpplus.deathtp.deathtp");
            }

            canUseCommand = (player.hasPermission("deathtpplus.deathtp.deathtp") || config.isAllowDeathtp());


            if (canUseCommand) {
                TeleportHelper teleportHelper = new TeleportHelper(plugin);
                log.debug("canUseCommand", canUseCommand);
                String thisWorld = player.getWorld().getName();

                if (!teleportHelper.canTp(player, true)) {
                    log.debug("canTp", "nope");
                    return true;
                }


                DeathLocation locationRecord = deathLocationLog.getRecord(player.getName());

                if (locationRecord != null) {
                    World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
                    if (!teleportHelper.canGoBetween(thisWorld, deathWorld, player)) {
                        player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                        return true;
                    }

                    Location deathLocation = teleportHelper.findTeleportLocation(locationRecord, player);

                    if (deathLocation == null) {
                        return true;
                    }

                    deathLocation.setWorld(deathWorld);
                    player.teleport(deathLocation);
                    teleportHelper.registerTp(player);

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


}

