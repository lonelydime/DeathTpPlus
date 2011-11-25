package org.simiancage.DeathTpPlus.logs;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.DeathLocationRecordDTP;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PluginName: DeathTpPlus
 * Class: DeathLocationsLogDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:25
 */

public class DeathLocationsLogDTP {
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final ConfigDTP config = ConfigDTP.getInstance();
    private static final LoggerDTP log = LoggerDTP.getLogger();
    private DeathTpPlus plugin;
    private String dataFolder;
    private File deathLocationLogFile;

    public DeathLocationsLogDTP(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
        deathLocationLogFile = new File(dataFolder, LOCATION_LOG_FILE);
        if (!deathLocationLogFile.exists()) {
            try {
                deathLocationLogFile.createNewFile();
            }
            catch (IOException e) {
                log.severe("Failed to create death location log", e);
            }
        }
    }

    public DeathLocationRecordDTP getRecord(String playerName)
    {
        DeathLocationRecordDTP deathLocation = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(deathLocationLogFile));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                deathLocation = new DeathLocationRecordDTP(line);
                if (playerName.equalsIgnoreCase(deathLocation.getPlayerName())) {
                    return deathLocation;
                }
                else {
                    deathLocation = null;
                }
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            log.severe("Failed to read death location log", e);
        }

        return deathLocation;
    }

    public void setRecord(DeathDetailDTP deathDetail)
    {
        List<DeathLocationRecordDTP> deathLocations = new ArrayList<DeathLocationRecordDTP>();
        DeathLocationRecordDTP playerRecord = null;

        try {
            BufferedReader deathLocationLogReader = new BufferedReader(new FileReader(deathLocationLogFile));

            String line = null;
            while ((line = deathLocationLogReader.readLine()) != null) {
                DeathLocationRecordDTP deathLocation = new DeathLocationRecordDTP(line);
                if (deathDetail.getPlayer().getName().equalsIgnoreCase(deathLocation.getPlayerName())) {
                    deathLocation.setLocation(deathDetail.getPlayer().getLocation());
                    deathLocation.setWorldName(deathDetail.getPlayer().getWorld().getName());
                    playerRecord = deathLocation;
                }
                deathLocations.add(deathLocation);
            }

            deathLocationLogReader.close();
        }
        catch (IOException e) {
            log.severe("Failed to read death location lo", e);
        }

        if (playerRecord == null) {
            playerRecord = new DeathLocationRecordDTP(deathDetail.getPlayer());
            deathLocations.add(playerRecord);
        }

        try {
            BufferedWriter deathLocationLogWriter = new BufferedWriter(new FileWriter(deathLocationLogFile));

            for (DeathLocationRecordDTP deathLocation : deathLocations) {
                deathLocationLogWriter.write(deathLocation.toString());
                deathLocationLogWriter.newLine();
            }

            deathLocationLogWriter.close();
        }
        catch (IOException e) {
            log.severe("Failed to write death location log", e);
        }
    }
}
