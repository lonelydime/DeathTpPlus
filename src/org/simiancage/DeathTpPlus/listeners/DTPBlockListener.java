package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: DTPBlockListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 21:59
 */

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.MoofIT.Minecraft.Cenotaph.Cenotaph;
import com.MoofIT.Minecraft.Cenotaph.TombBlock;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPBlockListener extends BlockListener {

    private DeathTpPlus plugin;

    public DTPBlockListener(DeathTpPlus instance) {
        this.plugin = instance;
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

        if (plugin.noDestroy && !plugin.hasPerm(p, "admin", false)) {
            plugin.logEvent(p.getName() + " tried to destroy tombstone at "
                    + b.getLocation());
            plugin.sendMessage(p, "Tombstone unable to be destroyed");
            event.setCancelled(true);
            return;
        }

        if (plugin.lwcPlugin != null && plugin.lwcEnable
                && tBlock.getLwcEnabled()) {
            if (tBlock.getOwner().equals(p.getName())
                    || plugin.hasPerm(p, "admin", false)) {
                plugin.deactivateLWC(tBlock, true);
            } else {
                event.setCancelled(true);
                return;
            }
        }
        plugin.logEvent(p.getName() + " destroyed tombstone at "
                + b.getLocation());
        plugin.removeTomb(tBlock, true);
    }
}
