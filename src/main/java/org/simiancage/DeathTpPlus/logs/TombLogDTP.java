package org.simiancage.DeathTpPlus.logs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: TombLogDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:28
 */

public class TombLogDTP implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 312699013882578456L;
	private ArrayList<LocSaveDTP> signBlocks = new ArrayList<LocSaveDTP>();
	private int deaths;
	private String player;
	private String reason;
	private LocSaveDTP deathLoc;
	private LocSaveDTP respawn;
	private transient ConfigDTP config;
	private transient LoggerDTP log;

	public TombLogDTP(TombDTP TombDTP) {
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
		for (Block b : TombDTP.getSignBlocks()) {
			signBlocks.add(new LocSaveDTP(b));
		}
		reason = TombDTP.getReason();
		player = TombDTP.getPlayer();
		deaths = TombDTP.getDeaths();
		if (TombDTP.getDeathLoc() != null) {
			try {
				deathLoc = new LocSaveDTP(TombDTP.getDeathLoc());
			} catch (NullPointerException e) {
				deathLoc = null;
				log.warning("Player :" + player + " : NPE avoided with deathLoc", e);
			}
		} else {
			deathLoc = null;
		}
		if (TombDTP.getRespawn() != null) {
			try {
				respawn = new LocSaveDTP(TombDTP.getRespawn());
			} catch (NullPointerException e) {
				respawn = null;
				log.warning("Player :" + player + " : NPE avoided with respawn", e);
			}
		} else {
			respawn = null;
		}
	}

	public TombDTP load() {
		TombDTP TombDTP = new TombDTP();
		if (deathLoc != null) {
			TombDTP.setDeathLoc(deathLoc.getLoc(), deathLoc.getWorld());
		} else {
			TombDTP.setDeathLoc(null, null);
		}

		if (respawn != null) {
			TombDTP.setRespawn(respawn.getLoc(), respawn.getWorld());
		} else {
			TombDTP.setRespawn(null, null);
		}
		TombDTP.setDeaths(deaths);
		TombDTP.setPlayer(player);
		TombDTP.setReason(reason);
		for (LocSaveDTP loc : signBlocks) {
			try {
				Block b = loc.getBlock();
				if (b != null) {
					TombDTP.addSignBlock(b);
				}
			} catch (IllegalArgumentException e) {
				log.info("One of the Tomb of " + player + " was destroyed. :\n"
						+ loc);
			}
		}
		return TombDTP;
	}
}

class LocSaveDTP implements Serializable {
	/**
	 *
	 */
	private transient LoggerDTP log;
	private transient ConfigDTP config;
	private static final long serialVersionUID = 8631716113887974333L;
	private double x;
	private double y;
	private double z;
	private String world;

	public LocSaveDTP(Location loc) throws NullPointerException {
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		world = loc.getWorld().getName();

	}

	public LocSaveDTP(Block block) {
		this(block.getLocation());
	}

	public Location getLoc() {
		return new Location(DeathTpPlus.getBukkitServer().getWorld(world), x, y, z);
	}

	public Block getBlock() {
		World w = DeathTpPlus.getBukkitServer().getWorld(world);
		if (w == null) {
			log.info("World is not loaded :\n" + this);
			return null;
		}
		return w.getBlockAt(getLoc());
	}

	public String getWorld() {
		return world;
	}

	/*
		* (non-Javadoc)
		*
		* @see java.lang.Object#toString()
		*/
	@Override
	public String toString() {
		return "LocSave={World=" + world + ", x=" + x + ", y=" + y + ", z=" + z + "}";

	}
}


