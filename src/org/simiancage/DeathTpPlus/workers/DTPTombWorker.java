package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTombWorker
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:05
 */


import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import org.simiancage.DeathTpPlus.DTPTomb;
import org.simiancage.DeathTpPlus.DeathTpPlus;


public class DTPTombWorker extends DTPWorker {
    private static DTPTombWorker instance;
    protected HashMap<String, DTPTomb> tombs = new HashMap<String, DTPTomb>();
    protected static DeathTpPlus pluginInstance;
    protected DTPSaveSystem saveSys;
    public String graveDigger = "[" + ChatColor.GOLD + "Gravedigger" + ChatColor.WHITE + "] ";
    private static DTPLogger log;
    private static DTPConfig config;

    public static DTPTombWorker getInstance() {
        if (instance == null){
            instance = new DTPTombWorker();
            log = DTPLogger.getLogger();
            config = DTPConfig.getInstance();
        }
        return instance;
    }


    /*public static void killInstance() {
        workerLog.info("DTPWorker Instance destroyed");
        for (Handler h : workerLog.getHandlers()) {
            h.close();
            workerLog.removeHandler(h);
        }
        instance = null;
    }
*/
    private DTPTombWorker() {
    }

    /**
     * @param pluginInstance
     * the pluginInstance to set
     */

    //ToDo check how to use this together with log formatter

/*    public void setPluginInstance(DeathTpPlus pluginInstance) {
        this.pluginInstance = pluginInstance;
        String path = pluginInstance.getDataFolder().getPath();
        saveSys = new DTPSaveSystem(path);
        try {

// This block configure the logger with handler and formatter
            File logger = new File(path + File.separator + "log.txt");
            if (logger.exists())
                logger.delete();
            FileHandler fh = new FileHandler(logger.getPath(), true);
            workerLog.addHandler(fh);
            workerLog.setUseParentHandlers(false);
            workerLog.setLevel(Level.ALL);
            DTPLogFormatter formatter = new DTPLogFormatter();
            fh.setFormatter(formatter);

// the following statement is used to log any messages
            workerLog.info("Logger created");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        configInit();
    }

    *//**
     * Check if the config file exist, load it if exist else create it
     *//*
    private void configInit() {
        config = pluginInstance.getConfiguration();
        if (!new File(pluginInstance.getDataFolder().getPath() + File.separator + "config.yml")
                .exists()) {
            config.setProperty("reset-deathloc", true);
            config.setProperty("use-iConomy", true);
            config.setProperty("creation-price", 10.0D);
            config.setProperty("deathtp-price", 50.0D);
            config.setProperty("allow-tp", true);
            config.setProperty("maxTombStone", 0);
            config.setProperty("TombKeyword", "[DTPTomb]");
            config.setProperty("use-tombAsSpawnPoint", true);
            config.setProperty("cooldownTp", 5.0D);
            config.setProperty("reset-respawn", false);
            config.setProperty("maxDeaths", 0);
            config.save();
            workerLog.info("Config created");
        }
        config.load();
    */}

    /**
     * Return the number of tomb the player has.
     *
     * @param player
     * @return
     */
    public int getNbTomb(String player) {
        if (hasTomb(player))
            return tombs.get(player).getNbSign();
        else
            return 0;
    }

    /**
     * Function to check if iConomy is found and the setting used for it.
     *
     * @param player
     * @param action
     * @return
     */
    public boolean iConomyCheck(Player player, String action) {
        if (DTPWorker.getPayement() != null && this.getConfig().getBoolean("use-iConomy", true)
                && !this.hasPerm(player, "tomb.free", false)) {
            if (DTPWorker.getPayement().hasAccount(player.getName())) {
                if (!DTPWorker.getPayement().getAccount(player.getName())
                        .hasEnough(this.getConfig().getDouble(action, 1.0))) {
                    player.sendMessage(graveDigger + ChatColor.RED + "You don't have "
                            + DTPWorker.getPayement().format(this.getConfig().getDouble(action, 1.0))
                            + " to pay me.");
                    return false;
                } else {
                    DTPWorker.getPayement().getAccount(player.getName())
                            .subtract(this.getConfig().getDouble(action, 1.0));
                    if (this.getConfig().getDouble(action, 1.0) != 0)
                        player.sendMessage(graveDigger
                                + DTPWorker.getPayement().format(
                                this.getConfig().getDouble(action, 1.0))
                                + ChatColor.DARK_GRAY + " used to paying me.");
                    return true;
                }

            } else {
                player.sendMessage(graveDigger + ChatColor.RED
                        + "You must have an account to paying me.");
                return false;
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

    *//**
     * @return the pluginInstance
     *//*
    public TombPlugin getPlugin() {
        return pluginInstance;
    }*/

    /**
     * Check if the player have already a tomb
     *
     * @param player
     * @return
     */
    public boolean hasTomb(String player) {
        return tombs.containsKey(player);
    }

    /**
     * Add the DTPTomb
     *
     * @param player
     * @param sign
     */
    public void setTomb(String player, DTPTomb DTPTomb) {
        tombs.put(player, DTPTomb);
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
     *
     * @param player
     * @return the tombs of the player
     */
    public DTPTomb getTomb(final String player) {
        DTPTomb t = null;

        if ((t = tombs.get(player)) != null)
            return t;
        else {
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
                    if (curDelta == 0)
                        break;
                }
            }
            if (found != null)
                return tombs.get(found);
            else
                return null;
        }
    }

    public DTPTomb getTomb(Block sign) {
        for (String name : tombs.keySet()) {
            DTPTomb result;
            if ((result = tombs.get(name)).hasSign(sign))
                return result;
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
