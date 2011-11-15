package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: DTPPlayerListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:01
 */

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;
import org.simiancage.DeathTpPlus.workers.DTPTombWorker;

public class DTPPlayerListener extends PlayerListener {
    private DeathTpPlus plugin;
    private DTPConfig config;
    private DTPLogger log;
    private DTPTombWorker worker;

    public DTPPlayerListener(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        worker = DTPTombWorker.getInstance();
        log.debug("PlayerListener active");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        log.debug("onPlayerInteract executing");

        if (config.isEnableTombStone()){
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            Block b = event.getClickedBlock();

            if (b.getType() != Material.SIGN_POST && b.getType() != Material.CHEST)
                return;
// We'll do quickloot on rightclick of chest if we're going to destroy
// it anyways

            if (b.getType() == Material.CHEST
                    && (!config.isDestroyOnQuickLoot() || config.isAllowTombStoneDestroy()))
                return;
            if (!plugin.hasPerm(event.getPlayer(), "quickloot", false))
                return;

            DTPTombBlock tBlock = plugin.tombBlockList.get(b.getLocation());
            if (tBlock == null || !(tBlock.getBlock().getState() instanceof Chest))
                return;

            if (!tBlock.getOwner().equals(event.getPlayer().getName()))
                return;

            Chest sChest = (Chest) tBlock.getBlock().getState();
            Chest lChest = (tBlock.getLBlock() != null) ? (Chest) tBlock
                    .getLBlock().getState() : null;

            ItemStack[] items = sChest.getInventory().getContents();
            boolean overflow = false;
            for (int cSlot = 0; cSlot < items.length; cSlot++) {
                ItemStack item = items[cSlot];
                if (item == null)
                    continue;
                if (item.getType() == Material.AIR)
                    continue;
                int slot = event.getPlayer().getInventory().firstEmpty();
                if (slot == -1) {
                    overflow = true;
                    break;
                }
                event.getPlayer().getInventory().setItem(slot, item);
                sChest.getInventory().clear(cSlot);
            }
            if (lChest != null) {
                items = lChest.getInventory().getContents();
                for (int cSlot = 0; cSlot < items.length; cSlot++) {
                    ItemStack item = items[cSlot];
                    if (item == null)
                        continue;
                    if (item.getType() == Material.AIR)
                        continue;
                    int slot = event.getPlayer().getInventory().firstEmpty();
                    if (slot == -1) {
                        overflow = true;
                        break;
                    }
                    event.getPlayer().getInventory().setItem(slot, item);
                    lChest.getInventory().clear(cSlot);
                }
            }

            if (!overflow) {
// We're quicklooting, so no need to resume this interaction
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.DENY); // TODO: Minor bug here - if
// you're holding a sign,
// it'll still pop up
                event.setCancelled(true);

                if (config.isDestroyOnQuickLoot()) {
                    plugin.destroyTombStone(tBlock);
                }
            }

// Manually update inventory for the time being.
            event.getPlayer().updateInventory();
            plugin.sendMessage(event.getPlayer(), "Tombstone quicklooted!");
            Location location = tBlock.getBlock().getLocation();
            String loc = location.getWorld().getName();
            loc = loc +", x=" + location.getBlock().getX();
            loc = loc +", y=" + location.getBlock().getY();
            loc = loc +", z=" + location.getBlock().getZ();
            log.debug(event.getPlayer().getName() + " quicklooted tombstone at "
                    + loc);
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (worker.hasTomb(playerName)) {
            worker.getTomb(playerName).checkSigns();
        }
    }


    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (config.isUseTombAsRespawnPoint()
                && worker.hasTomb(p.getName())) {
            Location respawn = worker.getTomb(p.getName()).getRespawn();
            if (respawn != null)
                event.setRespawnLocation(respawn);
        }
    }
}
