package org.simiancage.DeathTpPlus.helpers;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.model.Protection.Type;
import com.griefcraft.model.ProtectionTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.TombStoneBlockDTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * PluginName: DeathTpPlus
 * Class: TombStoneHelperDTP
 * User: DonRedhorse
 * Date: 27.11.11
 * Time: 21:27
 */

public class TombStoneHelperDTP {


	private static final String TOMB_STONE_LOCATION_LIST = "TombStoneList";
	private LoggerDTP log;
	private ConfigDTP config;
	private DeathTpPlus plugin;
	private static TombStoneHelperDTP instance;
	private ConcurrentLinkedQueue<TombStoneBlockDTP> tombStoneListDTP = new ConcurrentLinkedQueue<TombStoneBlockDTP>();
	private HashMap<Location, TombStoneBlockDTP> tombStoneBlockList = new HashMap<Location, TombStoneBlockDTP>();
	private HashMap<String, ArrayList<TombStoneBlockDTP>> playerTombStoneList = new HashMap<String, ArrayList<TombStoneBlockDTP>>();

	private TombStoneHelperDTP() {
		this.plugin = DeathTpPlus.getPlugin();
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();

	}

	public static TombStoneHelperDTP getInstance() {
		if (instance == null) {
			instance = new TombStoneHelperDTP();
		}
		return instance;
	}

	public Boolean activateLWC(Player player, TombStoneBlockDTP tStoneBlockDTP) {
		if (!config.isEnableLWC()) {
			return false;
		}
		if (plugin.getLwcPlugin() == null) {
			return false;
		}
		LWC lwc = plugin.getLwcPlugin().getLWC();

// Register the chest + sign as private
		Block block = tStoneBlockDTP.getBlock();
		Block sign = tStoneBlockDTP.getSign();
		// Implemented API Change for LWC 4
		registerLWCProtection(lwc, block, true, tStoneBlockDTP.getOwner());
		if (sign != null) {
			registerLWCProtection(lwc, sign, true, tStoneBlockDTP.getOwner());
		}

		tStoneBlockDTP.setLwcEnabled(true);
		return true;
	}

	public Boolean protectWithLockette(Player player, TombStoneBlockDTP tStoneBlockDTP) {
		if (!config.isEnableLockette()) {
			return false;
		}
		if (plugin.getLockettePlugin() == null) {
			return false;
		}

		Block signBlock = null;

		signBlock = findPlace(tStoneBlockDTP.getBlock(), true);
		if (signBlock == null) {
			plugin.sendMessage(player, "No room for Lockette sign! Chest unsecured!");
			return false;
		}

		signBlock.setType(Material.AIR); // hack to prevent oddness with signs
// popping out of the ground as of
// Bukkit 818
		signBlock.setType(Material.WALL_SIGN);

		String facing = getDirection((getYawTo(signBlock.getLocation(), tStoneBlockDTP
				.getBlock().getLocation()) + 270) % 360);
		if (facing.equals("North")) {
			signBlock.setData((byte) 0x02);
		} else if (facing.equals("South")) {
			signBlock.setData((byte) 0x03);
		} else if (facing.equals("West")) {
			signBlock.setData((byte) 0x04);
		} else if (facing.equals("East")) {
			signBlock.setData((byte) 0x05);
		} else {
			plugin.sendMessage(player, "Error placing Lockette sign! Chest unsecured!");
			return false;
		}

		BlockState signBlockState = null;
		signBlockState = signBlock.getState();
		final Sign sign = (Sign) signBlockState;

		String name = player.getName();
		if (name.length() > 15) {
			name = name.substring(0, 15);
		}
		sign.setLine(0, "[Private]");
		sign.setLine(1, name);
		// Todo Check if replacing plugin with this is really the solution!!!
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				sign.update();
			}
		});
		tStoneBlockDTP.setLocketteSign(sign);
		return true;
	}


	public void deactivateLWC(TombStoneBlockDTP tStoneBlockDTP, boolean force) {
		if (!config.isEnableLWC()) {
			return;
		}
		if (plugin.getLwcPlugin() == null) {
			return;
		}
		LWC lwc = plugin.getLwcPlugin().getLWC();

// Remove the protection on the chest
		Block _block = tStoneBlockDTP.getBlock();
		Protection protection = lwc.findProtection(_block);
		if (protection != null) {
			protection.remove();
			/*// Implemented API Change for LWC 4
						if (plugin.isLWC4()){
							lwc.getPhysicalDatabase().removeProtection(protection.getId());
						} else {
							protection.remove();
							//lwc.getPhysicalDatabase().unregisterProtection(protection.getId());
						}*/
// Set to public instead of removing completely
			if (config.isLwcPublic() && !force) {
				registerLWCProtection(lwc, _block, false, tStoneBlockDTP.getOwner());
			}
		}

// Remove the protection on the sign
		_block = tStoneBlockDTP.getSign();
		if (_block != null) {
			protection = lwc.findProtection(_block);
			if (protection != null) {
				protection.remove();

// Set to public instead of removing completely

				if (config.isLwcPublic() && !force) {
					registerLWCProtection(lwc, _block, false, tStoneBlockDTP.getOwner());
				}
			}
		}
		tStoneBlockDTP.setLwcEnabled(false);
	}

	public void deactivateLockette(TombStoneBlockDTP tStoneBlockDTP) {
		if (tStoneBlockDTP.getLocketteSign() == null) {
			return;
		}
		tStoneBlockDTP.getLocketteSign().getBlock().setType(Material.AIR);
		tStoneBlockDTP.removeLocketteSign();
	}

	public void removeTombStone(TombStoneBlockDTP tStoneBlockDTP, boolean removeList) {
		if (tStoneBlockDTP == null) {
			return;
		}
		log.debug("removeTombStone", tStoneBlockDTP);
		log.debug("removeList", removeList);
		tombStoneBlockList.remove(tStoneBlockDTP.getBlock().getLocation());
		if (tStoneBlockDTP.getLBlock() != null) {
			tombStoneBlockList.remove(tStoneBlockDTP.getLBlock().getLocation());
		}
		if (tStoneBlockDTP.getSign() != null) {
			tombStoneBlockList.remove(tStoneBlockDTP.getSign().getLocation());
		}

// Remove just this tomb from tombStoneListDTP
		ArrayList<TombStoneBlockDTP> tList = playerTombStoneList.get(tStoneBlockDTP.getOwner());
		if (tList != null) {
			tList.remove(tStoneBlockDTP);
			if (tList.size() == 0) {
				playerTombStoneList.remove(tStoneBlockDTP.getOwner());
			}
		}

		if (removeList) {
			tombStoneListDTP.remove(tStoneBlockDTP);
		}

		if (tStoneBlockDTP.getBlock() != null) {
			saveTombStoneList(tStoneBlockDTP.getBlock().getWorld().getName());
		}
	}

	// Load TombStonelist
	public void loadTombStoneList(String world) {
		if (!config.isSaveTombStoneList()) {
			return;
		}
		try {
			File fh = new File(plugin.getDataFolder().getPath(), TOMB_STONE_LOCATION_LIST + "-"
					+ world + ".db");
			if (!fh.exists()) {
				return;
			}
			Scanner scanner = new Scanner(fh);
			int loadedTombStones = 0;
			while (scanner.hasNextLine()) {
				int droppedExperience;
				String line = scanner.nextLine().trim();
				String[] split = line.split(":");
				// block:lblock:sign:time:name:lwc:droppedExperience
				Block block = readBlock(split[0]);
				Block lBlock = readBlock(split[1]);
				Block sign = readBlock(split[2]);
				String owner = split[3];
				long time = Long.valueOf(split[4]);
				boolean lwc = Boolean.valueOf(split[5]);
				if (split.length == 5) {
					droppedExperience = Integer.valueOf(split[6]);
				} else {
					droppedExperience = 0;
				}
				if (block == null || owner == null) {
					log.warning("Invalid entry in database "
							+ fh.getName());
					continue;
				}
				TombStoneBlockDTP tStoneBlockDTP = new TombStoneBlockDTP(block, lBlock, sign, owner,
						time, lwc, droppedExperience);
				offerTombStoneBlockList(tStoneBlockDTP);
				// Used for quick tombStone lookup
				putTombStoneBlockList(block.getLocation(), tStoneBlockDTP);
				if (lBlock != null) {
					putTombStoneBlockList(lBlock.getLocation(), tStoneBlockDTP);
				}
				if (sign != null) {
					putTombStoneBlockList(sign.getLocation(), tStoneBlockDTP);
				}
				ArrayList<TombStoneBlockDTP> pList = getPlayerTombStoneList(owner);
				if (pList == null) {
					pList = new ArrayList<TombStoneBlockDTP>();
					putPlayerTombStoneList(owner, pList);
				}
				pList.add(tStoneBlockDTP);
				loadedTombStones++;
			}
			scanner.close();
			log.info("Successfully loaded " + loadedTombStones + " TombStones for world " + world);
		} catch (IOException e) {
			log.warning("Error loading TombStone list of world" + world, e);
		}
	}

	public void saveTombStoneList(String world) {
		if (!config.isSaveTombStoneList()) {
			return;
		}
		try {
			File fh = new File(plugin.getDataFolder().getPath(), TOMB_STONE_LOCATION_LIST + "-"
					+ world + ".db");
			BufferedWriter bw = new BufferedWriter(new FileWriter(fh));
			for (TombStoneBlockDTP tStoneBlockDTP : getTombStoneListDTP()) {
				// Skip not this world
				if (!tStoneBlockDTP.getBlock().getWorld().getName()
						.equalsIgnoreCase(world)) {
					continue;
				}

				StringBuilder builder = new StringBuilder();

				bw.append(printBlock(tStoneBlockDTP.getBlock()));
				bw.append(":");
				bw.append(printBlock(tStoneBlockDTP.getLBlock()));
				bw.append(":");
				bw.append(printBlock(tStoneBlockDTP.getSign()));
				bw.append(":");
				bw.append(tStoneBlockDTP.getOwner());
				bw.append(":");
				bw.append(String.valueOf(tStoneBlockDTP.getTime()));
				bw.append(":");
				bw.append(String.valueOf(tStoneBlockDTP.getLwcEnabled()));
				bw.append(":");
				bw.append(String.valueOf(tStoneBlockDTP.getDroppedExperience()));

				bw.append(builder.toString());
				bw.newLine();
			}
			bw.close();
			log.info("Successfully saved TombStone list");
		} catch (IOException e) {
			log.warning("Error saving TombStone list", e);
		}
	}


	private Block readBlock(String b) {
		if (b.length() == 0) {
			return null;
		}
		String[] split = b.split(",");
		// world,x,y,z
		World world = plugin.getServer().getWorld(split[0]);
		if (world == null) {
			return null;
		}
		return world.getBlockAt(Integer.valueOf(split[1]),
				Integer.valueOf(split[2]), Integer.valueOf(split[3]));
	}


	private String printBlock(Block b) {
		if (b == null) {
			return "";
		}
		return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + ","
				+ b.getZ();
	}


	/**
	 * Gets the Yaw from one location to another in relation to North.
	 *
	 * @param from
	 * @param to
	 *
	 * @return
	 */
	public double getYawTo(Location from, Location to) {
		final int distX = to.getBlockX() - from.getBlockX();
		final int distZ = to.getBlockZ() - from.getBlockZ();
		double degrees = Math.toDegrees(Math.atan2(-distX, distZ));
		degrees += 180;
		return degrees;
	}

	/**
	 * Converts a rotation to a cardinal direction name. Author: sk89q -
	 * Original function from CommandBook plugin
	 *
	 * @param rot
	 *
	 * @return
	 */
	public static String getDirection(double rot) {

		// Todo remove this when the changes work

/*        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North";
        } else {
            return null;
        }*/

		// Changed to new North Orientation

		if (0 <= rot && rot < 22.5) {
			return "West";
		} else if (22.5 <= rot && rot < 67.5) {
			return "NorthWest";
		} else if (67.5 <= rot && rot < 112.5) {
			return "North";
		} else if (112.5 <= rot && rot < 157.5) {
			return "Northeast";
		} else if (157.5 <= rot && rot < 202.5) {
			return "East";
		} else if (202.5 <= rot && rot < 247.5) {
			return "Southeast";
		} else if (247.5 <= rot && rot < 292.5) {
			return "South";
		} else if (292.5 <= rot && rot < 337.5) {
			return "Southwest";
		} else if (337.5 <= rot && rot < 360.0) {
			return "West";
		} else {
			return null;
		}

	}

	/**
	 * Find a block near the base block to place the tombstone
	 *
	 * @param base
	 * @param CardinalSearch
	 *
	 * @return
	 */
	public Block findPlace(Block base, Boolean CardinalSearch) {
		if (canReplace(base.getType())) {
			return base;
		}
		int baseX = base.getX();
		int baseY = base.getY();
		int baseZ = base.getZ();
		World w = base.getWorld();

		if (CardinalSearch) {
			Block b;
			b = w.getBlockAt(baseX - 1, baseY, baseZ);
			if (canReplace(b.getType())) {
				return b;
			}
			b = w.getBlockAt(baseX + 1, baseY, baseZ);
			if (canReplace(b.getType())) {
				return b;
			}
			b = w.getBlockAt(baseX, baseY, baseZ - 1);
			if (canReplace(b.getType())) {
				return b;
			}
			b = w.getBlockAt(baseX, baseY, baseZ + 1);
			if (canReplace(b.getType())) {
				return b;
			}
			b = w.getBlockAt(baseX, baseY, baseZ);
			if (canReplace(b.getType())) {
				return b;
			}

			return null;
		}

		for (int x = baseX - 1; x < baseX + 1; x++) {
			for (int z = baseZ - 1; z < baseZ + 1; z++) {
				Block b = w.getBlockAt(x, baseY, z);
				if (canReplace(b.getType())) {
					return b;
				}
			}
		}

		return null;
	}

	public Boolean canReplace(Material mat) {
		return (mat == Material.AIR || mat == Material.SAPLING
				|| mat == Material.WATER || mat == Material.STATIONARY_WATER
				|| mat == Material.LAVA || mat == Material.STATIONARY_LAVA
				|| mat == Material.YELLOW_FLOWER || mat == Material.RED_ROSE
				|| mat == Material.BROWN_MUSHROOM
				|| mat == Material.RED_MUSHROOM || mat == Material.FIRE
				|| mat == Material.CROPS || mat == Material.SNOW
				|| mat == Material.SUGAR_CANE || mat == Material.GRAVEL || mat == Material.SAND);
	}

	public String convertTime(long s) { // TODO implement later
		long days = s / 86400;
		int hours = (int) (s % 86400 / 3600);
		int minutes = (int) (s % 86400 % 3600 / 60);
		int seconds = (int) (s % 86400 % 3600 % 60);
		return (days > 1 ? days : "") + (hours < 10 ? "0" : "") + hours + ":"
				+ (minutes < 10 ? "0" : "") + minutes + ":"
				+ (seconds < 10 ? "0" : "") + seconds;
	}

	public void destroyTombStone(Location loc) {
		destroyTombStone(tombStoneBlockList.get(loc));
	}

	public void destroyTombStone(TombStoneBlockDTP tStoneBlockDTP) {
		tStoneBlockDTP.getBlock().getWorld().loadChunk(tStoneBlockDTP.getBlock().getChunk());

		deactivateLWC(tStoneBlockDTP, true);

		if (tStoneBlockDTP.getSign() != null) {
			tStoneBlockDTP.getSign().setType(Material.AIR);
		}
		deactivateLockette(tStoneBlockDTP);
		tStoneBlockDTP.getBlock().setType(Material.AIR);
		if (tStoneBlockDTP.getLBlock() != null) {
			tStoneBlockDTP.getLBlock().setType(Material.AIR);
		}

		removeTombStone(tStoneBlockDTP, true);

		Player p = plugin.getServer().getPlayer(tStoneBlockDTP.getOwner());
		if (p != null) {
			plugin.sendMessage(p, "Your tombstone has been destroyed!");
		}
	}

	public void registerLWCProtection(LWC lwc, Block block, boolean privat, String owner) {
		if (plugin.isLWC4()) {
			Type type;
			if (privat) {
				type = Type.PRIVATE;
			} else {
				type = Type.PUBLIC;
			}
			lwc.getPhysicalDatabase().registerProtection(block.getTypeId(), type, block.getWorld().getName(), owner, "", block.getX(), block.getY(), block.getZ());

		} else {
			int protectionTypes;
			if (privat) {
				protectionTypes = ProtectionTypes.PRIVATE;
			} else {
				protectionTypes = ProtectionTypes.PUBLIC;

			}
			lwc.getPhysicalDatabase().registerProtection(block.getTypeId(), protectionTypes, block.getWorld().getName(), owner, "", block.getX(), block.getY(), block.getZ());

		}

		return;
	}


// Getters and Setters from here on..


	public ConcurrentLinkedQueue<TombStoneBlockDTP> getTombStoneListDTP() {
		return tombStoneListDTP;
	}

	public void offerTombStoneList(TombStoneBlockDTP tombStoneBlock) {
		tombStoneListDTP.offer(tombStoneBlock);
	}


	public void setTombStoneListDTP(ConcurrentLinkedQueue<TombStoneBlockDTP> tombStoneListDTP) {
		this.tombStoneListDTP = tombStoneListDTP;
	}

	public HashMap<Location, TombStoneBlockDTP> getTombStoneBlockList() {
		return tombStoneBlockList;
	}

	public TombStoneBlockDTP getTombStoneBlockList(Location location) {
		return tombStoneBlockList.get(location);
	}


	public void setTombStoneBlockList(HashMap<Location, TombStoneBlockDTP> tombStoneBlockList) {
		this.tombStoneBlockList = tombStoneBlockList;
	}

	public HashMap<String, ArrayList<TombStoneBlockDTP>> getPlayerTombStoneList() {
		return playerTombStoneList;
	}

	public void setPlayerTombStoneList(HashMap<String, ArrayList<TombStoneBlockDTP>> playerTombStoneList) {
		this.playerTombStoneList = playerTombStoneList;
	}

	public void offerTombStoneBlockList(TombStoneBlockDTP tombStoneBlock) {
		tombStoneListDTP.offer(tombStoneBlock);
	}

	public void putTombStoneBlockList(Location location, TombStoneBlockDTP tombStoneBlock) {
		tombStoneBlockList.put(location, tombStoneBlock);
	}

	public ArrayList<TombStoneBlockDTP> getPlayerTombStoneList(String owner) {
		return playerTombStoneList.get(owner);
	}

	public void putPlayerTombStoneList(String owner, ArrayList<TombStoneBlockDTP> pList) {
		playerTombStoneList.put(owner, pList);
	}
}

