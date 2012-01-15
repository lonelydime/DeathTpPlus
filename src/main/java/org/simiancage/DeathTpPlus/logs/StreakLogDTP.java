package org.simiancage.DeathTpPlus.logs;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP.DeathEventType;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.StreakRecordDTP;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PluginName: DeathTpPlus
 * Class: StreakLogDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:30
 */

public class StreakLogDTP {
    private static final String STREAK_LOG_FILE = "streak.txt";
    private static final ConfigDTP config = ConfigDTP.getInstance();
    private static final LoggerDTP log = LoggerDTP.getLogger();
    private String dataFolder;

    private DeathTpPlus plugin;
    private File streakLogFile;

    public StreakLogDTP(DeathTpPlus plugin) {
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
        streakLogFile = new File(dataFolder, STREAK_LOG_FILE);
        if (!streakLogFile.exists()) {
            try {
                streakLogFile.createNewFile();
            } catch (IOException e) {
                log.severe("Failed to create streak log", e);
            }
        }
    }

    public StreakRecordDTP getRecord(String playerName) {
        StreakRecordDTP streak = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                streak = new StreakRecordDTP(line);
                if (playerName.equalsIgnoreCase(streak.getPlayerName())) {
                    return streak;
                } else {
                    streak = null;
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            log.severe("Failed to read streak log", e);
        }

        return streak;
    }

    public void setRecord(DeathDetailDTP deathDetail) {
        String killerName;
        String victimName = deathDetail.getPlayer().getName();

        if (deathDetail.getCauseOfDeath() == DeathEventType.SUICIDE) {
            killerName = deathDetail.getCauseOfDeath().toString();
        } else if (deathDetail.getKiller() != null) {
            killerName = deathDetail.getKiller().getName();
        } else {
            return;
        }

        List<StreakRecordDTP> streakList = new ArrayList<StreakRecordDTP>();

        StreakRecordDTP killStreakRecord = null;
        StreakRecordDTP deathStreakRecord = null;

        // read the file
        try {
            BufferedReader streakLogReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = streakLogReader.readLine()) != null) {
                StreakRecordDTP streak = new StreakRecordDTP(line);
                if (victimName.equalsIgnoreCase(streak.getPlayerName())) {
                    streak.setCount(streak.getCount() > 0 ? -1 : streak.getCount() - 1);
                    deathStreakRecord = streak;
                }
                if (killerName.equalsIgnoreCase(streak.getPlayerName())) {
                    streak.setCount(streak.getCount() < 0 ? 1 : streak.getCount() + 1);
                    streak.updateMultiKillCount(Long.valueOf(config.getMultiKillTimeWindow()));
                    killStreakRecord = streak;
                }
                streakList.add(streak);
            }

            streakLogReader.close();
        } catch (IOException e) {
            log.severe("Failed to read streak log", e);
        }

        if (killStreakRecord == null) {
            killStreakRecord = new StreakRecordDTP(killerName, 1, new Date(), 1);
            streakList.add(killStreakRecord);
        }

        if (deathStreakRecord == null) {
            deathStreakRecord = new StreakRecordDTP(victimName, -1, new Date(0L), 0);
            streakList.add(deathStreakRecord);
        }

        // Check to see if we should announce a streak
        if (deathDetail.getCauseOfDeath() != DeathEventType.SUICIDE && config.isShowStreaks()) {
            // Deaths
            String deathStreakMessage = DeathMessagesDTP.getDeathStreakMessage(deathStreakRecord.getCount());
            if (deathStreakMessage != null) {
                plugin.getServer().getPluginManager().callEvent(new DeathStreakEventDTP(deathDetail.getPlayer(), deathDetail.getKiller(), deathStreakMessage, deathStreakRecord.getCount()));
            }
            // Kills
            String multiKillMessage = DeathMessagesDTP.getMultiKillMessage(killStreakRecord.getMultiKillCount());
            if (multiKillMessage != null && killStreakRecord.isWithinMutiKillTimeWindow(Long.valueOf(config.getMultiKillTimeWindow()))) {
                plugin.getServer().getPluginManager().callEvent(new KillStreakEventDTP(deathDetail.getKiller(), deathDetail.getPlayer(), multiKillMessage, killStreakRecord.getMultiKillCount(), true));
            } else {
                String killStreakMessage = DeathMessagesDTP.getKillStreakMessage(killStreakRecord.getCount());
                if (killStreakMessage != null) {
                    plugin.getServer().getPluginManager().callEvent(new KillStreakEventDTP(deathDetail.getKiller(), deathDetail.getPlayer(), killStreakMessage, killStreakRecord.getCount(), false));
                }
            }
        }

        // Write streaks to file
        try {
            BufferedWriter streakLogWriter = new BufferedWriter(new FileWriter(streakLogFile));

            for (StreakRecordDTP streak : streakList) {
                streakLogWriter.write(streak.toString());
                streakLogWriter.newLine();
            }

            streakLogWriter.close();
        } catch (IOException e) {
            log.severe("Failed to write streak log", e);
        }
    }
}

