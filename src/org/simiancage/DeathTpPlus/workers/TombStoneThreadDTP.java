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
import org.simiancage.DeathTpPlus.objects.TombBlockDTP;
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
        for (Iterator<TombBlockDTP> iter = plugin.tombListDTP.iterator(); iter
                .hasNext();) {
            TombBlockDTP tBlockDTP = iter.next();

// "empty" option checks
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                if (tBlockDTP.getBlock().getState() instanceof Chest) {
                    int itemCount = 0;

                    Chest sChest = (Chest) tBlockDTP.getBlock().getState();
                    Chest lChest = (tBlockDTP.getLBlock() != null) ? (Chest) tBlockDTP
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
                            plugin.destroyTombStone(tBlockDTP);
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (config.isRemoveTombStoneSecurity()) {
                Player p = plugin.getServer().getPlayer(tBlockDTP.getOwner());

                if (cTime >= (tBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))) {
                    if (tBlockDTP.getLwcEnabled() && plugin.lwcPlugin != null) {
                        plugin.deactivateLWC(tBlockDTP, false);
                        tBlockDTP.setLwcEnabled(false);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your tombstone!");
                    }
                    if (tBlockDTP.getLocketteSign() != null
                            && plugin.LockettePlugin != null) {
                        plugin.deactivateLockette(tBlockDTP);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "Lockette protection disabled on your tombstone!");
                    }
                }
            }

// Block removal check
            if (config.isRemoveTombStone()
                    && cTime > (tBlockDTP.getTime() + Long.parseLong(config.getRemoveTombStoneTime()))) {
                plugin.destroyTombStone(tBlockDTP);
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

