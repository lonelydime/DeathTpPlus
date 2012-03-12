package org.simiancage.DeathTpPlus.events;

//~--- non-JDK imports --------------------------------------------------------

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.*;
import org.simiancage.DeathTpPlus.listeners.EntityListenerDTP;
import org.simiancage.DeathTpPlus.logs.DeathLocationsLogDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.logs.StreakLogDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * PluginName: DeathTpPlus
 * Class: onEntityDeathDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:27
 */
public class onEntityDeathDTP {
	/**
	 * Field description
	 */
	private TombWorkerDTP tombWorker = TombWorkerDTP.getInstance();

	/**
	 * Field description
	 */
	private ConfigDTP config;

	/**
	 * Field description
	 */
	private DeathLocationsLogDTP deathLocationsLog;

	/**
	 * Field description
	 */
	private DeathLogDTP deathLog;

	/**
	 * Field description
	 */
	private DeathMessagesDTP deathMessages;

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
	private StreakLogDTP streakLog;

	/**
	 * Field description
	 */
	private TombMessagesDTP tombMessages;

	/**
	 * Field description
	 */
	private TombStoneHelperDTP tombStoneHelper;

	//~--- constructors -------------------------------------------------------

	/**
	 * Constructs ...
	 *
	 * @param plugin
	 */
	public onEntityDeathDTP(DeathTpPlus plugin) {
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
		tombWorker = TombWorkerDTP.getInstance();
		deathMessages = DeathMessagesDTP.getInstance();
		tombMessages = TombMessagesDTP.getInstance();
		this.plugin = plugin;
		deathLocationsLog = plugin.getDeathLocationLog();
		streakLog = plugin.getStreakLog();
		deathLog = plugin.getDeathLog();
		tombStoneHelper = TombStoneHelperDTP.getInstance();
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param plugin
	 * @param entityListenerDTP
	 * @param entityDeathEvent
	 */
	public void oEDeaDeathTp(DeathTpPlus plugin, EntityListenerDTP entityListenerDTP,
	                         EntityDeathEvent entityDeathEvent) {
		DeathDetailDTP deathDetail = new DeathDetailDTP(entityDeathEvent);

		log.debug("deathDetail", deathDetail);
		deathLocationsLog.setRecord(deathDetail);
	}

	/**
	 * Method description
	 *
	 * @param plugin
	 * @param entityListenerDTP
	 * @param entityDeathEvent
	 */
	public void oEDeaGeneralDeath(DeathTpPlus plugin, EntityListenerDTP entityListenerDTP,
	                              EntityDeathEvent entityDeathEvent) {
		DeathDetailDTP deathDetail = new DeathDetailDTP(entityDeathEvent);

		if (config.isShowStreaks()) {
			streakLog.setRecord(deathDetail);
		}

		// write kill to deathlog
		if (config.isAllowDeathLog()) {
			deathLog.setRecord(deathDetail);
		}

		if (config.isShowDeathNotify()) {
			String deathMessage = DeathMessagesDTP.getDeathMessage(deathDetail);

			log.debug("deathMessage", deathMessage);

			if (entityDeathEvent instanceof PlayerDeathEvent) {
				if (config.isDisableDeathNotifyInSpecifiedWorlds() || config.isShowDeathNotifyInDeathWorldOnly()) {
					((PlayerDeathEvent) entityDeathEvent).setDeathMessage("");

					Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

					for (Player player : onlinePlayers) {
						World world = player.getWorld();

						if (!config.isDisabledDeathNotifyWorld(world.getName())
								|| (config.isShowDeathNotifyInDeathWorldOnly()
								&& (world == deathDetail.getWorld()))) {
							player.sendMessage(deathMessage);
						}
					}
				} else {
					((PlayerDeathEvent) entityDeathEvent).setDeathMessage(deathMessage);
				}

				if (config.isShowDeathNotifyOnConsole()) {
					log.info(UtilsDTP.removeColorCodes(deathMessage));
				}
			}

			/*
						 *  // CraftIRC
						 *           if (plugin.craftircHandle != null) {
						 *               EndPoint endPoint =
						 *               plugin.craftircHandle.newMsgToTag() sendMessageToTag(UtilsDTP.removeColorCodes(deathMessage), config.getIrcDeathTpTag());
						 *           }
						 */
		}

		if (config.isAllowDeathLog()) {
			deathLog.setRecord(deathDetail);
		}

		// Tombstone part
		if (config.isEnableTombStone()) {
			CreateTombStone(deathDetail);
		}

		// Tomb part
		if (config.isEnableTomb()) {
			UpdateTomb(deathDetail);
		}

		if (config.isShowDeathSign()) {
			ShowDeathSign(deathDetail);
		}
	}

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void CreateTombStone(DeathDetailDTP deathDetail) {
		Player player = deathDetail.getPlayer();

		if (!plugin.hasPerm(player, "tombstone.use", false)) {
			return;
		}

		log.debug(player.getName() + " died.");

		List<ItemStack> deathDrops = deathDetail.getEntityDeathEvent().getDrops();

		if ((deathDrops.size() == 0) && !(config.isKeepExperienceOnQuickLoot())) {
			plugin.sendMessage(player, "Inventory Empty.");
			log.debug(player.getName() + " inventory empty.");

			return;
		}

//      Get the current player location.
		Location loc = player.getLocation();
		Block block = returnGoodPlace(player, loc);

		if (plugin.isWorldGuardEnabled() && !plugin.getWorldGuardPlugin().canBuild(player, block)) {
			plugin.sendMessage(player, "You died in a protected region. Dropping inventory");
			log.debug(player.getName() + " died in WorldGuard Region, dropping inventory");
			return;
		}

//      Don't create the chest if it or its sign would be in the void
		if (config.isVoidCheck()
				&& ((config.isShowTombStoneSign() && (block.getY() > 126))
				|| (!config.isShowTombStoneSign() && (block.getY() > 127))
				|| (player.getLocation().getY() < 1))) {
			plugin.sendMessage(player, "Your tombstone would be in the Void. Inventory dropped");
			log.debug(player.getName() + " died in the Void.");

			return;
		}

//      Check if the player has a chest.
		int pChestCount = 0;
		int pSignCount = 0;

		for (ItemStack item : deathDrops) {
			if (item == null) {
				continue;
			}

			if (item.getType() == Material.CHEST) {
				pChestCount += item.getAmount();
			}

			if (item.getType() == Material.SIGN) {
				pSignCount += item.getAmount();
			}
		}

		if ((pChestCount == 0) && !(plugin.hasPerm(player, "tombstone.freechest", false))) {
			plugin.sendMessage(player, "No chest found in inventory. Inventory dropped");
			log.debug(player.getName() + " No chest in inventory.");

			return;
		}

//      Check if we can replace the block.
		block = tombStoneHelper.findPlace(block, false);

		if (block == null) {
			plugin.sendMessage(player, "Could not find room for chest. Inventory dropped");
			log.debug(player.getName() + " Could not find room for chest.");

			return;
		}

//      Check if there is a nearby chest
		if (!config.isAllowInterfere() && checkChest(block)) {
			plugin.sendMessage(player, "There is a chest interfering with your tombstone. Inventory dropped");
			log.debug(player.getName() + " Chest interfered with tombstone creation.");

			return;
		}

		int removeChestCount = 1;
		int removeSign = 0;

//      Do the check for a large chest block here so we can check for
//      interference
		Block lBlock = findLarge(block);

//      Set the current block to a chest, init some variables for later use.
		block.setType(Material.CHEST);

//      We're running into issues with 1.3 where we can't cast to a Chest :(
		BlockState state = block.getState();

		if (!(state instanceof Chest)) {
			plugin.sendMessage(player, "Could not access chest. Inventory dropped.");
			log.debug(player.getName() + " Could not access chest.");

			return;
		}

		Chest sChest = (Chest) state;
		Chest lChest = null;
		int slot = 0;
		int maxSlot = sChest.getInventory().getSize();

//      Check if they need a large chest.
		if (deathDrops.size() > maxSlot) {

//          If they are allowed spawn a large chest to catch their entire
//          inventory.
			if ((lBlock != null) && plugin.hasPerm(player, "tombstone.large", false)) {
				removeChestCount = 2;

//              Check if the player has enough chests
				if ((pChestCount >= removeChestCount) || plugin.hasPerm(player, "tombstone.freechest", false)) {
					lBlock.setType(Material.CHEST);
					lChest = (Chest) lBlock.getState();
					maxSlot = maxSlot * 2;
				} else {
					removeChestCount = 1;
				}
			}
		}

//      Don't remove any chests if they get a free one.
		if (plugin.hasPerm(player, "tombstone.freechest", false)) {
			removeChestCount = 0;
		}

//      Check if we have signs enabled, if the player can use signs, and if
//      the player has a sign or gets a free sign
		Block sBlock = null;

		if (config.isShowTombStoneSign() && plugin.hasPerm(player, "tombstone.sign", false)
				&& ((pSignCount > 0) || plugin.hasPerm(player, "tombstone.freesign", false))) {

//          Find a place to put the sign, then place the sign.
			sBlock = sChest.getWorld().getBlockAt(sChest.getX(), sChest.getY() + 1, sChest.getZ());

			if (tombStoneHelper.canReplace(sBlock.getType())) {
				createSign(sBlock, deathDetail);
				removeSign = 1;
			} else if (lChest != null) {
				sBlock = lChest.getWorld().getBlockAt(lChest.getX(), lChest.getY() + 1, lChest.getZ());

				if (tombStoneHelper.canReplace(sBlock.getType())) {
					createSign(sBlock, deathDetail);
					removeSign = 1;
				}
			}
		}

//      Don't remove a sign if they get a free one
		if (plugin.hasPerm(player, "tombstone.freesign", false)) {
			removeSign = 0;
		}

		int experience;

		if (config.isKeepExperienceOnQuickLoot()) {
			if (config.isKeepFullExperience()) {
				experience = deathDetail.getPlayer().getTotalExperience();
			} else {
				experience = deathDetail.getEntityDeathEvent().getDroppedExp();
			}

			log.debug("experience", experience);
			deathDetail.getEntityDeathEvent().setDroppedExp(0);
		} else {
			experience = 0;
		}

//      Create a TombBlock for this tombstone
		TombStoneBlockDTP tStoneBlockDTP = new TombStoneBlockDTP(sChest.getBlock(), (lChest != null)
				? lChest.getBlock()
				: null, sBlock, player.getName(), (System.currentTimeMillis() / 1000), experience);

//      Protect the chest/sign if LWC is installed.
		Boolean prot = false;
		Boolean protLWC = false;

		if (plugin.hasPerm(player, "tombstone.lwc", false)) {
			prot = tombStoneHelper.activateLWC(player, tStoneBlockDTP);
		}

		tStoneBlockDTP.setLwcEnabled(prot);

		if (prot) {
			protLWC = true;
		}

//      Protect the chest with Lockette if installed, enabled, and
//      unprotected.
		if (plugin.hasPerm(player, "tombstone.lockette", false)) {
			prot = tombStoneHelper.protectWithLockette(player, tStoneBlockDTP);
		}

//      Add tombstone to list
		tombStoneHelper.offerTombStoneList(tStoneBlockDTP);

//      Add tombstone blocks to tombStoneBlockList
		tombStoneHelper.putTombStoneBlockList(tStoneBlockDTP.getBlock().getLocation(), tStoneBlockDTP);

		if (tStoneBlockDTP.getLBlock() != null) {
			tombStoneHelper.putTombStoneBlockList(tStoneBlockDTP.getLBlock().getLocation(), tStoneBlockDTP);
		}

		if (tStoneBlockDTP.getSign() != null) {
			tombStoneHelper.putTombStoneBlockList(tStoneBlockDTP.getSign().getLocation(), tStoneBlockDTP);
		}

//      Add tombstone to player lookup list
		ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(player.getName());

		if (pList == null) {
			pList = new ArrayList<TombStoneBlockDTP>();
			tombStoneHelper.putPlayerTombStoneList(player.getName(), pList);
		}

		pList.add(tStoneBlockDTP);
		tombStoneHelper.saveTombStoneList(player.getWorld().getName());

//      Next get the players inventory using the getDrops() method.
		for (Iterator<ItemStack> iter = deathDrops.listIterator(); iter.hasNext(); ) {
			ItemStack item = iter.next();

			if (item == null) {
				continue;
			}

//          Take the chest(s)
			if ((removeChestCount > 0) && (item.getType() == Material.CHEST)) {
				if (item.getAmount() >= removeChestCount) {
					item.setAmount(item.getAmount() - removeChestCount);
					removeChestCount = 0;
				} else {
					removeChestCount -= item.getAmount();
					item.setAmount(0);
				}

				if (item.getAmount() == 0) {
					iter.remove();

					continue;
				}
			}

//          Take a sign
			if ((removeSign > 0) && (item.getType() == Material.SIGN)) {
				item.setAmount(item.getAmount() - 1);
				removeSign = 0;

				if (item.getAmount() == 0) {
					iter.remove();

					continue;
				}
			}

//          Add items to chest if not full.
			if (slot < maxSlot) {
				if (slot >= sChest.getInventory().getSize()) {
					if (lChest == null) {
						continue;
					}

					lChest.getInventory().setItem(slot % sChest.getInventory().getSize(), item);
				} else {
					sChest.getInventory().setItem(slot, item);
				}

				iter.remove();
				slot++;
			} else if (removeChestCount == 0) {
				break;
			}
		}

//      Tell the player how many items went into chest.
		String msg = "Inventory stored in chest. ";

		if (deathDrops.size() > 0) {
			msg += deathDrops.size() + " items wouldn't fit in chest.";
		}

		plugin.sendMessage(player, msg);
		log.debug(player.getName() + " " + msg);

		if (prot && protLWC) {
			plugin.sendMessage(player,
					"Chest protected with LWC. " + config.getRemoveTombStoneSecurityTimeOut()
							+ "s before chest is unprotected.");
			log.debug(player.getName() + " Chest protected with LWC. " + config.getRemoveTombStoneSecurityTimeOut()
					+ "s before chest is unprotected.");
		}

		if (prot && !protLWC) {
			plugin.sendMessage(player,
					"Chest protected with Lockette. " + config.getRemoveTombStoneSecurityTimeOut()
							+ "s before chest is unprotected.");
			log.debug(player.getName() + " Chest protected with Lockette.");
		}

		if (config.isRemoveTombStone()) {
			plugin.sendMessage(player,
					"Chest will break in " + config.getRemoveTombStoneTime()
							+ "s unless an override is specified.");
			log.debug(player.getName() + " Chest will break in " + config.getRemoveTombStoneTime() + "s");
		}

		if (config.isRemoveTombStoneWhenEmpty() && config.isKeepTombStoneUntilEmpty()) {
			plugin.sendMessage(
					player,
					"Break override: Your tombstone will break when it is emptied, but will not break until then.");
		} else {
			if (config.isRemoveTombStoneWhenEmpty()) {
				plugin.sendMessage(player, "Break override: Your tombstone will break when it is emptied.");
			}

			if (config.isKeepTombStoneUntilEmpty()) {
				plugin.sendMessage(player, "Break override: Your tombstone will not break until it is empty.");
			}
		}
	}

	/**
	 * Method description
	 *
	 * @param player
	 * @param loc
	 *
	 * @return
	 */
	private Block returnGoodPlace(Player player, Location loc) {
		Block block = player.getWorld().getBlockAt(loc);

		// If we run into something we don't want to destroy, go one up.
		if ((block.getType() == Material.STEP) || (block.getType() == Material.SIGN)
				|| (block.getType() == Material.SIGN_POST) || (block.getType() == Material.TORCH)
				|| (block.getType() == Material.REDSTONE_WIRE) || (block.getType() == Material.RAILS)
				|| (block.getType() == Material.STONE_PLATE) || (block.getType() == Material.WOOD_PLATE)
				|| (block.getType() == Material.REDSTONE_TORCH_ON)
				|| (block.getType() == Material.REDSTONE_TORCH_OFF) || (block.getType() == Material.CAKE_BLOCK)) {
			block = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
		}

		if (config.isShouldOnlyUseAirToCreate()) {
			if (isNotAir(block)) {
				World world = player.getWorld();
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				Block otherBlock = world.getBlockAt(x + 1, y, z);

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x - 1, y, z);
				}

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x, y, z - 1);
				}

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x, y, z + 1);
				}

				if (!isNotAir(otherBlock)) {
					block = otherBlock;
				}
			}
		}

		return block;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param block
	 *
	 * @return
	 */
	private boolean isNotAir(Block block) {
		return (block.getType() != Material.AIR);
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void UpdateTomb(DeathDetailDTP deathDetail) {
		Player player = deathDetail.getPlayer();

		if (tombWorker.hasTomb(player.getName())) {
			log.debug("UpdateTomb");

			TombDTP TombDTP = tombWorker.getTomb(player.getName());

			log.debug("TombDTP", TombDTP);

			String signtext;

			if (deathDetail.isPVPDeath()) {
				signtext = "By " + deathDetail.getKiller().getName();
			} else {
				signtext = tombMessages.getMessage(deathDetail.getCauseOfDeath());
			}

			int deathLimit = config.getMaxDeaths();

			TombDTP.addDeath();

			if ((deathLimit != 0) && (TombDTP.getDeaths() % deathLimit) == 0) {
				TombDTP.resetTombBlocks();
				player.sendMessage(tombWorker.graveDigger + "You've reached the number of deaths before Tomb reset.("
						+ ChatColor.DARK_RED + deathLimit + ChatColor.WHITE
						+ ") All your tombs are now destroyed.");
			} else {
				TombDTP.setReason(signtext);
				TombDTP.setDeathLoc(player.getLocation(), player.getWorld().getName());
				TombDTP.updateDeath();
			}
		}
	}

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void ShowDeathSign(DeathDetailDTP deathDetail) {

		// place sign
		Block signBlock = returnGoodPlace(deathDetail.getPlayer(), deathDetail.getPlayer().getLocation());

		signBlock = tombStoneHelper.findPlace(signBlock, false);

		if (plugin.isWorldGuardEnabled() && !plugin.getWorldGuardPlugin().canBuild(deathDetail.getPlayer(), signBlock)) {
			log.debug(deathDetail.getPlayer().getName() + " died in WorldGuard Region, not creating DeathSign");
			signBlock = null;
		}

		if (signBlock == null) {
			deathDetail.getPlayer().sendMessage("We will remember you, even without a Deathsign!");
			log.informational("Couldn't create a deathsign for player " + deathDetail.getPlayer().getName()
					+ " at location " + deathDetail.getPlayer().getLocation().toString());

			return;
		}

		signBlock.setType(Material.SIGN_POST);

		BlockState state = signBlock.getState();

		if (state instanceof Sign) {
			log.debug("Creating DeathSign at: ", signBlock.getLocation());
			final Sign sign = (Sign) state;
			String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
			String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
			String name = deathDetail.getPlayer().getName();
			String reason;

			if (deathDetail.isPVPDeath()) {
				reason = tombMessages.getPvpMessage(deathDetail.getKiller().getName());
			} else {
				reason = tombMessages.getMessage(deathDetail.getCauseOfDeath());
			}

			String[] signMessage = config.getTombStoneSign();

			for (int x = 0; x < 4; x++) {
				String line = signMessage[x];

				line = line.replace("{name}", name);
				line = line.replace("{date}", date);
				line = line.replace("{time}", time);
				line = line.replace("{reason}", reason);

				if (line.length() > 15) {
					line = line.substring(0, 15);
				}
				log.debug("Writing line " + x + ": " + line);
				sign.setLine(x, line);
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.update();
				}
			});
		}
	}

//  Helper Methods

	/**
	 * Method description
	 *
	 * @param signBlock
	 * @param deathDetail
	 */
	private void createSign(Block signBlock, DeathDetailDTP deathDetail) {
		String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
		String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
		String name = deathDetail.getPlayer().getName();
		String reason;

		if (deathDetail.isPVPDeath()) {
			reason = tombMessages.getPvpMessage(deathDetail.getKiller().getName());
		} else {
			reason = tombMessages.getMessage(deathDetail.getCauseOfDeath());
		}

		signBlock.setType(Material.SIGN_POST);

		final Sign sign = (Sign) signBlock.getState();
		String[] signMessage = config.getTombStoneSign();

		for (int x = 0; x < 4; x++) {
			String line = signMessage[x];

			line = line.replace("{name}", name);
			line = line.replace("{date}", date);
			line = line.replace("{time}", time);
			line = line.replace("{reason}", reason);

			if (line.length() > 15) {
				line = line.substring(0, 15);
			}

			sign.setLine(x, line);
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				sign.update();
			}
		});
	}

	/**
	 * Method description
	 *
	 * @param base
	 *
	 * @return
	 */
	Block findLarge(Block base) {

//      Check all 4 sides for air.
		Block exp;

		exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(), base.getZ());

		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() - 1);

		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(), base.getZ());

		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() + 1);

		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		return null;
	}

	/**
	 * Method description
	 *
	 * @param base
	 *
	 * @return
	 */
	boolean checkChest(Block base) {

//      Check all 4 sides for a chest.
		Block exp;

		exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(), base.getZ());

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() - 1);

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(), base.getZ());

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() + 1);

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		return false;
	}
}
