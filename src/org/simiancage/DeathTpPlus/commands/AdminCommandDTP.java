package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.*;
import org.simiancage.DeathTpPlus.objects.TombStoneBlockDTP;

import java.util.ArrayList;


public class AdminCommandDTP implements CommandExecutor {

    private static final String NO_TOMBSTONES_FOUND_FOR = "No tombstones found for ";
    private static final String PERM_DENIED = "Permission Denied";
    private static final String INVALID_TOMBSTONE_ENTRY = "Invalid tombstone entry.";
    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private DeathMessagesDTP deathMessages;
    private TombMessagesDTP tombMessages;
    private TombStoneHelperDTP tombStoneHelper;


    public AdminCommandDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        deathMessages = DeathMessagesDTP.getInstance();
        tombMessages = TombMessagesDTP.getInstance();
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        log.info("dtpadmin command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpadmin command executing");
        if (!plugin.hasPerm(sender, "admin", false)) {
            plugin.sendMessage(sender, PERM_DENIED);
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            plugin.sendMessage(p, "Usage: /dtpadmin list");
            plugin.sendMessage(p, "Usage: /dtpadmin list <playerCaseSensitive>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin find <playerCaseSensitive> <#>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin remove <playerCaseSensitive> <#>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin time <playerCaseSensitive> <#>");
            plugin.sendMessage(p, "Usage: /dtpadmin version");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (!plugin.hasPerm(sender, "admin.list", false)) {
                plugin.sendMessage(p, PERM_DENIED);
                return true;
            }
            if (args.length < 2) {
                if (tombStoneHelper.getPlayerTombStoneList().keySet().isEmpty()) {
                    plugin.sendMessage(p, "There are no tombstones.");
                    return true;
                }
                plugin.sendMessage(p, "Players with tombstones:");
                for (String player : tombStoneHelper.getPlayerTombStoneList().keySet()) {
                    plugin.sendMessage(p, player);
                }
                return true;
            }
            ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, NO_TOMBSTONES_FOUND_FOR + args[1] + ".");
                return true;
            }
            plugin.sendMessage(p, "Tombstone List:");
            int i = 0;
            for (TombStoneBlockDTP tombStoneDTP : pList) {
                i++;
                if (tombStoneDTP.getBlock() == null) {
                    continue;
                }
                int X = tombStoneDTP.getBlock().getX();
                int Y = tombStoneDTP.getBlock().getY();
                int Z = tombStoneDTP.getBlock().getZ();
                plugin.sendMessage(p, " " + i + " - World: "
                        + tombStoneDTP.getBlock().getWorld().getName() + " @(" + X
                        + "," + Y + "," + Z + ")");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("find")) {
            if (!plugin.hasPerm(sender, "admin.find", false)) {
                plugin.sendMessage(p, PERM_DENIED);
                return true;
            }
            ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, NO_TOMBSTONES_FOUND_FOR + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            TombStoneBlockDTP tStoneBlockDTP = pList.get(slot);
            double degrees = (tombStoneHelper.getYawTo(tStoneBlockDTP.getBlock().getLocation(),
                    p.getLocation()) + 270) % 360;
            int X = tStoneBlockDTP.getBlock().getX();
            int Y = tStoneBlockDTP.getBlock().getY();
            int Z = tStoneBlockDTP.getBlock().getZ();
            plugin.sendMessage(p, args[1] + "'s tombstone #" + args[2]
                    + " is at " + X + "," + Y + "," + Z + ", to the "
                    + tombStoneHelper.getDirection(degrees) + ".");
            return true;
        } else if (args[0].equalsIgnoreCase("time")) {
            if (!plugin.hasPerm(p, "admin.time", false)) {
                plugin.sendMessage(p, PERM_DENIED);
                return true;
            }
            if (args.length != 3) {
                return false;
            }
            ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, NO_TOMBSTONES_FOUND_FOR + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            long cTime = System.currentTimeMillis() / 1000;
            TombStoneBlockDTP tStoneBlockDTP = pList.get(slot);
            long secTimeLeft = (tStoneBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))
                    - cTime;
            long remTimeLeft = (tStoneBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneTime())) - cTime;
            if (config.isRemoveTombStoneSecurity() && secTimeLeft > 0) {
                plugin.sendMessage(p, "Security removal: " + secTimeLeft
                        + " seconds.");
            }
            if (config.isRemoveTombStone() & remTimeLeft > 0) {
                plugin.sendMessage(p, "Tombstone removal: " + remTimeLeft
                        + " seconds.");
            }
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                plugin.sendMessage(p, "Keep until empty:"
                        + config.isKeepTombStoneUntilEmpty() + "; remove when empty: "
                        + config.isRemoveTombStoneWhenEmpty());
            }
            return true;
        } else if (args[0].equalsIgnoreCase("version")) {
            String message;
            if (config.isDifferentPluginAvailable()) {
                message = "There is a different plugin version available, please check the logs.";
            } else {
                message = "Your plugin version is fine";
            }
            plugin.sendMessage(p, message);

            if (!(config.getConfigVer().equalsIgnoreCase(config.getConfigCurrent()))) {
                plugin.sendMessage(p, "Your config file is out of date.");
            } else if (config.getConfigVer().equalsIgnoreCase(config.getConfigCurrent())) {
                plugin.sendMessage(p, "Your config file is up to date.");
            }

            if (deathMessages.isDeathMessagesRequiresUpdate()) {
                message = "Your deathmessages are out of date.";
            } else {
                message = "Your deathmessages are up to date.";
            }
            plugin.sendMessage(p, message);

            if (tombMessages.isTombMessagesRequiresUpdate()) {
                message = "Your tombmessages are out of date.";
            } else {
                message = "Your tombmessages are up to date.";
            }
            plugin.sendMessage(p, message);


        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!plugin.hasPerm(sender, "admin.remove", false)) {
                plugin.sendMessage(p, PERM_DENIED);
                return true;
            }
            ArrayList<TombStoneBlockDTP> pList = tombStoneHelper.getPlayerTombStoneList(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, NO_TOMBSTONES_FOUND_FOR + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, INVALID_TOMBSTONE_ENTRY);
                return true;
            }
            TombStoneBlockDTP tStoneBlockDTP = pList.get(slot);
            tombStoneHelper.destroyTombStone(tStoneBlockDTP);

        } else {
            plugin.sendMessage(p, "Usage: /dtpadmin list");
            plugin.sendMessage(p, "Usage: /dtpadmin list <playerCaseSensitive>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin find <playerCaseSensitive> <#>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin remove <playerCaseSensitive> <#>");
            plugin.sendMessage(p,
                    "Usage: /dtpadmin time <playerCaseSensitive> <#>");
            plugin.sendMessage(p, "Usage: /dtpadmin version");
            return true;
        }
        return true;
    }

}
