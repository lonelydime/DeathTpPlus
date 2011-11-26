package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * PluginName: DeathTpPlus
 * Class: KillStreakEventDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:53
 */

@SuppressWarnings("serial")
public class KillStreakEventDTP extends Event
{
    private Player player;
    private Player victim;
    private String message;
    private Integer kills;

    public KillStreakEventDTP(Player player, Player victim, String message, Integer kills)
    {
        super("KillStreakEventDTP");

        this.player = player;
        this.victim = victim;
        this.message = message;
        this.kills = kills;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getVictim() {
        return victim;
    }

    public void setVictim(Player victim) {
        this.victim = victim;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Integer getKills()
    {
        return kills;
    }

    public void setKills(Integer kills)
    {
        this.kills = kills;
    }

}
