package org.simiancage.DeathTpPlus.death.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PluginName: ${plugin}
 * Class: DeathStreakEvent
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:46
 */

public class DeathStreakEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Player killer;
	private String message;
	private Integer deaths;

	public DeathStreakEvent(Player player, Player killer, String message, Integer deaths) {
		//super("DeathStreakEvent");

		this.player = player;
		this.killer = killer;
		this.message = message;
		this.deaths = deaths;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getKiller() {
		return killer;
	}

	public void setKiller(Player killer) {
		this.killer = killer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
