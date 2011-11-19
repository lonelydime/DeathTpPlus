package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: BlockListenerDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 21:59
 */

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.onBlockBreakDTP;
import org.simiancage.DeathTpPlus.events.onSignChangeDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

public class BlockListenerDTP extends BlockListener {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private TombWorkerDTP worker;
    private onBlockBreakDTP obb;
    private onSignChangeDTP oss;

    public BlockListenerDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        worker = TombWorkerDTP.getInstance();
        log.debug("BlockListener active");
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {

        Block b = event.getBlock();
        Player p = event.getPlayer();

        if (config.isEnableTombStone() && !event.isCancelled())
        {
            obb = new onBlockBreakDTP();
            obb.oBBTombStone(plugin, event);

           /* if (b.getType() == Material.WALL_SIGN) {
                org.bukkit.material.Sign signData = (org.bukkit.material.Sign) b
                        .getState().getData();
                TombBlockDTP tBlockDTP = plugin.tombBlockList.get(b.getRelative(
                        signData.getAttachedFace()).getLocation());
                if (tBlockDTP == null)
                    return;

                if (tBlockDTP.getLocketteSign() != null) {
                    Sign sign = (Sign) b.getState();
                    event.setCancelled(true);
                    sign.update();
                    return;
                }
            }

            if (b.getType() != Material.CHEST && b.getType() != Material.SIGN_POST)
                return;

            TombBlockDTP tBlockDTP = plugin.tombBlockList.get(b.getLocation());

            if (tBlockDTP == null)
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
                    && tBlockDTP.getLwcEnabled()) {
                if (tBlockDTP.getOwner().equals(p.getName())
                        || plugin.hasPerm(p, "admin", false)) {
                    plugin.deactivateLWC(tBlockDTP, true);
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            log.debug(p.getName() + " destroyed tombstone at "
                    + loc);
            plugin.removeTomb(tBlockDTP, true);*/
        }
// ToDo TombDTP Integration
        if (config.isEnableTomb() && !event.isCancelled())
        {
            obb = new onBlockBreakDTP();
            obb.oBBTomb(plugin, event);

           /* if (b.getState() instanceof Sign) {
                Block block = b;
                String playerName = event.getPlayer().getName();
                Sign sign = (Sign) block.getState();
                if (sign.getLine(0).indexOf(config.getTombKeyWord()) == 0) {
                    TombDTP TombDTP;
                    if (event.getPlayer().hasPermission("deathtpplus.admin.tomb")) {
                        if ((TombDTP = worker.getTomb(block)) != null) {
                            TombDTP.removeSignBlock(block);
                            if (config.isResetTombRespawn()) {
                                TombDTP.setRespawn(null);
                                event.getPlayer().sendMessage(
                                        worker.graveDigger + TombDTP.getPlayer()
                                                + "'s respawn point has been reset.");
                            }
                        }
                        return;
                    }
                    if (worker.hasTomb(playerName)) {
                        if (!worker.getTomb(playerName).hasSign(block))
                            event.setCancelled(true);
                        else {
                            TombDTP = worker.getTomb(playerName);
                            TombDTP.removeSignBlock(block);
                            if (config.isResetTombRespawn()) {
                                TombDTP.setRespawn(null);
                                event.getPlayer().sendMessage(
                                        worker.graveDigger + TombDTP.getPlayer()
                                                + "'s respawn point has been reset.");
                            }
                        }
                    } else
                        event.setCancelled(true);
                }

            }*/
        }
    }

// ToDo edit TombDTP Integration!

    public void onSignChange(SignChangeEvent event) {

        if (config.isEnableTomb() && !event.isCancelled()){
           oss = new onSignChangeDTP();
           oss.oSCTomb(plugin, event);

        }

       /*
        String line0 = event.getLine(0);
        Player p = event.getPlayer();
        boolean admin = false;
        if (line0.indexOf(config.getTombKeyWord()) == 0) {
            if (!event.getLine(1).isEmpty() && p.hasPermission("deathtpplus.admin.tomb"))
                admin = true;
// Sign check
            TombDTP TombDTP = null;
            String deadName = event.getLine(1);
            if (admin) {
                if ((TombDTP = worker.getTomb(deadName)) == null)
                    try {
                        deadName = p.getServer().getPlayer(event.getLine(1)).getName();
                    } catch (Exception e2) {
                        p.sendMessage(worker.graveDigger + "The player " + event.getLine(1)
                                + "was not found.(The player HAS to be CONNECTED)");
                        return;
                    }
                else
                    deadName = TombDTP.getPlayer();
            } else
                deadName = event.getPlayer().getName();
            if (TombDTP != null)
                TombDTP.checkSigns();
            else if (worker.hasTomb(deadName)) {
                TombDTP = worker.getTomb(deadName);
                TombDTP.checkSigns();
            }
            int nbSign = 0;
            if (TombDTP != null)
                nbSign = TombDTP.getNbSign();
// max check
            int maxTombs = config.getMaxTomb();
            if (!admin && maxTombs != 0 && (nbSign + 1) > maxTombs) {
                p.sendMessage(worker.graveDigger + "You have reached your TombDTP limit.");
                event.setCancelled(true);
                return;
            }
// perm and iConomy check
            if ((!admin && !p.hasPermission("deathtpplus.tomb.create"))
                    || !worker.economyCheck(p, "creation-price")) {
                event.setCancelled(true);
                return;
            }
            Block block = event.getBlock();
            try {

                if (TombDTP != null) {
                    TombDTP.addSignBlock(block);
                } else {
                    TombDTP = new TombDTP(block);
                    TombDTP.setPlayer(deadName);
                    worker.setTomb(deadName, TombDTP);
                }
                TombDTP.updateNewBlock();
                if (config.isUseTombAsRespawnPoint()) {
                    TombDTP.setRespawn(p.getLocation());
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
*/
    }
}
