package org.simiancage.DeathTpPlus.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP.DeathRecordType;

public class TopCommandDTP implements CommandExecutor {
	private static final int CMDS_PER_PAGE = 8;
	private DeathLogDTP deathLog;
	private LoggerDTP log;

	public TopCommandDTP(DeathTpPlus plugin) {
		log = LoggerDTP.getLogger();
		deathLog = DeathTpPlus.getDeathLog();
		log.informational("report command registered");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String identifier, String[] args) {
		if (!((Player) sender).hasPermission("deathtpplus.deathtp.top") || args.length < 1) {
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
