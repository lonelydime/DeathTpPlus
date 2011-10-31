package org.simiancage.DeathTpPlus;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTombBlock
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:21
 */

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class DTPTombBlock {
    private Block block;
    private Block lBlock;
    private Block sign;
    private Sign LocketteSign;
    private long time;
    private String owner;
    private boolean lwcEnabled = false;

    public DTPTombBlock(Block block, Block lBlock, Block sign, String owner,
                        long time) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
    }

    DTPTombBlock(Block block, Block lBlock, Block sign, String owner, long time,
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

    void setLocketteSign(Sign sign) {
        this.LocketteSign = sign;
    }

    void removeLocketteSign() {
        this.LocketteSign = null;
    }
}

