package org.simiancage.DeathTpPlus.tomb.events.handlers;

//~--- non-JDK imports --------------------------------------------------------

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.ConfigManager;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.models.Tomb;
import org.simiancage.DeathTpPlus.tomb.models.TombStoneBlock;
import org.simiancage.DeathTpPlus.tomb.workers.TombWorker;

//~--- classes ----------------------------------------------------------------

/**
 * PluginName: DeathTpPlus
 * Class: BlockBreakHandler
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:32
 */
public class BlockBreakHandler {
	/**
	 * Field description
	 */
	private ConfigManager config;

	/**
	 * Field description
	 */
	private DefaultLogger log;

	/**
	 * Field description
	 */
	private TombStoneHelper tombStoneHelper;

	/**
	 * Field description
	 */
	private TombWorker tombWorker;

	//~--- constructors -------------------------------------------------------

	/**
	 * Constructs ...
	 */
	public BlockBreakHandler() {
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
		tombWorker = TombWorker.getInstance();
		tombStoneHelper = TombStoneHelper.getInstance();
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param plugin
	 * @param event
	 */
	public void oBBTombStone(DeathTpPlus plugin, BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block b = event.getBlock();
		Player p = event.getPlayer();

		if (b.getType() == Material.WALL_SIGN) {
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign) b.getState().getData();
			TombStoneBlock tStoneBlock =
					tombStoneHelper.getTombStoneBlockList(b.getRelative(signData.getAttachedFace()).getLocation());

			if (tStoneBlock == null) {
				return;
			}

			if (tStoneBlock.getLocketteSign() != null) {
				Sign sign = (Sign) b.getState();

				event.setCancelled(true);
				sign.update();

				return;
			}
		}

		if ((b.getType() != Material.CHEST) && (b.getType() != Material.SIGN_POST)) {
			return;
		}

		TombStoneBlock tStoneBlock = tombStoneHelper.getTombStoneBlockList(b.getLocation());

		if (tStoneBlock == null) {
			return;
		}

		Location location = b.getLocation();
		String loc = location.getWorld().getName();

		loc = loc + ", x=" + location.getBlock().getX();
		loc = loc + ", y=" + location.getBlock().getY();
		loc = loc + ", z=" + location.getBlock().getZ();

		if (!config.isAllowTombStoneDestroy() && !plugin.hasPerm(p, "admin", false)) {
			log.debug(p.getName() + " tried to destroy tombstone at " + loc);
			plugin.sendMessage(p, "Tombstone unable to be destroyed");
			event.setCancelled(true);

			return;
		}

		if ((plugin.getLwcPlugin() != null) && config.isEnableLWC() && tStoneBlock.getLwcEnabled()) {
			if (tStoneBlock.getOwner().equals(p.getName()) || plugin.hasPerm(p, "admin", false)) {
				tombStoneHelper.deactivateLWC(tStoneBlock, true);
			} else {
				event.setCancelled(true);

				return;
			}
		}

		log.debug(p.getName() + " destroyed tombstone at " + loc);
		tombStoneHelper.removeTombStone(tStoneBlock, true);
	}

	/**
	 * Method description
	 *
	 * @param event
	 */
	public void oBBTomb(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (block.getState() instanceof Sign) {
			String playerName = event.getPlayer().getName();
			Sign sign = (Sign) block.getState();

			if (sign.getLine(0).indexOf(config.getTombKeyWord()) == 0) {
				Tomb tomb;

				if (player.hasPermission("deathtpplus.admin.tomb")) {
					if ((tomb = tombWorker.getTomb(block)) != null) {
						tomb.removeSignBlock(block);

						if (config.isResetTombRespawn()) {
							tomb.setRespawn(null, null);
							player.sendMessage(tombWorker.graveDigger + tomb.getPlayer()
									+ "'s respawn point has been reset.");
						}
					}

					return;
				}

				if (tombWorker.hasTomb(playerName)) {
					if (!tombWorker.getTomb(playerName).hasSign(block)) {
						event.setCancelled(true);
					} else {
						tomb = tombWorker.getTomb(playerName);
						tomb.removeSignBlock(block);

						if (config.isResetTombRespawn()) {
							tomb.setRespawn(null, null);
							player.sendMessage(tombWorker.graveDigger + tomb.getPlayer()
									+ "'s respawn point has been reset.");
						}
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
}
