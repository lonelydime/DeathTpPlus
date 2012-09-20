package org.simiancage.DeathTpPlus.commons;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.teleport.persistence.DeathLocationDao;
import org.simiancage.DeathTpPlus.teleport.persistence.DeathLocation;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.models.Tomb;
import org.simiancage.DeathTpPlus.tomb.models.TombStoneBlock;
import org.simiancage.DeathTpPlus.tomb.workers.TombWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * PluginName: DeathTpPlus
 * Class: DynMapHelper
 * User: DonRedhorse
 * Date: 04.01.12
 * Time: 17:25
 */

public class DynMapHelper {
	private DeathTpPlus plugin;
	private ConfigManager configManager;
	private DefaultLogger logger;
	private static String configFile = "DynMapConfig.yml";
	private YamlConfiguration cfg = new YamlConfiguration();
	private String pluginPath;
	Plugin dynmap;
	DynmapAPI api;
	MarkerAPI markerapi;
	private double per = 15.0;

	private enum Layers {
		TOMBSTONES("TombStones", "chest", "Treasures of %name%"),
		TOMBS("Tombs", "skull", "Tomb of %name%"),
		DEATHLOCATIONS("LastDeath", "pirateflag", "%name% died here");

		private String name;
		private boolean hideByDefault = false;
		private int layerPrio = 0;
		private int minZoom = 0;
		private String defIcon;
		private String labelFmt = "%name%";
		private boolean isEnabled = true;

		private Layers(String name, String defIcon, String labelFmt) {
			this.name = name;
			this.defIcon = defIcon;
			this.labelFmt = labelFmt;
		}

		@Override
		public String toString() {
			String s = toProperCase(super.toString());
			return s;
		}

		String toProperCase(String s) {
			return s.substring(0, 1).toUpperCase() +
					s.substring(1).toLowerCase();
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public void setEnabled(boolean enabled) {
			isEnabled = enabled;
		}

		public String getDefIcon() {
			return defIcon;
		}

		public void setDefIcon(String defIcon) {
			this.defIcon = defIcon;
		}

		public boolean isHideByDefault() {
			return hideByDefault;
		}

		public void setHideByDefault(boolean hideByDefault) {
			this.hideByDefault = hideByDefault;
		}

		public String getLabelFmt() {
			return labelFmt;
		}

		public void setLabelFmt(String labelFmt) {
			this.labelFmt = labelFmt;
		}

		public int getMinZoom() {
			return minZoom;
		}

		public void setMinZoom(int minZoom) {
			this.minZoom = minZoom;
		}

		public int getLayerPrio() {
			return layerPrio;
		}

		public void setLayerPrio(int layerPrio) {
			this.layerPrio = layerPrio;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}


	public DynMapHelper(DeathTpPlus plugin) {
		this.plugin = plugin;
		configManager = ConfigManager.getInstance();
		logger = DefaultLogger.getLogger();
		pluginPath = plugin.getDataFolder() + System.getProperty("file.separator");
	}

	private abstract class Layer {
		MarkerSet set;
		MarkerIcon deficon;
		String labelfmt;
		Map<String, Marker> markers = new HashMap<String, Marker>();

		public Layer(Layers layer) {
			set = markerapi.getMarkerSet("deathtpplus." + layer);
			if (set == null) {
				set = markerapi.createMarkerSet("deathtpplus." + layer, layer.getName(), null, false);
			} else {
				set.setMarkerSetLabel(layer.getName());
			}
			if (set == null) {
				logger.severe("Error creating " + layer.getName() + " marker set");
				return;
			}
			set.setLayerPriority(layer.getLayerPrio());
			set.setHideByDefault(layer.isHideByDefault());
			int minzoom = layer.getMinZoom();
			if (minzoom > 0) /* Don't call if non-default - lets us work with pre-0.28 dynmap */ {
				set.setMinZoom(minzoom);
			}
			String icon = layer.getDefIcon();
			this.deficon = markerapi.getMarkerIcon(icon);
			if (this.deficon == null) {
				logger.info("Unable to load default icon '" + icon + "' - using default '" + deficon + "'");
				this.deficon = markerapi.getMarkerIcon(layer.getDefIcon());
			}
			labelfmt = layer.getLabelFmt();
		}

		void cleanup() {
			if (set != null) {
				set.deleteMarkerSet();
				set = null;
			}
			markers.clear();
		}


		void updateMarkerSet() {
			Map<String, Marker> newmap = new HashMap<String, Marker>(); /* Build new map */

			Map<String, Location> marks = getMarkers();
			for (String name : marks.keySet()) {
				Location loc = marks.get(name);
				logger.debug("Location", loc);
				String wname = loc.getWorld().getName();

				/* Get location */
				String id = wname + "/" + name;

				String label = labelfmt.replace("%name%", name);

				/* See if we already have marker */
				Marker m = markers.remove(id);
				if (m == null) { /* Not found? Need new one */
					m = set.createMarker(id, label, wname, loc.getX(), loc.getY(), loc.getZ(), deficon, false);
				} else { /* Else, update position if needed */
					m.setLocation(wname, loc.getX(), loc.getY(), loc.getZ());
					m.setLabel(label);
					m.setMarkerIcon(deficon);
				}
				newmap.put(id, m); /* Add to new map */
			}
			/* Now, review old map - anything left is gone */
			for (Marker oldm : markers.values()) {
				oldm.deleteMarker();
			}
			/* And replace with new map */
			markers.clear();
			markers = newmap;
		}

		/* Get current markers, by ID with location */
		public abstract Map<String, Location> getMarkers();
	}

	private class TombLayer extends Layer {
		TombWorker tombs;


		public TombLayer() {
			super(Layers.TOMBS);
			tombs = TombWorker.getInstance();
		}

		/* Get current markers, by ID with location */
		public Map<String, Location> getMarkers() {
			HashMap<String, Location> map = new HashMap<String, Location>();
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				String playerName = player.getName();
				if (tombs.hasTomb(playerName)) {
					try {
						Tomb tomb = tombs.getTomb(playerName);
						List<Block> signBlocks = tomb.getSignBlocks();
						if (!signBlocks.isEmpty()) {
							Block block = signBlocks.get(0);
							map.put(playerName, block.getLocation());
						}
					} catch (NullPointerException e) {
						logger.debug("Caught an exception", e);
					}
				}
			}
			return map;
		}
	}


	private class TombStoneLayer extends Layer {


		public TombStoneLayer() {
			super(Layers.TOMBSTONES);

		}

		/* Get current markers, by ID with location */
		public Map<String, Location> getMarkers() {
			HashMap<String, Location> map = new HashMap<String, Location>();
			TombStoneHelper tombStoneHelper = TombStoneHelper.getInstance();
			HashMap<Location, TombStoneBlock> tombStoneBlockMap = tombStoneHelper.getTombStoneBlockList();
			if (!tombStoneBlockMap.isEmpty()) {
				Iterator<Location> iterator = tombStoneBlockMap.keySet().iterator();
				TombStoneBlock tombStoneBlock;
				String playerName;
				Location location;
				while (iterator.hasNext()) {

					location = iterator.next();
					tombStoneBlock = tombStoneBlockMap.get(location);
					playerName = tombStoneBlock.getOwner();
					map.put(playerName, location);

				}
			}
			return map;
		}
	}

	private class DeathLocationLayer extends Layer {


		public DeathLocationLayer() {
			super(Layers.DEATHLOCATIONS);

		}

		/* Get current markers, by ID with location */
		public Map<String, Location> getMarkers() {
			HashMap<String, Location> map = new HashMap<String, Location>();
			DeathLocationDao deathLocationLog = new DeathLocationDao(plugin);
			HashMap<Integer, DeathLocation> deathLocationsLog = deathLocationLog.getAllRecords();
			if (!deathLocationsLog.isEmpty()) {
				String player;
				Location location;
				for (int i = 0; i < deathLocationsLog.size(); i++) {
					player = deathLocationsLog.get(i).getPlayerName();
					location = deathLocationsLog.get(i).getLocation();
					double x = location.getX();
					double y = location.getY();
					double z = location.getZ();
					String worldName = deathLocationsLog.get(i).getWorldName();
					World world = plugin.getServer().getWorld(worldName);
					location = new Location(world, x, y, z);
					map.put(player, location);
				}
			}

			return map;
		}
	}

	/* TombStone layer settings */
	private Layer tombstonelayer;

	/* Tomb layer settings */
	private Layer tomblayer;

	/* DeathLocation layer settings */
	private Layer deathlocationlayer;


	long updperiod;
	boolean stop;


	private class MarkerUpdate implements Runnable {
		public void run() {
			if (!stop) {
				updateMarkers();
			}
		}
	}

	/* Update markers*/
	private void updateMarkers() {
		logger.debug("Updating Markers");
		if (Layers.TOMBSTONES.isEnabled()) {
			logger.debug("updating tombstonelayer");
			tombstonelayer.updateMarkerSet();
		}
		if (Layers.TOMBS.isEnabled()) {
			logger.debug("updating tomblayer");
			tomblayer.updateMarkerSet();
		}
		if (Layers.DEATHLOCATIONS.isEnabled()) {
			logger.debug("updating deathlocations");
			deathlocationlayer.updateMarkerSet();
		}
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MarkerUpdate(), updperiod);
	}


	public void onEnable() {
		logger.info("initializing DynMap integration");
		/* Get dynmap */
		dynmap = plugin.getDynmap();
		if (dynmap == null) {
			logger.severe("Cannot find DynMap!");
			plugin.setDynmapEnabled(false);
			return;
		}
		if (!(new File(plugin.getDataFolder(), configFile)).exists()) {
			logger.info("Creating default configuration file");
			writeConfig();
		}
		try {
			cfg.load(pluginPath + configFile);
		} catch (IOException e) {
			logger.severe("Can't read the " + configFile + " File!", e);
		} catch (InvalidConfigurationException e) {
			logger.severe("Problem with the configuration in " + configFile + "!", e);
		}
		loadConfig();
		api = plugin.getDynmapAPI();
		activate();

	}

	private void activate() {
		/* Now, get markers API */
		markerapi = api.getMarkerAPI();
		if (markerapi == null) {
			logger.severe("Error loading DynMap marker API!");
			return;
		}

		if (Layers.DEATHLOCATIONS.isEnabled()) {
			if (configManager.isEnableDeathtp()) {
				deathlocationlayer = new DeathLocationLayer();
			} else {
				logger.warning("You need to enable DeathTp to be able to use the DeathLocations Layer!");
				logger.info("Disabling DeathLocations Layer");
				Layers.DEATHLOCATIONS.setEnabled(false);
			}
		}
		if (Layers.TOMBSTONES.isEnabled()) {
			if (configManager.isEnableTombStone()) {
				tombstonelayer = new TombStoneLayer();
			} else {
				logger.warning("You need to enable TombStone to be able to use the TombStone Layer!");
				logger.info("Disabling TombStone Layer");
				Layers.TOMBSTONES.setEnabled(false);
			}
		}
		if (Layers.TOMBS.isEnabled()) {
			if (configManager.isEnableTomb()) {
				tomblayer = new TombLayer();
			} else {
				logger.warning("You need to enable Tomb to be able to use the Tomb Layer!");
				logger.info("Disabling Tomb Layer");
				Layers.TOMBS.setEnabled(false);
			}
		}
		logger.debug("per", per);
		if (per < 2.0) {
			per = 2.0;
		}
		updperiod = (long) (per * 20.0);
		stop = false;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MarkerUpdate(), updperiod);

		logger.info("DynMap Integration is activated");
	}

	public void onDisable() {
		if (tombstonelayer != null) {
			tomblayer.cleanup();
			tomblayer = null;
		}
		if (tomblayer != null) {
			tomblayer.cleanup();
			tomblayer = null;
		}
		if (deathlocationlayer != null) {
			deathlocationlayer.cleanup();
			deathlocationlayer = null;
		}
		stop = true;
	}

	public boolean writeConfig() {
		logger.debug("creating config");
		boolean success = false;
		try {
			PrintWriter stream;
			File folder = plugin.getDataFolder();
			if (folder != null) {
				folder.mkdirs();
			}
			PluginDescriptionFile pdfFile = plugin.getDescription();
			stream = new PrintWriter(pluginPath + configFile);
			logger.debug("starting contents");
//Let's write our config ;)
			stream.println("# " + pdfFile.getName() + " " + pdfFile.getVersion() + " by " + pdfFile.getAuthors().toString());
			stream.println("#");
			stream.println("# Configuration File for DynMap Integration");
			stream.println("#");
			stream.println("# For detailed assistance please visit: " + configManager.getPluginSlug());
			stream.println();
			stream.println("# Seconds between updates");
			stream.println("update:");
			stream.println("    period: " + per);
			stream.println();
			stream.println("layer:");

			for (Layers layer : Layers.values()) {
				stream.println("    " + layer.toString() + ":");
				stream.println("        enable: " + layer.isEnabled());
				stream.println("        name: \"" + layer.getName() + "\"");
				stream.println("         # make layer hidden by default");
				stream.println("        hideByDefault: " + layer.isHideByDefault());
				stream.println("        # (optional) set minimum zoom level when icons should be visible (0=default, any zoom)");
				stream.println("        minZoom: " + layer.getMinZoom());
				stream.println("        # Default Icon for Marker");
				stream.println("        defIcon: \"" + layer.getDefIcon() + "\"");
				stream.println("        # Label format - substitute %name% for players name");
				stream.println("        labelFmt: \"" + layer.getLabelFmt() + "\"");
				stream.println("        # LayerPriority ");
				stream.println("        layerPrio: " + layer.getLayerPrio());
				stream.println();

			}


			stream.println();

			stream.close();

			success = true;

		} catch (FileNotFoundException e) {
			logger.warning("Error saving the " + configFile + ".");
		}

		return success;
	}

	private void loadConfig() {
		per = cfg.getDouble("update.period");
		for (Layers layer : Layers.values()) {
			String node = "layer." + layer + ".";
			layer.setEnabled(cfg.getBoolean(node + "enable"));
			layer.setName(cfg.getString(node + "name"));
			layer.setHideByDefault(cfg.getBoolean(node + "hideByDefault"));
			layer.setMinZoom(cfg.getInt(node + "minZoom"));
			layer.setDefIcon(cfg.getString(node + "defIcon"));
			layer.setLabelFmt(cfg.getString(node + "labelFmt"));
			layer.setLayerPrio(cfg.getInt(node + "layerPrio"));
		}
	}
}

