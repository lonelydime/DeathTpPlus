package org.simiancage.DeathTpPlus.events;

//~--- non-JDK imports --------------------------------------------------------

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombStoneHelperDTP;
import org.simiancage.DeathTpPlus.models.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

//~--- classes ----------------------------------------------------------------

/**
 * PluginName: DeathTpPlus
 * Class: onBlockBreakDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:32
 */
public class onBlockBreakDTP {
	/**
	 * Field description
	 */
	private ConfigDTP config;

	/**
	 * Field description
	 */
	private LoggerDTP log;

	/**
	 * Field description
	 */
	private DeathTpPlus plugin;

	/**
	 * Field description
	 */
	private TombStoneHelperDTP tombStoneHelper;

	/**
	 * Field description
	 */
	private TombWorkerDTP tombWorker;

	//~--- constructors -------------------------------------------------------

	/**
	 * Constructs ...
	 */
	public onBlockBreakDTP() {
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
		tombWorker = TombWorkerDTP.getInstance();
		tombStoneHelper = TombStoneHelperDTP.getInstance();
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
			TombStoneBlockDTP tStoneBlockDTP =
					tombStoneHelper.getTombStoneBlockList(b.getRelative(signData.getAttachedFace()).getLocation());

			if (tStoneBlockDTP == null) {
				return;
			}

			if (tStoneBlockDTP.getLocketteSign() != null) {
				Sign sign = (Sign) b.getState();

				event.setCancelled(true);
				sign.update();

				return;
			}
		}

		if ((b.getType() != Material.CHEST) && (b.getType() != Material.SIGN_POST)) {
			return;
		}

		TombStoneBlockDTP tStoneBlockDTP = tombStoneHelper.getTombStoneBlockList(b.getLocation());

		if (tStoneBlockDTP == null) {
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

		if ((plugin.getLwcPlugin() != null) && config.isEnableLWC() && tStoneBlockDTP.getLwcEnabled()) {
			if (tStoneBlockDTP.getOwner().equals(p.getName()) || plugin.hasPerm(p, "admin", false)) {
				tombStoneHelper.deactivateLWC(tStoneBlockDTP, true);
			} else {
				event.setCancelled(true);

				return;
			}
		}

		log.debug(p.getName() + " destroyed tombstone at " + loc);
		tombStoneHelper.removeTombStone(tStoneBlockDTP, true);
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
				TombDTP TombDTP;

				if (player.hasPermission("deathtpplus.admin.tomb")) {
					if ((TombDTP = tombWorker.getTomb(block)) != null) {
						TombDTP.removeSignBlock(block);

						if (config.isResetTombRespawn()) {
							TombDTP.setRespawn(null);
							player.sendMessage(tombWorker.graveDigger + TombDTP.getPlayer()
									+ "'s respawn point has been reset.");
						}
					}

					return;
				}

				if (tombWorker.hasTomb(playerName)) {
					if (!tombWorker.getTomb(playerName).hasSign(block)) {
						event.setCancelled(true);
					} else {
						TombDTP = tombWorker.getTomb(playerName);
						TombDTP.removeSignBlock(block);

						if (config.isResetTombRespawn()) {
							TombDTP.setRespawn(null);
							player.sendMessage(tombWorker.graveDigger + TombDTP.getPlayer()
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
