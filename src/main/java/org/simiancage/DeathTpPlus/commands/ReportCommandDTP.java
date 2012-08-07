package org.simiancage.DeathTpPlus.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP;
import org.simiancage.DeathTpPlus.models.DeathRecordDTP.DeathRecordType;

public class ReportCommandDTP implements CommandExecutor {
	private static final int CMDS_PER_PAGE = 8;
	private DeathLogDTP deathLog;
	private LoggerDTP log;

	public ReportCommandDTP(DeathTpPlus plugin) {
		log = LoggerDTP.getLogger();
		deathLog = DeathTpPlus.getDeathLog();
		log.informational("report command registered");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String identifier, String[] args) {
		if (args.length < 1) {
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

		if (sender instanceof Player) {
			if (!((Player) sender).hasPermission("deathtpplus.deathtp.report")) {
				return true;
			}

			List<DeathRecordDTP> records = null;
			if (args[0].equalsIgnoreCase("kills")) {
				records = deathLog.getRecordsByType(((Player) sender).getName(), DeathRecordType.kill);
			}
			else if (args[0].equalsIgnoreCase("deaths")) {
				records = deathLog.getRecordsByType(((Player) sender).getName(), DeathRecordType.death);
			}

			if (records == null || records.isEmpty()) {
				sender.sendMessage("No records found.");
			}
			else {
				Collections.sort(records, new Comparator<DeathRecordDTP>() {
					public int compare(DeathRecordDTP record1, DeathRecordDTP record2) {
						return record1.getEventName().compareToIgnoreCase(record2.getEventName());
					}
				});

				int numPages = records.size() / CMDS_PER_PAGE;
				if (records.size() % CMDS_PER_PAGE != 0) {
					numPages++;
				}
				if (page >= numPages || page < 0) {
					page = 0;
				}

				sender.sendMessage("§c-----[ " + "§fDeathTpPlus Report <" + (page + 1) + "/" + numPages + ">§c ]-----");
				int start = page * CMDS_PER_PAGE;
				int end = start + CMDS_PER_PAGE;
				if (end > records.size()) {
					end = records.size();
				}
				for (int c = start; c < end; c++) {
					DeathRecordDTP record = records.get(c);
					sender.sendMessage(String.format("%s: %s", record.getEventName(), record.getCount()));
				}
			}
		}
		else {
			sender.sendMessage("Console cannot display reports for themselves!");
		}

		return true;
	}
}
