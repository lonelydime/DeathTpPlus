package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.models.DeathDetailDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP.DeathRecordType;

import org.bukkit.Bukkit;

/**
 * PluginName: DeathTpPLus Class: DeathLogDTP User: DonRedhorse Date: 25.11.11
 * Time: 19:19
 */
public class DeathLogDTP implements Runnable {
	private static final String DEATH_LOG_FILE = "deathlog.txt";
	private static final String DEATH_LOG_TMP = "deathlog.tmp";
	private static final String DEATH_LOG_BAK = "deathlog.bak";
	private static final String CHARSET = "UTF-8";
	private static final long SAVE_DELAY = 1 * (60 * 20); // 1 minutes
	private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes
	private Map<String, DeathRecordDTP> deaths;
	private static final LoggerDTP log = LoggerDTP.getLogger();
	private String dataFolder;
	private File deathLogFile;

	public DeathLogDTP(DeathTpPlus plugin) {
		deaths = new Hashtable<String, DeathRecordDTP>();
		dataFolder = plugin.getDataFolder() + System.getProperty("file.separator");
		deathLogFile = new File(dataFolder, DEATH_LOG_FILE);
		if (!deathLogFile.exists()) {
			try {
				deathLogFile.createNewFile();
			} catch (IOException e) {
				log.severe("Failed to create death log: " + e.toString());
			}
		}
		load();

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
	}

	private void load() {
		try {
			BufferedReader deathLogReader = new BufferedReader(new InputStreamReader(new FileInputStream(deathLogFile), CHARSET));

			String line = null;
			while ((line = deathLogReader.readLine()) != null) {
				DeathRecordDTP deathRecord = new DeathRecordDTP(line);
				deaths.put(deathRecord.getKey(), deathRecord);
			}

			deathLogReader.close();
		} catch (IOException e) {
			log.severe("Failed to edit death log: " + e.toString());
		}
	}

	public synchronized void save() {
		File tmpDeathLogFile = new File(dataFolder, DEATH_LOG_TMP);
		File bakDeathLogFile = new File(dataFolder, DEATH_LOG_BAK);
		try {
			BufferedWriter tmpDeathLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpDeathLogFile), CHARSET));

			for (DeathRecordDTP deathRecord : deaths.values()) {
				tmpDeathLogWriter.write(deathRecord.toString());
				tmpDeathLogWriter.newLine();
			}

			tmpDeathLogWriter.close();
			if (bakDeathLogFile.exists()) {
				bakDeathLogFile.delete();
			}
			if (!deathLogFile.renameTo(bakDeathLogFile)) {
				throw new Exception("Failed to rename old death log file");
			}
			if (!tmpDeathLogFile.renameTo(deathLogFile)) {
				throw new Exception("Failed to rename death log.");
			}
			tmpDeathLogFile.delete();
		} catch (Exception e) {
			log.severe("Failed to edit death log: " + e.toString());
		}
	}

	public Map<String, Integer> getTotalsByType(DeathRecordType type) {
		Map<String, Integer> totals = new Hashtable<String, Integer>();

		for (DeathRecordDTP record : getRecords()) {
			if (record.getType() == type) {
				if (totals.containsKey(record.getPlayerName())) {
					totals.put(record.getPlayerName(), totals.get(record.getPlayerName()) + record.getCount());
				} else {
					totals.put(record.getPlayerName(), record.getCount());
				}
			}
		}

		return totals;
	}

	public int getTotalByType(String playerName, DeathRecordType type) {
		List<DeathRecordDTP> records = getRecords(playerName);
		int totalDeaths = 0;

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

	public List<DeathRecordDTP> getRecordsByType(String playerName, DeathRecordType type) {
		List<DeathRecordDTP> records = new ArrayList<DeathRecordDTP>();

		for (DeathRecordDTP record : getRecords(playerName)) {
			if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type) {
				records.add(record);
			}
		}

		return records;
	}

	private List<DeathRecordDTP> getRecords(String playerName) {
		List<DeathRecordDTP> records = new ArrayList<DeathRecordDTP>();

		for (DeathRecordDTP deathRecord : getRecords()) {
			if (playerName.equalsIgnoreCase(deathRecord.getPlayerName())) {
				records.add(deathRecord);
			}
		}

		return records;
	}

	private Collection<DeathRecordDTP> getRecords() {
		return deaths.values();
	}

	public void setRecord(DeathDetailDTP deathDetail) {
		if (deathDetail.isPVPDeath()) {
			setRecord(deathDetail.getKiller().getName(), DeathRecordType.kill, deathDetail.getPlayer().getName());
			setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getKiller().getName());
		} else {
			setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getCauseOfDeath().toString());
		}
	}

	private void setRecord(String playerName, DeathRecordType type, String eventName) {
		DeathRecordDTP deathRecord = new DeathRecordDTP(playerName, type, eventName, 1);
		if (deaths.containsKey(deathRecord.getKey())) {
			deaths.get(deathRecord.getKey()).incrementCount();
		} else {
			deaths.put(deathRecord.getKey(), deathRecord);
		}
	}

	@Override
	public void run() {
		save();
	}
}
