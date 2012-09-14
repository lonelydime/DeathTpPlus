package org.simiancage.DeathTpPlus.teleport.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.death.DeathDetail;

/**
 * PluginName: DeathTpPlus
 * Class: DeathLocationDao
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:25
 */

public class DeathLocationDao implements Runnable {
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final DefaultLogger log = DefaultLogger.getLogger();
    private static final String CHARSET = "UTF-8";
    private static final long SAVE_DELAY = 2 * (60 * 20); // 2 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

    private Map<String, DeathLocation> deathLocations;
    private String dataFolder;
    private File deathLocationLogFile;

    public DeathLocationDao(DeathTpPlus plugin) {
        deathLocations = new Hashtable<String, DeathLocation>();
        dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
        deathLocationLogFile = new File(dataFolder, LOCATION_LOG_FILE);
        if (!deathLocationLogFile.exists()) {
            try {
                deathLocationLogFile.createNewFile();
            }
            catch (IOException e) {
                log.severe("Failed to create death location log: " + e.toString());
            }
        }
        load();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
    }

    private void load() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(deathLocationLogFile), CHARSET));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                DeathLocation deathLocation = new DeathLocation(line);
                deathLocations.put(deathLocation.getPlayerName(), deathLocation);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            log.severe("Failed to read death location log: " + e.toString());
        }
    }

    public synchronized void save() {
        try {
            BufferedWriter deathLocationLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deathLocationLogFile), CHARSET));

            for (DeathLocation deathLocation : deathLocations.values()) {
                deathLocationLogWriter.write(deathLocation.toString());
                deathLocationLogWriter.newLine();
            }

            deathLocationLogWriter.close();
        }
        catch (IOException e) {
            log.severe("Failed to write death location log: " + e.toString());
        }
    }

    public DeathLocation getRecord(String playerName) {
        return deathLocations.get(playerName);
    }

    public HashMap<Integer, DeathLocation> getAllRecords() {
        int i = 0;
        HashMap<Integer, DeathLocation> deathLocationRecordList = new HashMap<Integer, DeathLocation>();
        for (DeathLocation record : deathLocations.values()) {
            deathLocationRecordList.put(i, record);
        }
        return deathLocationRecordList;
    }

    public void setRecord(DeathDetail deathDetail) {
        deathLocations.put(deathDetail.getPlayer().getName(), new DeathLocation(deathDetail.getPlayer()));
    }

    @Override
    public void run() {
        save();
    }
}
