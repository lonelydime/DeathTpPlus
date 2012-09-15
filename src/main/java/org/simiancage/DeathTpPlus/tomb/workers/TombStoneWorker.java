package org.simiancage.DeathTpPlus.tomb.workers;

/**
 * PluginName: DeathTpPlus
 * Class: TombStoneWorker
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:23
 */

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.commons.ConfigManager;
import org.simiancage.DeathTpPlus.tomb.TombStoneHelper;
import org.simiancage.DeathTpPlus.tomb.models.TombStoneBlock;

import java.util.Iterator;

public class TombStoneWorker extends Thread {
    private DeathTpPlus plugin;
    private ConfigManager config;
    private TombStoneHelper tombStoneHelper;

    public TombStoneWorker(DeathTpPlus instance) {
        this.plugin = instance;
        config = ConfigManager.getInstance();
        tombStoneHelper = TombStoneHelper.getInstance();
    }

    public void run() {
        long cTime = System.currentTimeMillis() / 1000;
        for (Iterator<TombStoneBlock> iter = tombStoneHelper.getTombStoneList().iterator(); iter
                .hasNext(); ) {
            TombStoneBlock tStoneBlock = iter.next();

// "empty" option checks
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                if (tStoneBlock.getBlock().getState() instanceof Chest) {
                    int itemCount = 0;

                    Chest sChest = (Chest) tStoneBlock.getBlock().getState();
                    Chest lChest = (tStoneBlock.getLBlock() != null) ? (Chest) tStoneBlock
                            .getLBlock().getState() : null;

                    for (ItemStack item : sChest.getInventory().getContents()) {
                        if (item != null) {
                            itemCount += item.getAmount();
                        }
                    }
                    if (lChest != null && itemCount == 0) {
                        for (ItemStack item : lChest.getInventory()
                                .getContents()) {
                            if (item != null) {
                                itemCount += item.getAmount();
                            }
                        }
                    }

                    if (config.isKeepTombStoneUntilEmpty()) {
                        if (itemCount > 0) {
                            continue;
                        }
                    }
                    if (config.isRemoveTombStoneWhenEmpty()) {
                        if (itemCount == 0) {
                            tombStoneHelper.destroyTombStone(tStoneBlock);
                        }
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (config.isRemoveTombStoneSecurity()) {
                Player p = plugin.getServer().getPlayer(tStoneBlock.getOwner());

                if (cTime >= (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))) {
                    if (tStoneBlock.getLwcEnabled() && plugin.getLwcPlugin() != null) {
                        tombStoneHelper.deactivateLWC(tStoneBlock, false);
                        tStoneBlock.setLwcEnabled(false);
                        if (p != null) {
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your tombstone!");
                        }
                    }
                    if (tStoneBlock.getLocketteSign() != null
                            && plugin.getLockettePlugin() != null) {
                        tombStoneHelper.deactivateLockette(tStoneBlock);
                        if (p != null) {
                            plugin.sendMessage(p,
                                    "Lockette protection disabled on your tombstone!");
                        }
                    }
                }
            }

// Block removal check
            if (config.isRemoveTombStone()
                    && cTime > (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime()))) {
                tombStoneHelper.destroyTombStone(tStoneBlock);
// TODO this originally included
// the only instance of
// removeTombStone(tblock, false).
// check for bugs caused by the
// change to always true.
                iter.remove();
            }
        }
    }
}

