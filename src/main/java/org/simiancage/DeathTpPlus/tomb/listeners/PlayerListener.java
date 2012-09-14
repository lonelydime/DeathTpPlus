package org.simiancage.DeathTpPlus.tomb.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: PlayerListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:01
 */

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.ConfigManager;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.listeners.handlers.PlayerInteractHandler;
import org.simiancage.DeathTpPlus.tomb.workers.TombWorker;

public class PlayerListener implements Listener {
	private DeathTpPlus plugin;
	private ConfigManager config;
	private DefaultLogger log;
	private TombWorker worker;

	public PlayerListener(DeathTpPlus instance) {
		this.plugin = instance;
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
		worker = TombWorker.getInstance();
		log.debug("PlayerListener active");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.isCancelled()) {
			return;
		}

		if (config.isEnableTombStone()) {
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			PlayerInteractHandler onPlayerInteract = new PlayerInteractHandler(plugin);
			onPlayerInteract.playerInteractTombStone(event);
		}

		if (config.isEnableDeathtp() && config.isEnableTomb() && config.isAllowTombAsTeleport()) {
			if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			PlayerInteractHandler onPlayerInteract = new PlayerInteractHandler(plugin);
			onPlayerInteract.playerInteractTomb(event);
		}
		return;
	}


	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		String playerName = event.getPlayer().getName();
		if (worker.hasTomb(playerName)) {
			worker.getTomb(playerName).checkSigns();
		}
		if (event.getPlayer().hasPermission("deathtpplus.admin.version")) {
			if (config.isDifferentPluginAvailable()) {
				event.getPlayer().sendMessage("A new version of DeathTpPlus is available!");
			}
		}
	}


	@EventHandler
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
				log.debug("spawnworld", spawnWorld);
				boolean sameWorld = deathWorld.equalsIgnoreCase(spawnWorld);
				if (config.getAllowWorldTravel().equalsIgnoreCase("yes") || ((config.getAllowWorldTravel().equalsIgnoreCase("permissions") && p.hasPermission("deathtpplus.worldtravel")))) {
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
