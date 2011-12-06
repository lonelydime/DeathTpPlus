package org.simiancage.DeathTpPlus.logs;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP.DeathRecordType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PluginName: DeathTpPLus
 * Class: DeathLogDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:19
 */

public class DeathLogDTP {
    private static final String DEATH_LOG_FILE = "deathlog.txt";
    private static final String DEATH_LOG_TMP = "deathlog.tmp";
    private static final ConfigDTP config = ConfigDTP.getInstance();
    private static final LoggerDTP log = LoggerDTP.getLogger();
    private String dataFolder;
    private File deathLogFile;
    private DeathTpPlus plugin;

    public DeathLogDTP(DeathTpPlus plugin) {
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
        deathLogFile = new File(dataFolder, DEATH_LOG_FILE);
        if (!deathLogFile.exists()) {
            try {
                deathLogFile.createNewFile();
            } catch (IOException e) {
                log.severe("Failed to create death log", e);
            }
        }
    }

    public int getTotalByType(String playerName, DeathRecordType type) {
        List<DeathRecordDTP> records = getRecords(playerName);
        int totalDeaths = -1;

        for (DeathRecordDTP record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type) {
                totalDeaths += record.getCount();
            }
        }

        return totalDeaths;
    }

    public DeathRecordDTP getRecordByType(String playerName, String eventName, DeathRecordType type) {
        List<DeathRecordDTP> records = getRecords(playerName);

        for (DeathRecordDTP record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type && record.getEventName().equalsIgnoreCase(eventName)) {
                return record;
            }
        }

        return null;
    }

    List<DeathRecordDTP> getRecords(String playerName) {
        List<DeathRecordDTP> records = new ArrayList<DeathRecordDTP>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(deathLogFile));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                DeathRecordDTP deathRecord = new DeathRecordDTP(line);
                if (playerName.equalsIgnoreCase(deathRecord.getPlayerName())) {
                    records.add(deathRecord);
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            log.severe("Failed to read death log", e);
        }

        return records;
    }

    public void setRecord(DeathDetailDTP deathDetail) {
        if (deathDetail.isPVPDeath()) {
            setRecord(deathDetail.getKiller().getName(), DeathRecordType.kill, deathDetail.getPlayer().getName());
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getKiller().getName());
        } else {
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getCauseOfDeath().toString());
        }
    }

    @Deprecated
    void setRecord(String playerName, DeathRecordType type, String eventName) {
        File tmpDeathLogFile = new File(dataFolder, DEATH_LOG_TMP);
        DeathRecordDTP playerRecord = null;

        if (!tmpDeathLogFile.exists()) {
            try {
                tmpDeathLogFile.createNewFile();
            } catch (IOException e) {
                log.severe("Failed to create tmp death log", e);
            }
        }

        try {
            BufferedWriter tmpDeathLogWriter = new BufferedWriter(new FileWriter(tmpDeathLogFile));
            BufferedReader deathLogReader = new BufferedReader(new FileReader(deathLogFile));

            String line = null;
            while ((line = deathLogReader.readLine()) != null) {
                DeathRecordDTP deathRecord = new DeathRecordDTP(line);
                if (playerName.equalsIgnoreCase(deathRecord.getPlayerName()) && type == deathRecord.getType() && eventName.equalsIgnoreCase(deathRecord.getEventName())) {
                    deathRecord.setCount(deathRecord.getCount() + 1);
                    playerRecord = deathRecord;
                }

                tmpDeathLogWriter.write(deathRecord.toString());
                tmpDeathLogWriter.newLine();
            }

            if (playerRecord == null) {
                playerRecord = new DeathRecordDTP(playerName, type, eventName, 1);
                tmpDeathLogWriter.write(playerRecord.toString());
                tmpDeathLogWriter.newLine();
            }

            tmpDeathLogWriter.close();
            deathLogReader.close();

            deathLogFile.delete();
            tmpDeathLogFile.renameTo(deathLogFile);
        } catch (IOException e) {
            log.severe("Failed to edit death log", e);
        }
    }
}

