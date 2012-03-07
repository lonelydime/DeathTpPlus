package org.simiancage.DeathTpPlus.objects;

//~--- non-JDK imports --------------------------------------------------------

/**
 * PluginName: DeathTpPlus
 * Class: TombDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:03
 */

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.TombLogDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author DonRedhorse
 * @version ToDo enter version here, 24.01.2012
 */
public class TombDTP {

	//~--- fields -------------------------------------------------------------

	/**
	 * Field description
	 */
	private int deaths = 0;

	/**
	 * Field description
	 */
	private ConfigDTP config;

	/**
	 * Field description
	 */
	private Location deathLoc;


	private String deathWorld;


	/**
	 * Field description
	 */
	private LoggerDTP log;

	/**
	 * Field description
	 */
	private String playerName;

	/**
	 * Field description
	 */
	private String reason;

	/**
	 * Field description
	 */
	private Location respawn;


	private String respawnWorld;


	/**
	 * Field description
	 */
	private List<Block> signBlocks;

	/**
	 * Field description
	 */
	private long timeStamp;

	//~--- constructors -------------------------------------------------------

	/**
	 * Constructs ...
	 */
	public TombDTP() {
		this.signBlocks = new CopyOnWriteArrayList<Block>();
		timeStamp = 0;
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
	}

	/**
	 * @param sign
	 *
	 * @throws IllegalArgumentException
	 */
	public TombDTP(Block sign) throws IllegalArgumentException {
		this();
		addSignBlock(sign);
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * @return if the user can use death tp.
	 */
	public boolean canTeleport() {
		return (System.currentTimeMillis() >= timeStamp);
	}

	/**
	 * cut the msg to be sure that it don't exceed 18 char
	 *
	 * @param message
	 *
	 * @return
	 */
	private String cutMsg(String message) {
		String msg = null;

		if (message != null) {
			int length = message.length();

			if (length > 18) {
				msg = message.substring(0, 17);
			} else {
				msg = message;
			}
		}

		return msg;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * update the sign in the game
	 *
	 * @param line
	 * @param message
	 */
	private void setLine(final int line, String message) {
		if (!signBlocks.isEmpty()) {
			final String msg = cutMsg(message);

			Sign sign;
			int nbTickWaited = 2;
			for (Block block : signBlocks) {
				if (isSign(block)) {
					sign = (Sign) block.getState();
					sign.setLine(line, msg);
					Bukkit.getScheduler().scheduleSyncDelayedTask(TombWorkerDTP.getInstance().getPlugin(), new UpdateSignTask(sign), nbTickWaited++);
				} else {
					signBlocks.remove(block);
					log.info("[setLine]Tomb of " + playerName + " Block :(" + block.getWorld().getName()
							+ ", " + block.getX() + ", " + block.getY() + ", " + block.getZ()
							+ ") DESTROYED.");
				}
			}
		}
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 */
	public void updateDeath() {
		log.debug("updating death signs");

		if (!signBlocks.isEmpty()) {
			final String deathNb = cutMsg(deaths + " Deaths");
			final String deathReason = cutMsg(reason);
			Sign sign;
			int nbTickWaited = 2;

			log.info("[updateDeath] " + playerName + " died, updating tomb(s).");

			for (Block block : signBlocks) {
				if (isSign(block)) {
					sign = (Sign) block.getState();
					sign.setLine(2, deathNb);
					sign.setLine(3, deathReason);
					Bukkit.getScheduler().scheduleSyncDelayedTask(TombWorkerDTP.getInstance().getPlugin(), new UpdateSignTask(sign), nbTickWaited++);
				} else {
					signBlocks.remove(block);
					block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN, 1));
					block.setType(Material.AIR);
					log.info("[updateDeath]Tomb of " + playerName + " Block :("
							+ block.getWorld().getName() + ", " + block.getX() + ", " + block.getY()
							+ ", " + block.getZ() + ") DESTROYED.");
				}
			}
		}
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * @return the number of sign block that the tomb has.
	 */
	public int getNbSign() {
		return signBlocks.size();
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Check every block if they are always a sign.
	 */
	public void checkSigns() {
		for (Block block : signBlocks) {
			if (!isSign(block)) {
				signBlocks.remove(block);
				block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN, 1));
				block.setType(Material.AIR);
				log.info("[CheckSigns]Tomb of " + playerName + " Block :(" + block.getWorld().getName()
						+ ", " + block.getX() + ", " + block.getY() + ", " + block.getZ()
						+ ") DESTROYED.");
			}
		}
	}

	/**
	 * Increment the number of deaths
	 */
	public void addDeath() {
		deaths++;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(String player) {
		this.playerName = player;
	}

	/**
	 * @param deathLoc the deathLoc to set
	 */
	public void setDeathLoc(Location deathLoc, String deathWorld) {
		this.deathWorld = deathWorld;
		this.deathLoc = deathLoc;
	}

	/**
	 * @param respawn the respawn to set
	 */
	public void setRespawn(Location respawn, String respawnWorld) {
		this.respawnWorld = respawnWorld;
		this.respawn = respawn;
	}

	/**
	 * @param deaths the deaths to set
	 */
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * @return the deathLoc
	 */
	public Location getDeathLoc() {
		return deathLoc;
	}

	/**
	 * @return the deathWorld
	 */
	public String getDeathWorld() {
		return deathWorld;
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Update all the lines.
	 */
	public void updateAll() {
		setLine(1, playerName);
		setLine(2, deaths + " Deaths");
		setLine(3, reason);
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * @param sign block to be tested
	 *
	 * @return if the block is a sign
	 */
	private boolean isSign(Block sign) {
		if ((sign.getType() == Material.WALL_SIGN) || (sign.getType() == Material.SIGN)
				|| (sign.getType() == Material.SIGN_POST) || (sign.getState() instanceof Sign)) {
			return true;
		} else {
			if (sign.getType() == Material.AIR) {

				// Added chunkload when chunk not loaded, code from Tele++
				int cx = sign.getX() >> 4;
				int cz = sign.getZ() >> 4;
				World world = sign.getWorld();
				Location location = sign.getLocation();
				if (!world.isChunkLoaded(cx, cz)) {
					log.debug("Chunk at x: " + cx + " z: " + cz + " is not loaded, forcing load");
					world.loadChunk(cx, cz);

					if (!world.isChunkLoaded(cx, cz)) {
						log.severe("Chunk at x: " + cx + " z: " + cz + " is still not loaded");
						log.debug("Final try of loading chunk!");
						world.loadChunk(cx, cz);
					}
					Chunk chunk = world.getChunkAt(sign);
					log.debug("Chunk Location X: " + chunk.getX() + " Z: " + chunk.getZ());
					sign = location.getBlock();
					if (sign.getType() == Material.AIR) {
						log.debug("The location: " + sign.getLocation() + " is still air, one last try");
						world.loadChunk(cx, cz, true);

					}
				}
				sign = location.getBlock();

				log.debug("Ok, tried loading chunk, now let's check again it the tomb is still there.");

				if ((sign.getType() == Material.WALL_SIGN) || (sign.getType() == Material.SIGN)
						|| (sign.getType() == Material.SIGN_POST) || (sign.getState() instanceof Sign)) {
					return true;
				}
			}

			log.severe("Tomb of " + playerName + " Block :(" + sign.getWorld().getName() + ", " + sign.getX()
					+ ", " + sign.getY() + ", " + sign.getZ() + ") is not a sign it's a " + sign.getType());

			return false;
		}
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * @param sign the signBlock to set
	 */
	public void addSignBlock(Block sign) {
		if (isSign(sign)) {
			signBlocks.add(sign);
			log.info("Tomb Block :(" + sign.getWorld().getName() + ", " + sign.getX() + ", " + sign.getY() + ", "
					+ sign.getZ() + ") Added.");
		} else {
			throw new IllegalArgumentException("The block must be a SIGN or WALL_SIGN or SIGN_POST");
		}
	}

	public void addSignBlock(SignChangeEvent event) {
		final Block sign = event.getBlock();
		signBlocks.add(sign);
		log.info("Tomb Block :(" + sign.getWorld().getName() + ", " + sign.getX() + ", " + sign.getY() + ", "
				+ sign.getZ() + ") Added.");
		log.debug("This is chunk: Z " + (sign.getZ() >> 4) + " X" + (sign.getX() >> 4));
		event.setLine(1, cutMsg(playerName));
		event.setLine(2, cutMsg(deaths + " Deaths"));

		if ((reason != null) && !reason.isEmpty()) {
			event.setLine(3, cutMsg(reason));
		}

	}

	/**
	 * Clear the signBlock vector
	 */
	public void resetTombBlocks() {

		for (Block block : signBlocks) {
			if (isSign(block)) {
				block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN, 1));
				block.setType(Material.AIR);
			}

		}

		signBlocks.clear();
		log.info("[resetTombBlocks] Tomb of " + playerName + " reseted.");
	}

	/**
	 * Remove the given sign from the list.
	 *
	 * @param sign
	 */
	public void removeSignBlock(final Block sign) {
		if (hasSign(sign)) {

			signBlocks.remove(sign);
			log.info("[removeSignBlock]Tomb of " + playerName + " Block :(" + sign.getWorld().getName()
					+ ", " + sign.getX() + ", " + sign.getY() + ", " + sign.getZ() + ") REMOVED.");
		}
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Check if the block is used as a tomb.
	 *
	 * @param sign
	 *
	 * @return
	 */
	public boolean hasSign(Block sign) {
		return signBlocks.contains(sign);
	}

	/**
	 * @return the player
	 */
	public String getPlayer() {
		return playerName;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the deaths
	 */
	public int getDeaths() {
		return deaths;
	}

	/**
	 * @return the signBlocks
	 */
	public List<Block> getSignBlocks() {
		return signBlocks;
	}

	/**
	 * @return the respawn
	 */
	public Location getRespawn() {
		return respawn;
	}

	/**
	 * @return the respawnWorld
	 */
	public String getRespawnWorld() {
		return respawnWorld;
	}


	//~--- methods ------------------------------------------------------------

	/**
	 * To save the TombDTP
	 *
	 * @return
	 */
	public TombLogDTP save() {
		return new TombLogDTP(this);
	}

	private class UpdateSignTask implements Runnable {
		private final Sign toUpdate;


		/**
		 * @param toUpdate
		 */
		public UpdateSignTask(Sign toUpdate) {
			super();
			this.toUpdate = toUpdate;
		}


		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			toUpdate.update();
		}

	}
}
