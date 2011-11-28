package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEventDTP;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;

/**
 * PluginName: DeathTpPlugin
 * Class: StreakEventListenerDTP
 * User: DonRedhorse
 * Date: 26.11.11
 * Time: 19:53
 */

public class StreakEventsListenerDTP extends CustomEventListener implements Listener
{
    public StreakEventsListenerDTP()
    {
    }

    public void onDeathStreakEvent(DeathStreakEventDTP event)
    {
    }

    public void onKillStreakEvent(KillStreakEventDTP event)
    {
    }

    public void onCustomEvent(Event event)
    {
        if (event instanceof DeathStreakEventDTP) {
            onDeathStreakEvent((DeathStreakEventDTP) event);
        }
        else if (event instanceof KillStreakEventDTP) {
            onKillStreakEvent((KillStreakEventDTP) event);
        }
    }
}
