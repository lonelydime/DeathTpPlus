package org.simiancage.DeathTpPlus.objects;

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

    public TombStoneBlockDTP(Block block, Block lBlock, Block sign, String owner,
                             long time) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
    }

    public TombStoneBlockDTP(Block block, Block lBlock, Block sign, String owner, long time,
                             boolean lwc) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
        this.lwcEnabled = lwc;
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

