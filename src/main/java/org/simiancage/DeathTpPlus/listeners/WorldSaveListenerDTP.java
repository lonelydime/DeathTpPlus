package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlugin
 * Class: WorldSaveListenerDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:47
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;


public class WorldSaveListenerDTP implements Listener {
    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        TombWorkerDTP.getInstance().save();
    }

}
