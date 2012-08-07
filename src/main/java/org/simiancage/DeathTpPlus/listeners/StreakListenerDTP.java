package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.SpoutManager;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP;

/**
 * PluginName: DeathTpPlus
 * Class: StreakListenerDTP
 * User: DonRedhorse
 * Date: 26.11.11
 * Time: 19:57
 */

public class StreakListenerDTP implements Listener {
	private DeathTpPlus plugin;
    private static final int SOUND_DISTANCE = 50;
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
	public void onKillStreakEvent(final KillStreakEventDTP event) {
		String playerName = getPlayerNameForBroadcast(event.getPlayer());
		plugin.getServer().broadcastMessage(event.getMessage().replace("%n", playerName));
        final Location location = event.getPlayer().getLocation();
        if (config.isPlaySounds() && plugin.isSpoutEnabled()) {
            if (event.isMultiKill()) {
                // Play our multikill sound
                playMultiKillSound(event);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playKillStreakSound(event.getKills(), location);
                    }
                }, 40);
            }
        }
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
	
    private void playMultiKillSound(KillStreakEventDTP event) {
        String soundName = DeathMessagesDTP.getMultiKillSound(event.getKills());
        if (soundName == null) {
            return;
        }
        String url = DeathMessagesDTP.getSoundUrl() + soundName + DeathMessagesDTP.getSoundFormat();
        SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, event.getPlayer().getLocation(), SOUND_DISTANCE);
    }

    private void playKillStreakSound(Integer kills, Location loc) {
        String soundName = DeathMessagesDTP.getKillStreakSound(kills);
        if (soundName == null) {
            return;
        }
        String url = DeathMessagesDTP.getSoundUrl() + soundName + DeathMessagesDTP.getSoundFormat();
        SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, loc, SOUND_DISTANCE);
    }
}

