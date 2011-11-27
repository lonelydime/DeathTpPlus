package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: DeathTpPlus
 * Class: TombStoneThreadDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:23
 */

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.util.Iterator;

public class TombStoneThreadDTP extends Thread {
    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;

    public TombStoneThreadDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
    }

    public void run() {
        long cTime = System.currentTimeMillis() / 1000;
        for (Iterator<TombStoneBlockDTP> iter = plugin.tombStoneListDTP.iterator(); iter
                .hasNext();) {
            TombStoneBlockDTP tStoneBlockDTP = iter.next();

// "empty" option checks
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                if (tStoneBlockDTP.getBlock().getState() instanceof Chest) {
                    int itemCount = 0;

                    Chest sChest = (Chest) tStoneBlockDTP.getBlock().getState();
                    Chest lChest = (tStoneBlockDTP.getLBlock() != null) ? (Chest) tStoneBlockDTP
                            .getLBlock().getState() : null;

                    for (ItemStack item : sChest.getInventory().getContents()) {
                        if (item != null)
                            itemCount += item.getAmount();
                    }
                    if (lChest != null && itemCount == 0) {
                        for (ItemStack item : lChest.getInventory()
                                .getContents()) {
                            if (item != null)
                                itemCount += item.getAmount();
                        }
                    }

                    if (config.isKeepTombStoneUntilEmpty()) {
                        if (itemCount > 0)
                            continue;
                    }
                    if (config.isRemoveTombStoneWhenEmpty()) {
                        if (itemCount == 0)
                            plugin.destroyTombStone(tStoneBlockDTP);
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (config.isRemoveTombStoneSecurity()) {
                Player p = plugin.getServer().getPlayer(tStoneBlockDTP.getOwner());

                if (cTime >= (tStoneBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))) {
                    if (tStoneBlockDTP.getLwcEnabled() && plugin.lwcPlugin != null) {
                        plugin.deactivateLWC(tStoneBlockDTP, false);
                        tStoneBlockDTP.setLwcEnabled(false);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your tombstone!");
                    }
                    if (tStoneBlockDTP.getLocketteSign() != null
                            && plugin.LockettePlugin != null) {
                        plugin.deactivateLockette(tStoneBlockDTP);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "Lockette protection disabled on your tombstone!");
                    }
                }
            }

// Block removal check
            if (config.isRemoveTombStone()
                    && cTime > (tStoneBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneTime()))) {
                plugin.destroyTombStone(tStoneBlockDTP);
// TODO this originally included
// the only instance of
// removeTomb(tblock, false).
// check for bugs caused by the
// change to always true.
                iter.remove();
            }
        }
    }
}

