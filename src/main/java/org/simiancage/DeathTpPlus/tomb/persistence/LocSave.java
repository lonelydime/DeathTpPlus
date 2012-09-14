package org.simiancage.DeathTpPlus.tomb.persistence;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.DefaultLogger;

class LocSave implements Serializable {
	/**
	 *
	 */
	private transient DefaultLogger log;
	private static final long serialVersionUID = 8631716113887974333L;
	private double x;
	private double y;
	private double z;
	private String world;

	public LocSave(Location loc) throws NullPointerException {
		log = DefaultLogger.getLogger();
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		world = loc.getWorld().getName();

	}

	public LocSave(Block block) {
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