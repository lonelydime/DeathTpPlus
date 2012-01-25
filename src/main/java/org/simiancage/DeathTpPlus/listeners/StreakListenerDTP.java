package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;

/**
 * PluginName: DeathTpPlus
 * Class: StreakListenerDTP
 * User: DonRedhorse
 * Date: 26.11.11
 * Time: 19:57
 */

public class StreakListenerDTP implements Listener {
	private DeathTpPlus plugin;
	private ConfigDTP config = ConfigDTP.getInstance();

	public StreakListenerDTP(DeathTpPlus plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeathStreakEvent(DeathStreakEventDTP event) {
		String playerName = getPlayerNameForBroadcast(event.getPlayer());
		plugin.getServer().broadcastMessage(event.getMessage().replace("%n", playerName));
	}

	@EventHandler
	public void onKillStreakEvent(KillStreakEventDTP event) {
		String playerName = getPlayerNameForBroadcast(event.getPlayer());
		plugin.getServer().broadcastMessage(event.getMessage().replace("%n", playerName));
	}

	private String getPlayerNameForBroadcast(Player player) {
		String playerName = player.getName();
		if (config.isUseDisplayNameforBroadcasts()) {
			playerName = player.getDisplayName();
		}
		if (playerName.contains("*")) {
			playerName = playerName.replace("*", "");
		}
		//Todo Add NPE handling for specific errors here, have no idea atm what that could be
		//ToDo we will need input from users about that

		return playerName;
	}
}

