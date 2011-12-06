package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: DeathTpPlus
 * Class: TombWorkerDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:05
 */


import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombSaveSystemDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;

import java.util.HashMap;


public class TombWorkerDTP {
    private static TombWorkerDTP instance;
    private HashMap<String, TombDTP> tombs = new HashMap<String, TombDTP>();
    private static DeathTpPlus pluginInstance;
    private TombSaveSystemDTP saveSys;
    public String graveDigger = "[" + ChatColor.GOLD + "Gravedigger" + ChatColor.WHITE + "] ";
    private static LoggerDTP log;
    private static ConfigDTP config;

    public static TombWorkerDTP getInstance() {
        if (instance == null) {
            instance = new TombWorkerDTP();
            log = LoggerDTP.getLogger();
            config = ConfigDTP.getInstance();
        }
        return instance;
    }


    private TombWorkerDTP() {
    }

    /**
     * @param pluginInstance the pluginInstance to set
     */

    //ToDo check how to use this together with log formatter
    public void setPluginInstance(DeathTpPlus pluginInstance) {
        this.pluginInstance = pluginInstance;
        String path = pluginInstance.getDataFolder().getPath();
        saveSys = new TombSaveSystemDTP(path);
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
    }
/*        try {

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

 */

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
     * Add the TombDTP
     *
     * @param player
     * @param TombDTP
     */
    public void setTomb(String player, TombDTP TombDTP) {
        tombs.put(player, TombDTP);
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
    public TombDTP getTomb(final String player) {
        TombDTP t = null;

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

    public TombDTP getTomb(Block sign) {
        for (String name : tombs.keySet()) {
            TombDTP result;
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
