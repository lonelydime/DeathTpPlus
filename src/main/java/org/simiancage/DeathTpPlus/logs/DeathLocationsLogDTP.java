package org.simiancage.DeathTpPlus.logs;

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
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.DeathLocationRecordDTP;

/**
 * PluginName: DeathTpPlus
 * Class: DeathLocationsLogDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:25
 */

public class DeathLocationsLogDTP implements Runnable {
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final LoggerDTP log = LoggerDTP.getLogger();
    private static final String CHARSET = "UTF-8";
    private static final long SAVE_DELAY = 2 * (60 * 20); // 2 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

    private Map<String, DeathLocationRecordDTP> deathLocations;
    private String dataFolder;
    private File deathLocationLogFile;

    public DeathLocationsLogDTP(DeathTpPlus plugin) {
        deathLocations = new Hashtable<String, DeathLocationRecordDTP>();
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
                DeathLocationRecordDTP deathLocation = new DeathLocationRecordDTP(line);
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

            for (DeathLocationRecordDTP deathLocation : deathLocations.values()) {
                deathLocationLogWriter.write(deathLocation.toString());
                deathLocationLogWriter.newLine();
            }

            deathLocationLogWriter.close();
        }
        catch (IOException e) {
            log.severe("Failed to write death location log: " + e.toString());
        }
    }

    public DeathLocationRecordDTP getRecord(String playerName) {
        return deathLocations.get(playerName);
    }

    public HashMap<Integer, DeathLocationRecordDTP> getAllRecords() {
        int i = 0;
        HashMap<Integer, DeathLocationRecordDTP> deathLocationRecordList = new HashMap<Integer, DeathLocationRecordDTP>();
        for (DeathLocationRecordDTP record : deathLocations.values()) {
            deathLocationRecordList.put(i, record);
        }
        return deathLocationRecordList;
    }

    public void setRecord(DeathDetailDTP deathDetail) {
        deathLocations.put(deathDetail.getPlayer().getName(), new DeathLocationRecordDTP(deathDetail.getPlayer()));
    }

    @Override
    public void run() {
        save();
    }
}
