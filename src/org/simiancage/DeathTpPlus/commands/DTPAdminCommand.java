package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

import java.util.ArrayList;


public class DTPAdminCommand implements CommandExecutor {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPAdminCommand(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        log.info("dtpadmin command registered");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpadmin command executing");
        if (!plugin.hasPerm(sender, "admin", false)) {
            plugin.sendMessage(sender, "Permission Denied");
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
                plugin.sendMessage(p, "Permission Denied");
                return true;
            }
            if (args.length < 2) {
                if (plugin.playerTombList.keySet().isEmpty()) {
                    plugin.sendMessage(p, "There are no tombstones.");
                    return true;
                }
                plugin.sendMessage(p, "Players with tombstones:");
                for (String player : plugin.playerTombList.keySet()) {
                    plugin.sendMessage(p, player);
                }
                return true;
            }
            ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, "No tombstones found for " + args[1] + ".");
                return true;
            }
            plugin.sendMessage(p, "Tombstone List:");
            int i = 0;
            for (DTPTombBlock tomb : pList) {
                i++;
                if (tomb.getBlock() == null)
                    continue;
                int X = tomb.getBlock().getX();
                int Y = tomb.getBlock().getY();
                int Z = tomb.getBlock().getZ();
                plugin.sendMessage(p, " " + i + " - World: "
                        + tomb.getBlock().getWorld().getName() + " @(" + X
                        + "," + Y + "," + Z + ")");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("find")) {
            if (!plugin.hasPerm(sender, "admin.find", false)) {
                plugin.sendMessage(p, "Permission Denied");
                return true;
            }
            ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, "No tombstones found for " + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            DTPTombBlock tBlock = pList.get(slot);
            double degrees = (plugin.getYawTo(tBlock.getBlock().getLocation(),
                    p.getLocation()) + 270) % 360;
            int X = tBlock.getBlock().getX();
            int Y = tBlock.getBlock().getY();
            int Z = tBlock.getBlock().getZ();
            plugin.sendMessage(p, args[1] + "'s tombstone #" + args[2]
                    + " is at " + X + "," + Y + "," + Z + ", to the "
                    + plugin.getDirection(degrees) + ".");
            return true;
        } else if (args[0].equalsIgnoreCase("time")) {
            if (!plugin.hasPerm(p, "admin.time", false)) {
                plugin.sendMessage(p, "Permission Denied");
                return true;
            }
            if (args.length != 3)
                return false;
            ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, "No tombstones found for " + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            long cTime = System.currentTimeMillis() / 1000;
            DTPTombBlock tBlock = pList.get(slot);
            long secTimeLeft = (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))
                    - cTime;
            long remTimeLeft = (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime())) - cTime;
            if (config.isRemoveTombStoneSecurity() && secTimeLeft > 0)
                plugin.sendMessage(p, "Security removal: " + secTimeLeft
                        + " seconds.");
            if (config.isRemoveTombStone() & remTimeLeft > 0)
                plugin.sendMessage(p, "Tombstone removal: " + remTimeLeft
                        + " seconds.");
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty())
                plugin.sendMessage(p, "Keep until empty:"
                        + config.isKeepTombStoneUntilEmpty() + "; remove when empty: "
                        + config.isRemoveTombStoneWhenEmpty());
            return true;
        } else if (args[0].equalsIgnoreCase("version")) {
            String message;
            if (config.isDifferentPluginAvailable()){
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
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!plugin.hasPerm(sender, "admin.remove", false)) {
                plugin.sendMessage(p, "Permission Denied");
                return true;
            }
            ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(args[1]);
            if (pList == null) {
                plugin.sendMessage(p, "No tombstones found for " + args[1] + ".");
                return true;
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(args[2]);
            } catch (Exception e) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            slot -= 1;
            if (slot < 0 || slot >= pList.size()) {
                plugin.sendMessage(p, "Invalid tombstone entry.");
                return true;
            }
            DTPTombBlock tBlock = pList.get(slot);
            plugin.destroyTombStone(tBlock);

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
