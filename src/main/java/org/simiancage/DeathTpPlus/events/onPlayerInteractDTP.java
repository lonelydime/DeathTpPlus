package org.simiancage.DeathTpPlus.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.*;
import org.simiancage.DeathTpPlus.logs.DeathLocationsLogDTP;
import org.simiancage.DeathTpPlus.logs.DeathLogDTP;
import org.simiancage.DeathTpPlus.logs.StreakLogDTP;
import org.simiancage.DeathTpPlus.models.DeathLocationRecordDTP;
import org.simiancage.DeathTpPlus.models.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

/**
 * PluginName: DeathTpPlus
 * Class: onPlayerInteractDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:29
 */

public class onPlayerInteractDTP {

    private LoggerDTP log;
    private ConfigDTP config;
    private DeathMessagesDTP deathMessages;
    private DeathTpPlus plugin;
    private TombWorkerDTP tombWorker = TombWorkerDTP.getInstance();
    private TombMessagesDTP tombMessages;
    private DeathLocationsLogDTP deathLocationsLog;
    private StreakLogDTP streakLog;
    private DeathLogDTP deathLog;
    private TombStoneHelperDTP tombStoneHelper;
    private DeathLocationsLogDTP deathLocationLog;


    public onPlayerInteractDTP(DeathTpPlus plugin) {
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        tombWorker = TombWorkerDTP.getInstance();
        deathMessages = DeathMessagesDTP.getInstance();
        tombMessages = TombMessagesDTP.getInstance();
        this.plugin = plugin;
        deathLocationsLog = plugin.getDeathLocationLog();
        streakLog = plugin.getStreakLog();
        deathLog = plugin.getDeathLog();
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        deathLocationLog = plugin.getDeathLocationLog();
    }


    public void playerInteractTombStone(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();

        if (b.getType() != Material.SIGN_POST && b.getType() != Material.CHEST) {
            return;
        }
// We'll do quickloot on rightclick of chest if we're going to destroy
// it anyways

        if (b.getType() == Material.CHEST
                && (!config.isDestroyOnQuickLoot() || config.isAllowTombStoneDestroy())) {
            return;
        }
        if (!plugin.hasPerm(event.getPlayer(), "tombstone.quickloot", false)) {
            return;
        }

        TombStoneBlockDTP tStoneBlockDTP = tombStoneHelper.getTombStoneBlockList(b.getLocation());
        if (tStoneBlockDTP == null || !(tStoneBlockDTP.getBlock().getState() instanceof Chest)) {
            log.debug("Not a Tombstone!");
            return;
        }


        if (!tStoneBlockDTP.getOwner().equals(event.getPlayer().getName())) {
            log.debug("Owner of tombstone", tStoneBlockDTP.getOwner());
            return;
        }


        Chest sChest = (Chest) tStoneBlockDTP.getBlock().getState();
        Chest lChest = (tStoneBlockDTP.getLBlock() != null) ? (Chest) tStoneBlockDTP
                .getLBlock().getState() : null;

        ItemStack[] items = sChest.getInventory().getContents();
        boolean overflow = false;
        for (int cSlot = 0; cSlot < items.length; cSlot++) {
            ItemStack item = items[cSlot];
            if (item == null) {
                continue;
            }
            if (item.getType() == Material.AIR) {
                continue;
            }
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
                if (item == null) {
                    continue;
                }
                if (item.getType() == Material.AIR) {
                    continue;
                }
                int slot = event.getPlayer().getInventory().firstEmpty();
                if (slot == -1) {
                    overflow = true;
                    break;
                }
                event.getPlayer().getInventory().setItem(slot, item);
                lChest.getInventory().clear(cSlot);
            }
        }
        Player player = event.getPlayer();
        int storedDroppedExperience = tStoneBlockDTP.getDroppedExperience();
        int playerTotalExperience = player.getTotalExperience();
        log.debug("Player TotalExperience", playerTotalExperience);
        log.debug("Stored droppedExperience", storedDroppedExperience);
        for (int i = 0; i < storedDroppedExperience; ++i) {
            player.giveExp(1);
            log.debug("Player New TotalExperience", player.getTotalExperience());
        }
        if (!overflow) {
// We're quicklooting, so no need to resume this interaction
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
// TODO: Minor bug here - if
// you're holding a sign,
// it'll still pop up
            event.setCancelled(true);

            if (config.isDestroyOnQuickLoot()) {
                tombStoneHelper.destroyTombStone(tStoneBlockDTP);
            }
        }

// Manually update inventory for the time being.
        event.getPlayer().updateInventory();
        plugin.sendMessage(event.getPlayer(), "Tombstone quicklooted!");
        Location location = tStoneBlockDTP.getBlock().getLocation();
        String loc = location.getWorld().getName();
        loc = loc + ", x=" + location.getBlock().getX();
        loc = loc + ", y=" + location.getBlock().getY();
        loc = loc + ", z=" + location.getBlock().getZ();
        log.debug(event.getPlayer().getName() + " quicklooted tombstone at "
                + loc);
    }

    public void playerInteractTomb(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();

        if (b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN) {
            return;
        }
        Player player = event.getPlayer();
        if (!tombWorker.hasTomb(player.getName())) {
            return;
        }
        TombDTP tomb = tombWorker.getTomb(b);
        if (tomb == null) {
            return;
        }
        if (tomb.hasSign(b)) {
            TeleportHelperDTP teleportHelperDTP = new TeleportHelperDTP(plugin);
            String thisWorld = player.getWorld().getName();
            DeathLocationRecordDTP locationRecord = deathLocationLog.getRecord(player.getName());
            if (locationRecord != null) {
                World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
                if (!teleportHelperDTP.canGoBetween(thisWorld, deathWorld, player)) {
                    player.sendMessage("You do not have the right to travel between worlds via your tomb!");
                    return;
                }

                Location deathLocation = teleportHelperDTP.findTeleportLocation(locationRecord, player);

                if (deathLocation == null) {
                    return;
                }

                deathLocation.setWorld(deathWorld);
                player.teleport(deathLocation);
                TombWorkerDTP tombWorkerDTP = tombWorker.getInstance();
                player.sendMessage(tombWorkerDTP.graveDigger + "I teleported you to your place of death.");

            }
        }
    }
}

