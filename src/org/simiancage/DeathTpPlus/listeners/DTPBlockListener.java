package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: DTPBlockListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 21:59
 */

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.simiancage.DeathTpPlus.DTPTomb;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;
import org.simiancage.DeathTpPlus.workers.DTPTombWorker;

public class DTPBlockListener extends BlockListener {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;
    private DTPTombWorker worker;

    public DTPBlockListener(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        worker = DTPTombWorker.getInstance();
        log.debug("BlockListener active");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        log.debug("onBlockBreak executing");
        Block b = event.getBlock();
        Player p = event.getPlayer();

        if (config.isEnableTombStone())
        {
            if (b.getType() == Material.WALL_SIGN) {
                org.bukkit.material.Sign signData = (org.bukkit.material.Sign) b
                        .getState().getData();
                DTPTombBlock tBlock = plugin.tombBlockList.get(b.getRelative(
                        signData.getAttachedFace()).getLocation());
                if (tBlock == null)
                    return;

                if (tBlock.getLocketteSign() != null) {
                    Sign sign = (Sign) b.getState();
                    event.setCancelled(true);
                    sign.update();
                    return;
                }
            }

            if (b.getType() != Material.CHEST && b.getType() != Material.SIGN_POST)
                return;

            DTPTombBlock tBlock = plugin.tombBlockList.get(b.getLocation());

            if (tBlock == null)
                return;
            Location location = b.getLocation();
            String loc = location.getWorld().getName();
            loc = loc +", x=" + location.getBlock().getX();
            loc = loc +", y=" + location.getBlock().getY();
            loc = loc +", z=" + location.getBlock().getZ();
            if (!config.isAllowTombStoneDestroy() && !plugin.hasPerm(p, "admin", false)) {

                log.debug(p.getName() + " tried to destroy tombstone at "
                        + loc);
                plugin.sendMessage(p, "Tombstone unable to be destroyed");
                event.setCancelled(true);
                return;
            }

            if (plugin.lwcPlugin != null && config.isEnableLWC()
                    && tBlock.getLwcEnabled()) {
                if (tBlock.getOwner().equals(p.getName())
                        || plugin.hasPerm(p, "admin", false)) {
                    plugin.deactivateLWC(tBlock, true);
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            log.debug(p.getName() + " destroyed tombstone at "
                    + loc);
            plugin.removeTomb(tBlock, true);
        }
// ToDo DTPTomb Integration
        if (config.isEnableTomb())
        {
            if (b.getState() instanceof Sign) {
                Block block = b;
                String playerName = event.getPlayer().getName();
                Sign sign = (Sign) block.getState();
                if (sign.getLine(0).indexOf(config.getTombKeyWord()) == 0) {
                    DTPTomb DTPTomb;
                    if (event.getPlayer().hasPermission("deathtpplus.admin.tomb")) {
                        if ((DTPTomb = worker.getTomb(block)) != null) {
                            DTPTomb.removeSignBlock(block);
                            if (config.isResetTombRespawn()) {
                                DTPTomb.setRespawn(null);
                                event.getPlayer().sendMessage(
                                        worker.graveDigger + DTPTomb.getPlayer()
                                                + "'s respawn point has been reset.");
                            }
                        }
                        return;
                    }
                    if (worker.hasTomb(playerName)) {
                        if (!worker.getTomb(playerName).hasSign(block))
                            event.setCancelled(true);
                        else {
                            DTPTomb = worker.getTomb(playerName);
                            DTPTomb.removeSignBlock(block);
                            if (config.isResetTombRespawn()) {
                                DTPTomb.setRespawn(null);
                                event.getPlayer().sendMessage(
                                        worker.graveDigger + DTPTomb.getPlayer()
                                                + "'s respawn point has been reset.");
                            }
                        }
                    } else
                        event.setCancelled(true);
                }

            }
        }
    }

// ToDo edit DTPTomb Integration!

    public void onSignChange(SignChangeEvent e) {
        String line0 = e.getLine(0);
        Player p = e.getPlayer();
        boolean admin = false;
        if (line0.indexOf(config.getTombKeyWord()) == 0) {
            if (!e.getLine(1).isEmpty() && p.hasPermission("deathtpplus.admin.tomb"))
                admin = true;
// Sign check
            DTPTomb DTPTomb = null;
            String deadName = e.getLine(1);
            if (admin) {
                if ((DTPTomb = worker.getTomb(deadName)) == null)
                    try {
                        deadName = p.getServer().getPlayer(e.getLine(1)).getName();
                    } catch (Exception e2) {
                        p.sendMessage(worker.graveDigger + "The player " + e.getLine(1)
                                + "was not found.(The player HAS to be CONNECTED)");
                        return;
                    }
                else
                    deadName = DTPTomb.getPlayer();
            } else
                deadName = e.getPlayer().getName();
            if (DTPTomb != null)
                DTPTomb.checkSigns();
            else if (worker.hasTomb(deadName)) {
                DTPTomb = worker.getTomb(deadName);
                DTPTomb.checkSigns();
            }
            int nbSign = 0;
            if (DTPTomb != null)
                nbSign = DTPTomb.getNbSign();
// max check
            int maxTombs = config.getMaxTomb();
            if (!admin && maxTombs != 0 && (nbSign + 1) > maxTombs) {
                p.sendMessage(worker.graveDigger + "You have reached your DTPTomb limit.");
                e.setCancelled(true);
                return;
            }
// perm and iConomy check
            if ((!admin && !p.hasPermission("deathtpplus.tomb.create"))
                    || !worker.economyCheck(p, "creation-price")) {
                e.setCancelled(true);
                return;
            }
            Block block = e.getBlock();
            try {

                if (DTPTomb != null) {
                    DTPTomb.addSignBlock(block);
                } else {
                    DTPTomb = new DTPTomb(block);
                    DTPTomb.setPlayer(deadName);
                    worker.setTomb(deadName, DTPTomb);
                }
                DTPTomb.updateNewBlock();
                if (config.isUseTombAsRespawnPoint()) {
                    DTPTomb.setRespawn(p.getLocation());
                    if (admin)
                        p.sendMessage(worker.graveDigger + " When " + deadName
                                + " die, he/she will respawn here.");
                    else
                        p.sendMessage(worker.graveDigger + " When you die you'll respawn here.");
                }
            } catch (IllegalArgumentException e2) {
                p.sendMessage(worker.graveDigger
                        + "It's not a good place for a Tomb. Try somewhere else.");
            }

        }

    }
}
