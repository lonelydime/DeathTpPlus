package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: BlockListenerDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 21:59
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.onBlockBreakDTP;
import org.simiancage.DeathTpPlus.events.onSignChangeDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;

public class BlockListenerDTP implements Listener {

    private DeathTpPlus plugin;
    private LoggerDTP log;
    private ConfigDTP config;
    private onBlockBreakDTP obb;
    private onSignChangeDTP oss;

    public BlockListenerDTP(DeathTpPlus instance) {
        this.plugin = instance;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.debug("BlockListener active");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (config.isEnableTombStone() && !event.isCancelled()) {
            obb = new onBlockBreakDTP();
            obb.oBBTombStone(plugin, event);


        }

        if (config.isEnableTomb() && !event.isCancelled()) {
            obb = new onBlockBreakDTP();
            obb.oBBTomb(event);


        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        if (config.isEnableTomb() && !event.isCancelled()) {
            oss = new onSignChangeDTP();
            oss.oSCTomb(event);

        }

    }
}
