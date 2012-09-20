package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;

/**
 * This class is for backward compatibility
 * @author mung3r
 *
 */
public class DeathStreakEventDTP extends org.simiancage.DeathTpPlus.death.events.DeathStreakEvent {

    public DeathStreakEventDTP(Player player, Player killer, String message, Integer deaths) {
        super(player, killer, message, deaths);
    }

}
