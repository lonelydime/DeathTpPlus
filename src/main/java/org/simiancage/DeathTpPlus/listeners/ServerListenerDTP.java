package org.simiancage.DeathTpPlus.listeners;

//import org.bukkit.event.Listener;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.dynmap.DynmapAPI;
import org.getspout.spoutapi.Spout;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.DynMapHelperDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.yi.acru.bukkit.Lockette.Lockette;

public class ServerListenerDTP implements Listener {
	private static DeathTpPlus plugin;

	private LoggerDTP log;
	private ConfigDTP config;
	private boolean missingEconomyWarn = true;
	private boolean dynMapNotReady = true;


	public ServerListenerDTP(DeathTpPlus plugin) {
		this.plugin = plugin;
		log = LoggerDTP.getLogger();
		config = ConfigDTP.getInstance();
		log.debug("ServerListener active");

	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		PluginManager pm = plugin.getServer().getPluginManager();
		Plugin checkVault = pm.getPlugin("Vault");
		if ((checkVault == null) && plugin.isUseVault()) {
			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider == null) {
				plugin.setUseVault(false);
				plugin.setEconomyActive(false);
				log.info("un-hooked from Vault.");
				log.info("as Vault was unloaded / disabled.");
				missingEconomyWarn = true;
			}
		}

		if (event.getPlugin() == plugin.getLwcPlugin()) {
			log.info("LWC plugin lost.");
			plugin.setLwcPlugin(null);
			plugin.setLwcPluginVersion("");
		}

		if (event.getPlugin() == plugin.getLockettePlugin()) {
			log.info("Lockette plugin lost.");
			plugin.setLockettePlugin(null);
		}
		Plugin checkMobArena = pm.getPlugin("MobArena");
		if ((checkMobArena == null) && plugin.isMobArenaEnabled()) {
			log.info("Disabled MobArena protection.");
			log.info("as MobArena was unloaded / disabled.");
			plugin.setMaHandler(null);
			plugin.setMobArenaEnabled(false);
		}
		Plugin checkDynmap = pm.getPlugin("dynmap");
		if ((checkDynmap == null) && plugin.isDynmapEnabled()) {
			log.info("Disabled DnyMap integration.");
			log.info("as DynMap was unloaded / disabled.");
			plugin.setDynmapEnabled(false);
			plugin.setDynMap(null);
			plugin.getDynMapHelperDTP().onDisable();
			plugin.setDynMapHelperDTP(null);
			plugin.setDynmapAPI(null);
			dynMapNotReady = true;

		}
		Plugin checkWorldGuard = pm.getPlugin("WorldGuard");
		if ((checkWorldGuard == null) && plugin.isWorldGuardEnabled()) {
			log.info("Disabling WorldGuard integration.");
			log.info("as WorldGuard was unloaded / disabled.");
			plugin.setWorldGuardEnabled(false);
			plugin.setWorldGuardPlugin(null);

		}


	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		PluginManager pm = plugin.getServer().getPluginManager();
		Plugin checkVault = pm.getPlugin("Vault");
		Plugin checkMobArena = pm.getPlugin("MobArena");
		Plugin checkDynMap = pm.getPlugin("dynmap");
		Plugin checkWorldGuard = pm.getPlugin("WorldGuard");
		Plugin checkSpout = pm.getPlugin("Spout");
		if (checkVault != null && !plugin.isUseVault()) {
			plugin.setUseVault(true);
			log.info("Vault detected");
			log.info("Checking ecnomony providers now!");
		}


		if ((!plugin.isEconomyActive() && plugin.isUseVault())) {

			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				plugin.setEconomy(economyProvider.getProvider());
				plugin.setEconomyActive(true);
				log.info("Economy provider found: " + plugin.getEconomy().getName());

			} else {
				if (missingEconomyWarn) {
					log.warning("No economy provider found.");
					log.info("Still waiting for economy provider to show up.");
					missingEconomyWarn = false;
				}
			}
		}

		if (plugin.getLwcPlugin() == null) {
			if (event.getPlugin().getDescription().getName().equalsIgnoreCase("LWC")) {
				plugin.setLwcPlugin((LWCPlugin) plugin.checkPlugin(event.getPlugin()));
				/*String lwcVersion = event.getPlugin().getDescription().getVersion();
								plugin.setLwcPluginVersion(lwcVersion);
								log.debug("lwcVersion ", lwcVersion);*/
			}
		}

		if (plugin.getLockettePlugin() == null) {
			if (event.getPlugin().getDescription().getName()
					.equalsIgnoreCase("Lockette")) {
				plugin.setLockettePlugin((Lockette) plugin.checkPlugin(event
						.getPlugin()));
			}
		}

		if (checkMobArena != null && !plugin.isMobArenaEnabled()) {
			log.info("Enabling MobArena protection");
			plugin.setMaHandler(new MobArenaHandler());
			plugin.setMobArenaEnabled(true);
		}

		if (checkDynMap != null && !plugin.isDynmapEnabled() && config.isIntegrateIntoDynmap()) {
			log.info("Enabling DynMap Integration");
			plugin.setDynMap(checkDynMap);
			plugin.setDynmapEnabled(true);
		}
		if (checkDynMap != null) {
			if (checkDynMap.isEnabled() && plugin.isDynmapEnabled() && !plugin.isDynmapActive()) {
				DynmapAPI api = (DynmapAPI) checkDynMap; /* Get API */
				log.debug("dynMapApi", api);
				if (api == null) {
					if (dynMapNotReady) {
						log.info("DynMap not ready yet.. waiting");
						dynMapNotReady = false;
					}
				} else {
					log.info("DynMap ready!");
					plugin.setDynmapAPI(api);
					plugin.setDynmapActive(true);
					DynMapHelperDTP dynMapHelperDTP = new DynMapHelperDTP(plugin);
					plugin.setDynMapHelperDTP(dynMapHelperDTP);

					dynMapHelperDTP.onEnable();

				}
			}
		}

		if (checkWorldGuard != null && !plugin.isWorldGuardEnabled()) {
			log.info("Enabling WorldGuard integration");
			plugin.setWorldGuardPlugin((WorldGuardPlugin) checkWorldGuard);
			plugin.setWorldGuardEnabled(true);
		}

		if (checkSpout != null && !plugin.isSpoutEnabled()) {
		    log.info("Enabling Spout integration");
		    plugin.setSpoutPlugin(checkSpout);
		    plugin.setSpoutEnabled(true);
		}
	}
}
