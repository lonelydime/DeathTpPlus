package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;

/**
 * This class is for backward compatibility
 * @author rramos
 *
 */
public class KillStreakEventDTP extends org.simiancage.DeathTpPlus.death.events.KillStreakEvent {

    public KillStreakEventDTP(Player player, Player victim, String message, Integer kills, Boolean isMultiKill) {
        super(player, victim, message, kills, isMultiKill);
    }

}
