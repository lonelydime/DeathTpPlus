package org.simiancage.DeathTpPlus.models;

/**
 * PluginName: DeathTpPlus
 * Class: TombStoneBlockDTP
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:21
 */

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class TombStoneBlockDTP {
    private Block block;
    private Block lBlock;
    private Block sign;
    private Sign LocketteSign;
    private long time;
    private String owner;
    private boolean lwcEnabled = false;
    private int droppedExperience;

    public TombStoneBlockDTP(Block block, Block lBlock, Block sign, String owner,
                             long time, int droppedExperience) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
        this.droppedExperience = droppedExperience;
    }

    public TombStoneBlockDTP(Block block, Block lBlock, Block sign, String owner, long time,
                             boolean lwc, int droppedExperience) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
        this.droppedExperience = droppedExperience;
        this.lwcEnabled = lwc;
    }

    public int getDroppedExperience() {
        return droppedExperience;
    }

    public long getTime() {
        return time;
    }

    public Block getBlock() {
        return block;
    }

    public Block getLBlock() {
        return lBlock;
    }

    public Block getSign() {
        return sign;
    }

    public Sign getLocketteSign() {
        return LocketteSign;
    }

    public String getOwner() {
        return owner;
    }

    public boolean getLwcEnabled() {
        return lwcEnabled;
    }


    public void setLwcEnabled(boolean val) {
        lwcEnabled = val;
    }

    public void setLocketteSign(Sign sign) {
        this.LocketteSign = sign;
    }

    public void removeLocketteSign() {
        this.LocketteSign = null;
    }

}

