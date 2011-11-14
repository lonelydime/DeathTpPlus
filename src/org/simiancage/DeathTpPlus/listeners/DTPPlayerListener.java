package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: DTPPlayerListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:01
 */

import org.bukkit.ChatColor;
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
import org.simiancage.DeathTpPlus.DTPTomb;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

public class DTPPlayerListener extends PlayerListener {
    private DeathTpPlus plugin;
    private DTPConfig config;
    private DTPLogger log;

    public DTPPlayerListener(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        log.debug("PlayerListener active");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        log.debug("onPlayerInteract executing");

        if (config.isEnableTomb()){
            Player p = event.getPlayer();
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && (worker.getConfig().getBoolean("allow-tp", true) || worker.hasPerm(p, "tomb.tp",
                    false))) {
                Block block = event.getClickedBlock();
                if (block.getType().equals(Material.WALL_SIGN)
                        || block.getType().equals(Material.SIGN_POST)) {
                    if (worker.hasTomb(p.getName())) {
                        DTPTomb DTPTomb = worker.getTomb(p.getName());
                        if (DTPTomb.hasSign(block)) {
                            Location toTp;
                            if ((toTp = DTPTomb.getDeathLoc()) != null) {
                                if (DTPTomb.canTeleport()) {
                                    if (worker.iConomyCheck(p, "deathtp-price")) {
                                        p.teleport(toTp);
                                        DTPTomb.setTimeStamp(System.currentTimeMillis()
                                                + (int) (worker.getConfig().getDouble("cooldownTp",
                                                5.0D) * 60000));
                                        if (worker.getConfig().getBoolean("reset-deathloc", true))
                                            DTPTomb.setDeathLoc(null);
                                    }
                                } else {
                                    long timeLeft = DTPTomb.getTimeStamp() - System.currentTimeMillis();
                                    p.sendMessage(worker.graveDigger + " You have to wait "
                                            + ChatColor.GOLD + timeLeft / 60000 + " mins "
                                            + (timeLeft / 1000) % 60 + " secs" + ChatColor.WHITE
                                            + " to use the death tp.");
                                }

                            }
                        }
                    }
                }
            }
        }



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
    public void onPlayerQuit(PlayerQuitEvent event) {
        worker.removePermissionNode(event.getPlayer().getName());
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (worker.getConfig().getBoolean("use-tombAsSpawnPoint", true)
                && worker.hasTomb(p.getName())) {
            Location respawn = worker.getTomb(p.getName()).getRespawn();
            if (respawn != null)
                event.setRespawnLocation(respawn);
        }
    }
}
