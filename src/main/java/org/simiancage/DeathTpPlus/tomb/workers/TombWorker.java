package org.simiancage.DeathTpPlus.tomb.workers;

/**
 * PluginName: DeathTpPlus
 * Class: TombWorker
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:05
 */


import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.ConfigManager;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.models.Tomb;
import org.simiancage.DeathTpPlus.tomb.persistence.TombSaveSystem;

import java.util.HashMap;


public class TombWorker {
	private static TombWorker instance;
	private HashMap<String, Tomb> tombs = new HashMap<String, Tomb>();
	private static DeathTpPlus pluginInstance;
	private TombSaveSystem saveSys;
	public String graveDigger = "[" + ChatColor.GOLD + "Gravedigger" + ChatColor.WHITE + "] ";
	private static DefaultLogger log;
	private static ConfigManager config;

	public static TombWorker getInstance() {
		if (instance == null) {
			instance = new TombWorker();
			log = DefaultLogger.getLogger();
			config = ConfigManager.getInstance();
		}
		return instance;
	}


	private TombWorker() {
	}

	/**
	 * @param pluginInstance the pluginInstance to set
	 */


	public void setPluginInstance(DeathTpPlus pluginInstance) {
		TombWorker.pluginInstance = pluginInstance;
		String path = pluginInstance.getDataFolder().getPath();
		saveSys = new TombSaveSystem(path);
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
	}


	/**
	 * Return the number of tomb the player has.
	 *
	 * @param player
	 *
	 * @return
	 */
	public int getNbTomb(String player) {
		if (hasTomb(player)) {
			return tombs.get(player).getNbSign();
		} else {
			return 0;
		}
	}

	/**
	 * Function to check if economy is found and the setting used for it.
	 *
	 * @param player
	 * @param action
	 *
	 * @return
	 */
	public boolean economyCheck(Player player, String action) {
		double tombCost = Double.parseDouble(config.getTombCost());
		if ((tombCost > 0) && !player.hasPermission("deathtpplus.tomb.free")) {


			if (pluginInstance.isEconomyActive()) {
				if (pluginInstance.getEconomy().getBalance(player.getName()) > tombCost) {
					pluginInstance.getEconomy().withdrawPlayer(player.getName(), tombCost);

					player.sendMessage(graveDigger + tombCost + ChatColor.DARK_GRAY + " used to paying me.");
					return true;
				} else {
					player.sendMessage(graveDigger + ChatColor.RED + "You don't have  " + tombCost + " to pay me.");
					return false;
				}
			}
		}
		return true;
	}


	/*   *//**
	 * @return the config
	 *//*
    public Configuration getConfig() {
        return config;
    }
*/

	/**
	 * @return the pluginInstance
	 */
	public DeathTpPlus getPlugin() {
		return pluginInstance;
	}


	/**
	 * Check if the player have already a tomb
	 *
	 * @param player
	 *
	 * @return
	 */
	public boolean hasTomb(String player) {
		return tombs.containsKey(player);
	}

	/**
	 * Add the Tomb
	 *
	 * @param player
	 * @param Tomb
	 */
	public void setTomb(String player, Tomb tomb) {
		tombs.put(player, tomb);
	}

	/**
	 * Remove the tomb of the player.
	 *
	 * @param player
	 */
	public void removeTomb(String player) {
		tombs.remove(player);
	}

	/**
	 * @param player
	 *
	 * @return the tombs of the player
	 */
	public Tomb getTomb(final String player) {
		Tomb t = null;

		if ((t = tombs.get(player)) != null) {
			return t;
		} else {
			String found = null;
			String lowerName = player.toLowerCase();
			int delta = Integer.MAX_VALUE;
			for (String p : tombs.keySet()) {
				if (p.toLowerCase().startsWith(lowerName)) {
					int curDelta = p.length() - lowerName.length();
					if (curDelta < delta) {
						found = p;
						delta = curDelta;
					}
					if (curDelta == 0) {
						break;
					}
				}
			}
			if (found != null) {
				return tombs.get(found);
			} else {
				return null;
			}
		}
	}

	public Tomb getTomb(Block sign) {
		for (String name : tombs.keySet()) {
			Tomb result;
			if ((result = tombs.get(name)).hasSign(sign)) {
				return result;
			}
		}
		return null;
	}

	public synchronized void save() {
		saveSys.save(tombs);
		log.info("[SAVE] Tombs saved !");
	}

	public synchronized void load() {
		tombs = saveSys.load();
		log.info("[LOAD] Tombs loaded !");
	}

}
