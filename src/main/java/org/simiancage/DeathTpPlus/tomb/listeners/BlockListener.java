package org.simiancage.DeathTpPlus.tomb.listeners;

/**
 * PluginName: DeathTpPlus
 * Class: BlockListener
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 21:59
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.common.ConfigManager;
import org.simiancage.DeathTpPlus.common.DefaultLogger;
import org.simiancage.DeathTpPlus.tomb.listeners.handlers.BlockBreakHandler;
import org.simiancage.DeathTpPlus.tomb.listeners.handlers.SignChangeHandler;

public class BlockListener implements Listener {

    private DeathTpPlus plugin;
    private DefaultLogger log;
    private ConfigManager config;
    private BlockBreakHandler obb;
    private SignChangeHandler oss;

    public BlockListener(DeathTpPlus instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        config = ConfigManager.getInstance();
        log.debug("BlockListener active");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (config.isEnableTombStone() && !event.isCancelled()) {
            obb = new BlockBreakHandler();
            obb.oBBTombStone(plugin, event);


        }

        if (config.isEnableTomb() && !event.isCancelled()) {
            obb = new BlockBreakHandler();
            obb.oBBTomb(event);


        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        if (config.isEnableTomb() && !event.isCancelled()) {
            oss = new SignChangeHandler();
            oss.oSCTomb(event);

        }

    }
}
