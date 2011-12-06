package org.simiancage.DeathTpPlus.listeners;

/**
 * PluginName: DeathTpPlugin
 * Class: WorldSaveListenerDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:47
 */

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;


public class WorldSaveListenerDTP extends WorldListener {
    @Override
    public void onWorldSave(WorldSaveEvent event) {
        TombWorkerDTP.getInstance().save();
    }

}
