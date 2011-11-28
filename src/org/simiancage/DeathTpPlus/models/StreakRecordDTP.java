package org.simiancage.DeathTpPlus.models;

import java.util.Calendar;
import java.util.Date;

/**
 * PluginName: DeathTpPlus
 * Class: StreakRecordDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:43
 */

public class StreakRecordDTP {
    private String playerName;
    private Integer count;
    private Date multiKillStart;
    private Integer multiKillCount;

    public StreakRecordDTP()
    {
    }

    public StreakRecordDTP(String playerName, int count, Date timeStamp, int multiKillCount)
    {
        this.playerName = playerName;
        this.count = count;
        this.multiKillStart = timeStamp;
        this.multiKillCount = multiKillCount;
    }

    public StreakRecordDTP(String record)
    {
        if (record != null) {
            String[] parts = record.split(":");

            if (parts.length >= 2) {
                playerName = parts[0];
                count = Integer.valueOf(parts[1]);
                if (parts.length == 4) {
                    multiKillStart = new Date(Long.valueOf(parts[2]));
                    multiKillCount = Integer.valueOf(parts[3]);
                }
                else {
                    multiKillStart = new Date(0L);
                    multiKillCount = 0;
                }
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

    public Integer getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public Date getMultiKillStart()
    {
        return multiKillStart;
    }

    public void setMultiKillStart(Date multiKillStart)
    {
        this.multiKillStart = multiKillStart;
    }

    public Integer getMultiKillCount()
    {
        return multiKillCount;
    }

    public void setMultiKillCount(int multiKillCount)
    {
        this.multiKillCount = multiKillCount;
    }

    private Long getElapsedTime()
    {
        Long elapsedTime = Calendar.getInstance().getTimeInMillis();

        if (multiKillStart != null) {
            elapsedTime = Calendar.getInstance().getTimeInMillis() - multiKillStart.getTime();
        }
        return elapsedTime;
    }

    public Boolean isWithinMutiKillTimeWindow(long multiKillTimeWindow)
    {
        return multiKillTimeWindow > getElapsedTime();
    }

    public void updateMultiKillCount(long multiKillTimeWindow)
    {
        if (isWithinMutiKillTimeWindow(multiKillTimeWindow)) {
            multiKillCount++;
        }
        else {
            multiKillStart = new Date();
            multiKillCount = 1;
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s:%d:%tQ:%d", playerName, count, multiKillStart, multiKillCount);
    }
}


