package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;

public class KillStreakEvent extends KillStreakEventDTP {

    public KillStreakEvent(Player player, Player victim, String message, Integer kills, Boolean isMultiKill) {
        super(player, victim, message, kills, isMultiKill);
    }

}
