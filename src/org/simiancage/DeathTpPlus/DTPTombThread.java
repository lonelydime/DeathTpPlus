package org.simiancage.DeathTpPlus;

/**
 * PluginName: TODO insert Pluginname here
 * Class: DTPTombThread
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:23
 */

import java.util.Iterator;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DTPTombThread extends Thread {
    private DeathTpPlus plugin;

    public DTPTombThread(DeathTpPlus instance) {
        this.plugin = instance;
    }

    public void run() {
        long cTime = System.currentTimeMillis() / 1000;
        for (Iterator<DTPTombBlock> iter = plugin.tombList.iterator(); iter
                .hasNext();) {
            DTPTombBlock tBlock = iter.next();

// "empty" option checks
            if (plugin.keepUntilEmpty || plugin.removeWhenEmpty) {
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

                    if (plugin.keepUntilEmpty) {
                        if (itemCount > 0)
                            continue;
                    }
                    if (plugin.removeWhenEmpty) {
                        if (itemCount == 0)
                            plugin.destroyCenotaph(tBlock);
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (plugin.securityRemove) {
                Player p = plugin.getServer().getPlayer(tBlock.getOwner());

                if (cTime >= (tBlock.getTime() + plugin.securityTimeout)) {
                    if (tBlock.getLwcEnabled() && plugin.lwcPlugin != null) {
                        plugin.deactivateLWC(tBlock, false);
                        tBlock.setLwcEnabled(false);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your cenotaph!");
                    }
                    if (tBlock.getLocketteSign() != null
                            && plugin.LockettePlugin != null) {
                        plugin.deactivateLockette(tBlock);
                        if (p != null)
                            plugin.sendMessage(p,
                                    "Lockette protection disabled on your cenotaph!");
                    }
                }
            }

// Block removal check
            if (plugin.cenotaphRemove
                    && cTime > (tBlock.getTime() + plugin.removeTime)) {
                plugin.destroyCenotaph(tBlock); // TODO this originally included
// the only instance of
// removeTomb(tblock, false).
// check for bugs caused by the
// change to always true.
                iter.remove();
            }
        }
    }
}

