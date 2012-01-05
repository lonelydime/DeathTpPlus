package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: PlayerListenerDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:01
 */

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.onPlayerInteractDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombStoneHelperDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

public class PlayerListenerDTP extends PlayerListener {
    private DeathTpPlus plugin;
    private ConfigDTP config;
    private LoggerDTP log;
    private TombWorkerDTP worker;
    private TombStoneHelperDTP tombStoneHelper;

    public PlayerListenerDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        worker = TombWorkerDTP.getInstance();
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        log.debug("PlayerListener active");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        log.debug("onPlayerInteractDTP executing");

        if (event.isCancelled()) {
            return;
        }

        if (config.isEnableTombStone()) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            onPlayerInteractDTP onPlayerInteractDTP = new onPlayerInteractDTP(plugin);
            onPlayerInteractDTP.playerInteractTombStone(event);
        }

        if (config.isEnableDeathtp() && config.isEnableTomb() && config.isAllowTombAsTeleport()) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            onPlayerInteractDTP onPlayerInteractDTP = new onPlayerInteractDTP(plugin);
            onPlayerInteractDTP.playerInteractTomb(event);
        }
        return;
    }


    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (worker.hasTomb(playerName)) {
            worker.getTomb(playerName).checkSigns();
        }
        if (event.getPlayer().hasPermission("deathtpplus.admin.version")) {
            event.getPlayer().sendMessage("A new version of DeathTpPlus is available!");
        }
    }


    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();

        log.debug("hasTomb", worker.hasTomb(p.getName()));
        if (config.isUseTombAsRespawnPoint() && worker.hasTomb(p.getName())) {
            String deathWorld = event.getPlayer().getWorld().getName();
            Location respawn = worker.getTomb(p.getName()).getRespawn();
            log.debug("respawn", respawn);
            if (respawn != null) {
                boolean worldTravel = false;
                String spawnWorld = respawn.getWorld().getName();
                boolean sameWorld = deathWorld.equalsIgnoreCase(spawnWorld);
                if (config.getAllowWorldTravel().equalsIgnoreCase("yes") || (config.getAllowWorldTravel().equalsIgnoreCase("permissions") && p.hasPermission("deathtpplus.worldtravel"))) {
                    worldTravel = true;
                }
                log.debug("sameWorld", sameWorld);
                log.debug("worldTravel", worldTravel);
                if (sameWorld || worldTravel) {
                    log.debug("Respawn location set to Tomb");
                    event.setRespawnLocation(respawn);
                    plugin.sendMessage(p, worker.graveDigger + "You have been resurrected at your Tomb!");
                } else {
                    log.debug("Respawn location not set to Tomb");
                    plugin.sendMessage(p, worker.graveDigger + "You don't have the right to travel between worlds when you die!");
                }
            }
        }
    }
}
