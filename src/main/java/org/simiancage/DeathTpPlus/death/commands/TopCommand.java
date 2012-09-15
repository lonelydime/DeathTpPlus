package org.simiancage.DeathTpPlus.death.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.DefaultLogger;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecordDao;
import org.simiancage.DeathTpPlus.death.persistence.DeathRecord.DeathRecordType;

public class TopCommand implements CommandExecutor {
	private static final int CMDS_PER_PAGE = 8;
	private DeathRecordDao deathLog;
	private DefaultLogger log;
	private DeathTpPlus plugin;

	public TopCommand(DeathTpPlus plugin) {
		log = DefaultLogger.getLogger();
		deathLog = DeathTpPlus.getDeathLog();
		log.debug("top command registered");
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String identifier, String[] args) {
		if (!plugin.hasPerm(sender, "deathtpplus.deathtp.top", true) || args.length < 1) {
			return false;
		}

		int page = 0;
		if (args.length > 1) {
			try {
				page = Integer.parseInt(args[1]) - 1;
			}
			catch (NumberFormatException e) {
			}
		}

		Map<String, Integer> totals = null;
		if (args[0].equalsIgnoreCase("kills")) {
			totals = deathLog.getTotalsByType(DeathRecordType.kill);
		}
		else if (args[0].equalsIgnoreCase("deaths")) {
			totals = deathLog.getTotalsByType(DeathRecordType.death);
		}

		if (totals == null || totals.isEmpty()) {
			sender.sendMessage("No records found.");
		}
		else {
			ArrayList<Map.Entry<?, Integer>> sorted = new ArrayList<Map.Entry<?, Integer>>(totals.entrySet());
			Collections.sort(sorted, Collections.reverseOrder(new Comparator<Map.Entry<?, Integer>>() {
				public int compare(Map.Entry<?, Integer> record1, Map.Entry<?, Integer> record2) {
					return record1.getValue().compareTo(record2.getValue());
				}
			}));

			int numPages = sorted.size() / CMDS_PER_PAGE;
			if (sorted.size() % CMDS_PER_PAGE != 0) {
				numPages++;
			}
			if (page >= numPages || page < 0) {
				page = 0;
			}

			sender.sendMessage("§c-----[ " + "§fDeathTpPlus Top <" + (page + 1) + "/" + numPages + ">§c ]-----");
			int start = page * CMDS_PER_PAGE;
			int end = start + CMDS_PER_PAGE;
			if (end > sorted.size()) {
				end = sorted.size();
			}
			for (int c = start; c < end; c++) {
				Entry<?, Integer> record = sorted.get(c);
				sender.sendMessage(String.format("%4d. %s (%s)", c + 1, record.getKey(), record.getValue()));
			}
		}

		return true;
	}
}
