package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: TODO insert Pluginname here
 * Class: DTPTombThread
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:23
 */

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.util.Iterator;

public class DTPTombThread extends Thread {
    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPTombThread(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
    }

    public void run() {
        long cTime = System.currentTimeMillis() / 1000;
        for (Iterator<DTPTombBlock> iter = plugin.tombList.iterator(); iter
                .hasNext();) {
            DTPTombBlock tBlock = iter.next();

// "empty" option checks
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                if (tBlock.getBlock().getState() instanceof Chest) {
                    int itemCount = 0;

                    Chest sChest = (Chest) tBlock.getBlock().getState();
                    Chest lChest = (tBlock.getLBlock() != null) ? (Chest) tBlock
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
                            plugin.destroyTombStone(tBlock);
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (config.isRemoveTombStoneSecurity()) {
                Player p = plugin.getServer().getPlayer(tBlock.getOwner());

                if (cTime >= (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))) {
                    if (tBlock.getLwcEnabled() && plugin.lwcPlugin != null) {
                        plugin.deactivateLWC(tBlock, false);
                        tBlock.setLwcEnabled(false);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your tombstone!");
                    }
                    if (tBlock.getLocketteSign() != null
                            && plugin.LockettePlugin != null) {
                        plugin.deactivateLockette(tBlock);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "Lockette protection disabled on your tombstone!");
                    }
                }
            }

// Block removal check
            if (config.isRemoveTombStone()
                    && cTime > (tBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime()))) {
                plugin.destroyTombStone(tBlock); // TODO this originally included
// the only instance of
// removeTomb(tblock, false).
// check for bugs caused by the
// change to always true.
                iter.remove();
            }
        }
    }
}

