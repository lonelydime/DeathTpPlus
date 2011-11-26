package org.simiancage.DeathTpPlus.listeners;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;

/**
 * PluginName: ${plugin}
 * Class: StreakListenerDTP
 * User: DonRedhorse
 * Date: 26.11.11
 * Time: 19:57
 */

public class StreakListenerDTP extends StreakEventsListenerDTP
{
    private DeathTpPlus plugin;

    public StreakListenerDTP(DeathTpPlus plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onDeathStreakEvent(DeathStreakEventDTP event)
    {
        plugin.getServer().broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }

    @Override
    public void onKillStreakEvent(KillStreakEventDTP event)
    {
        plugin.getServer().broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }
}

