package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TeleportHelperDTP;
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

    /**
     * List of blocks which are normally save to teleport into
     */

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
                TeleportHelperDTP teleportHelperDTP = new TeleportHelperDTP(plugin);
                log.debug("canUseCommand", canUseCommand);
                String thisWorld = player.getWorld().getName();

                if (!teleportHelperDTP.canTp(player, true)) {
                    log.debug("canTp", "nope");
                    return true;
                }


                DeathLocationRecordDTP locationRecord = deathLocationLog.getRecord(player.getName());

                if (locationRecord != null) {
                    World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
                    if (!teleportHelperDTP.canGoBetween(thisWorld, deathWorld, player)) {
                        player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                        return true;
                    }

                    Location deathLocation = teleportHelperDTP.findTeleportLocation(locationRecord, player);

                    if (deathLocation == null) {
                        return true;
                    }

                    deathLocation.setWorld(deathWorld);
                    player.teleport(deathLocation);
                    teleportHelperDTP.registerTp(player);

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

