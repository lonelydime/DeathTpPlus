package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: PlayerListenerDTP
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.helpers.TombStoneHelperDTP;
import org.simiancage.DeathTpPlus.models.TombStoneBlockDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

public class PlayerListenerDTP extends PlayerListener {
    private DeathTpPlus plugin;
    private ConfigDTP config;
    private LoggerDTP log;
    private TombWorkerDTP worker;
    private TombStoneHelperDTP tombStoneHelper;

    public PlayerListenerDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        worker = TombWorkerDTP.getInstance();
        tombStoneHelper = TombStoneHelperDTP.getInstance();
        log.debug("PlayerListener active");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        log.debug("onPlayerInteractDTP executing");

        if (config.isEnableTombStone()) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
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
            for (int i = 1; i < storedDroppedExperience; ++i) {
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

        log.debug("hasTomb", worker.hasTomb(p.getName()));
        if (config.isUseTombAsRespawnPoint() && worker.hasTomb(p.getName())) {
            String deathWorld = event.getPlayer().getWorld().getName();
            Location respawn = worker.getTomb(p.getName()).getRespawn();
            log.debug("respawn", respawn);
            if (respawn != null) {
                boolean worldTravel = false;
                String spawnWorld = respawn.getWorld().getName();
                boolean sameWorld = deathWorld.equalsIgnoreCase(spawnWorld);
                if (config.getAllowWorldTravel().equalsIgnoreCase("yes") || (config.getAllowWorldTravel().equalsIgnoreCase("permissions") && p.hasPermission("deathtpplus.worldtravel"))) {
                    worldTravel = true;
                }
                log.debug("sameWorld", sameWorld);
                log.debug("worldTravel", worldTravel);
                if (sameWorld || worldTravel) {
                    log.debug("Respawn location set to Tomb");
                    event.setRespawnLocation(respawn);
                    plugin.sendMessage(p, worker.graveDigger + "You have been resurrected at your Tomb!");
                } else {
                    log.debug("Respawn location not set to Tomb");
                    plugin.sendMessage(p, worker.graveDigger + "You don't have the right to travel between worlds when you die!");
                }
            }
        }
    }
}
