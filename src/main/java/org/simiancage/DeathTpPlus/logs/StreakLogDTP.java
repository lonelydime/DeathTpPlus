package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP;
import org.simiancage.DeathTpPlus.helpers.DeathMessagesDTP.DeathEventType;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.StreakRecordDTP;

/**
 * PluginName: DeathTpPlus
 * Class: StreakLogDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:30
 */

public class StreakLogDTP implements Runnable {
    private static final String STREAK_LOG_FILE = "streak.txt";
    private static final ConfigDTP config = ConfigDTP.getInstance();
    private static final LoggerDTP log = LoggerDTP.getLogger();
    private String dataFolder;

    private static final String CHARSET = "UTF-8";
    private static final long SAVE_DELAY = 3 * (60 * 20); // 3 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

    private Map<String, StreakRecordDTP> streaks;
    private File streakLogFile;

    public StreakLogDTP(DeathTpPlus plugin) {
        streaks = new Hashtable<String, StreakRecordDTP>();
        dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
        streakLogFile = new File(dataFolder, STREAK_LOG_FILE);
        if (!streakLogFile.exists()) {
            try {
                streakLogFile.createNewFile();
            }
            catch (IOException e) {
                log.severe("Failed to create streak log: " + e.toString());
            }
        }
        load();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
    }

    private void load() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(streakLogFile), CHARSET));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                StreakRecordDTP streak = new StreakRecordDTP(line);
                streaks.put(streak.getPlayerName(), streak);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            log.severe("Failed to read streak log: " + e.toString());
        }
    }

    public synchronized void save() {
        try {
            BufferedWriter streakLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(streakLogFile), CHARSET));

            for (StreakRecordDTP streak : streaks.values()) {
                streakLogWriter.write(streak.toString());
                streakLogWriter.newLine();
            }

            streakLogWriter.close();
        }
        catch (IOException e) {
            log.severe("Failed to write streak log: " + e.toString());
        }
    }

    public StreakRecordDTP getRecord(String playerName) {
        return streaks.get(playerName);
    }

    public void setRecord(DeathDetailDTP deathDetail) {
        String killerName;
        String victimName = deathDetail.getPlayer().getName();

        if (deathDetail.getCauseOfDeath() == DeathEventType.SUICIDE) {
            killerName = deathDetail.getCauseOfDeath().toString();
        }
        else if (deathDetail.getKiller() != null) {
            killerName = deathDetail.getKiller().getName();
        }
        else {
            return;
        }

        StreakRecordDTP killStreakRecord;
        if (streaks.containsKey(killerName)) {
            killStreakRecord = streaks.get(killerName);
            killStreakRecord.incrementKillCount();
            killStreakRecord.updateMultiKillCount(Long.valueOf(config.getMultiKillTimeWindow()));
        }
        else {
            killStreakRecord = new StreakRecordDTP(killerName, 1, new Date(), 1);
            streaks.put(killerName, killStreakRecord);
        }

        StreakRecordDTP deathStreakRecord;
        if (streaks.containsKey(victimName)) {
            deathStreakRecord = streaks.get(victimName);
            deathStreakRecord.incrementDeathCount();
        }
        else {
            deathStreakRecord = new StreakRecordDTP(victimName, -1, new Date(0L), 0);
            streaks.put(victimName, deathStreakRecord);
        }

        // Check to see if we should announce a streak
        if (deathDetail.getCauseOfDeath() != DeathEventType.SUICIDE && config.isShowStreaks()) {
            // Deaths
            String deathStreakMessage = DeathMessagesDTP.getDeathStreakMessage(deathStreakRecord.getCount());
            if (deathStreakMessage != null) {
                Bukkit.getPluginManager().callEvent(new DeathStreakEventDTP(deathDetail.getPlayer(), deathDetail.getKiller(), deathStreakMessage, deathStreakRecord.getCount()));
            }
            // Multi Kills
            String multiKillMessage = DeathMessagesDTP.getMultiKillMessage(killStreakRecord.getMultiKillCount());
            if (multiKillMessage != null && killStreakRecord.isWithinMutiKillTimeWindow(Long.valueOf(config.getMultiKillTimeWindow()))) {
                Bukkit.getPluginManager().callEvent(new KillStreakEventDTP(deathDetail.getKiller(), deathDetail.getPlayer(), multiKillMessage, killStreakRecord.getMultiKillCount(), true));
            }
            // Kill Streak
            String killStreakMessage = DeathMessagesDTP.getKillStreakMessage(killStreakRecord.getCount());
            if (killStreakMessage != null) {
                Bukkit.getPluginManager().callEvent(new KillStreakEventDTP(deathDetail.getKiller(), deathDetail.getPlayer(), killStreakMessage, killStreakRecord.getCount(), false));
            }
        }
    }

    @Override
    public void run() {
        save();
    }
}
