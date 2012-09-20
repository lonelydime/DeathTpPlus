package org.simiancage.DeathTpPlus.tomb.persistence;

import org.bukkit.block.Block;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.models.Tomb;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: TombLog
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:28
 */

public class TombLog implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 312699013882578456L;
	private ArrayList<LocSave> signBlocks = new ArrayList<LocSave>();
	private int deaths;
	private String player;
	private String reason;
	private LocSave deathLoc;
	private LocSave respawn;
	private transient DefaultLogger log;

	public TombLog(Tomb tomb) {
		log = DefaultLogger.getLogger();
		for (Block b : tomb.getSignBlocks()) {
			signBlocks.add(new LocSave(b));
		}
		reason = tomb.getReason();
		player = tomb.getPlayer();
		deaths = tomb.getDeaths();
		if (tomb.getDeathLoc() != null) {
			try {
				deathLoc = new LocSave(tomb.getDeathLoc());
			} catch (NullPointerException e) {
				deathLoc = null;
				log.warning("Player :" + player + " : NPE avoided with deathLoc", e);
			}
		} else {
			deathLoc = null;
		}
		if (tomb.getRespawn() != null) {
			try {
				respawn = new LocSave(tomb.getRespawn());
			} catch (NullPointerException e) {
				respawn = null;
				log.warning("Player :" + player + " : NPE avoided with respawn", e);
			}
		} else {
			respawn = null;
		}
	}

	public Tomb load() {
		Tomb tomb = new Tomb();
		if (deathLoc != null) {
			tomb.setDeathLoc(deathLoc.getLoc(), deathLoc.getWorld());
		} else {
			tomb.setDeathLoc(null, null);
		}

		if (respawn != null) {
			tomb.setRespawn(respawn.getLoc(), respawn.getWorld());
		} else {
			tomb.setRespawn(null, null);
		}
		tomb.setDeaths(deaths);
		tomb.setPlayer(player);
		tomb.setReason(reason);
		for (LocSave loc : signBlocks) {
			try {
				Block b = loc.getBlock();
				if (b != null) {
					tomb.addSignBlock(b);
				}
			} catch (IllegalArgumentException e) {
				log.info("One of the Tomb of " + player + " was destroyed. :\n"
						+ loc);
			}
		}
		return tomb;
	}
}


