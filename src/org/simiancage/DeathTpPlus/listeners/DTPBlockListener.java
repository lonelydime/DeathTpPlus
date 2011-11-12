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


import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

public class DTPBlockListener extends BlockListener {

    private DeathTpPlus plugin;
    private DTPLogger log;
    private DTPConfig config;

    public DTPBlockListener(DeathTpPlus instance) {
        this.plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance()
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Player p = event.getPlayer();

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
}
