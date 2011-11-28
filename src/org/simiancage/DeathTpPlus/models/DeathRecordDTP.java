package org.simiancage.DeathTpPlus.models;

/**
 * PluginName: DeathTpPlugin
 * Class: DeathRecordDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:42
 */

public class DeathRecordDTP {
    public enum DeathRecordType {
        death, kill
    };

    private String playerName;
    private DeathRecordType type;
    private String eventName;
    private int count;

    public DeathRecordDTP()
    {
    }

    public DeathRecordDTP(String playerName, DeathRecordType type, String eventName, int count)
    {
        this.playerName = playerName;
        this.type = type;
        this.eventName = eventName;
        this.count = count;
    }

    public DeathRecordDTP(String record)
    {
        if (record != null) {
            String[] parts = record.split(":");

            if (parts.length == 4) {
                playerName = parts[0];
                type = DeathRecordType.valueOf(parts[1]);
                eventName = parts[2];
                count = Integer.valueOf(parts[3]);
            }
        }
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public DeathRecordType getType()
    {
        return type;
    }

    public void setType(DeathRecordType type)
    {
        this.type = type;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    @Override
    public String toString()
    {
        return String.format("%s:%s:%s:%d", playerName, type, eventName, count);
    }
}
