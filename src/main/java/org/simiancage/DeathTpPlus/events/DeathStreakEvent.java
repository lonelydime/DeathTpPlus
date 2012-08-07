package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;

public class DeathStreakEvent extends DeathStreakEventDTP {

    public DeathStreakEvent(Player player, Player killer, String message, Integer deaths) {
        super(player, killer, message, deaths);
    }

}
