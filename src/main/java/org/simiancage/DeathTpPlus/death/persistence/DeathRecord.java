package org.simiancage.DeathTpPlus.death.persistence;

/**
 * PluginName: DeathTpPlugin
 * Class: DeathRecord
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:42
 */

public class DeathRecord {
    public enum DeathRecordType {
        death, kill
    }

    private String playerName;
    private DeathRecordType type;
    private String eventName;
    private int count;

    public DeathRecord() {
    }

    public DeathRecord(String playerName, DeathRecordType type, String eventName, int count) {
        this.playerName = playerName;
        this.type = type;
        this.eventName = eventName;
        this.count = count;
    }

    public DeathRecord(String record) {
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public DeathRecordType getType() {
        return type;
    }

    public void setType(DeathRecordType type) {
        this.type = type;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount() {
        count++;
    }

    public String getKey() {
        return String.format("%s:%s:%s", playerName, type, eventName);
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s:%d", playerName, type, eventName, count);
    }
}
