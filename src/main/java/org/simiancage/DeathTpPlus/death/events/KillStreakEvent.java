package org.simiancage.DeathTpPlus.death.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PluginName: DeathTpPlus
 * Class: KillStreakEvent
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:53
 */

public class KillStreakEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Player victim;
	private String message;
	private Integer kills;
	private Boolean isMultiKill;

	public KillStreakEvent(Player player, Player victim, String message, Integer kills, Boolean isMultiKill) {
		//super("KillStreakEvent");

		this.player = player;
		this.victim = victim;
		this.message = message;
		this.kills = kills;
		this.isMultiKill = isMultiKill;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getVictim() {
		return victim;
	}

	public void setVictim(Player victim) {
		this.victim = victim;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getKills() {
		return kills;
	}

	public void setKills(Integer kills) {
		this.kills = kills;
	}

	public Boolean isMultiKill() {
		return isMultiKill;
	}

	public void isMultiKill(Boolean isMultiKill) {
		this.isMultiKill = isMultiKill;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
