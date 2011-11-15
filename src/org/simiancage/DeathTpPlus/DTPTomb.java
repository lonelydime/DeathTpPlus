package org.simiancage.DeathTpPlus;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTomb
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:03
 */

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;
import org.simiancage.DeathTpPlus.workers.DTPTombWorker;

public class DTPTomb {
    protected CopyOnWriteArrayList<Block> signBlocks;
    protected int deaths = 0;
    protected String playerName;
    protected String reason;
    protected Location deathLoc;
    protected Semaphore sema;
    protected Location respawn;
    protected long timeStamp;
    protected Block lastBlock;
    private DTPLogger log;
    private DTPConfig config;

    public DTPTomb() {
        this.signBlocks = new CopyOnWriteArrayList<Block>();
        timeStamp = 0;
        sema = new Semaphore(1, true);
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
    }

    /**
     *
     */
    public DTPTomb(Block sign) throws IllegalArgumentException {
        this();
        addSignBlock(sign);
    }

    /**
     *
     * @return if the user can use death tp.
     */
    public boolean canTeleport() {
        return (System.currentTimeMillis() >= timeStamp);
    }

    /**
     * cut the msg to be sure that it don't exceed 18 char
     *
     * @param message
     * @return
     */
    private String cutMsg(String message) {
        String msg = null;

        if (message != null) {
            int length = message.length();
            if (length > 18)
                msg = message.substring(0, 17);
            else
                msg = message;
        }
        return msg;
    }

    /**
     * update the sign in the game
     */
    private void setLine(final int line, String message) {
        if (!signBlocks.isEmpty()) {

            final String msg = cutMsg(message);
            DeathTpPlus.getBukkitServer().getScheduler().scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                        public void run() {
                            try {
                                sema.acquire();
                            } catch (InterruptedException e) {
                                log.debug("Couldn't acquire semaphore", e);
// e.printStackTrace();
                            }
                            Sign sign;
                            for (Block block : signBlocks) {
                                if (isSign(block)) {
                                    sign = (Sign) block.getState();
                                    sign.setLine(line, msg);
                                    sign.update(true);
                                    try {
                                        Thread.sleep(101);
                                    } catch (InterruptedException e) {

                                    }
                                } else {
                                    signBlocks.remove(block);
                                    log.info("[setLine]DTPTomb of " + playerName
                                            + " Block :(" + block.getWorld().getName() + ", "
                                            + block.getX() + ", " + block.getY() + ", "
                                            + block.getZ() + ") DESTROYED.");
                                }
                            }
                            sema.release();
                        }
                    });
        }
    }

    public void updateDeath() {
        if (!signBlocks.isEmpty()) {
            final String deathNb = cutMsg(deaths + " Deaths");
            final String deathReason = cutMsg(reason);
            DeathTpPlus.getBukkitServer().getScheduler()
                    .scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                        public void run() {
                            try {
                                sema.acquire();
                            } catch (InterruptedException e) {
                                log.debug("Couldn't acquire semaphore", e);
// e.printStackTrace();
                            }
                            Sign sign;
                            log.info("[updateDeath] " + playerName
                                    + " died updating tomb(s).");
                            for (Block block : signBlocks) {
                                if (isSign(block)) {
                                    sign = (Sign) block.getState();
                                    sign.setLine(2, deathNb);
                                    sign.setLine(3, deathReason);
                                    sign.update(true);
                                    try {
                                        Thread.sleep(110);
                                    } catch (InterruptedException e) {

                                    }
                                } else {
                                    signBlocks.remove(block);
                                    block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN,1));
                                    block.setType(Material.AIR);
                                    log.info("[updateDeath]DTPTomb of " + playerName
                                            + " Block :(" + block.getWorld().getName() + ", "
                                            + block.getX() + ", " + block.getY() + ", "
                                            + block.getZ() + ") DESTROYED.");
                                }
                            }
                            sema.release();
                        }
                    });
        }
    }

    /**
     *
     * @return the number of sign block that the tomb has.
     */
    public int getNbSign() {
        return signBlocks.size();
    }

    /**
     * Check every block if they are always a sign.
     */
    public void checkSigns() {
        DeathTpPlus.getBukkitServer().getScheduler()
                .scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                    public void run() {
                        try {
                            sema.acquire();
                        } catch (InterruptedException e) {
                            log.debug("Couldn't acquire semaphore", e);
// e.printStackTrace();
                        }
                        for (Block block : signBlocks)
                            if (!isSign(block)) {
                                signBlocks.remove(block);
                                block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.SIGN,1));
                                block.setType(Material.AIR);
                                log.info("[CheckSigns]DTPTomb of " + playerName
                                        + " Block :(" + block.getWorld().getName() + ", "
                                        + block.getX() + ", " + block.getY() + ", " + block.getZ()
                                        + ") DESTROYED.");
                            }
                        sema.release();
                    }
                });
    }

    /**
     * Increment the number of deaths
     */
    public void addDeath() {
        deaths++;
    }

    /**
     * @param reason
     * the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @param player
     * the player to set
     */
    public void setPlayer(String player) {
        this.playerName = player;
        setLine(1, player);
    }

    /**
     * @param deathLoc
     * the deathLoc to set
     */
    public void setDeathLoc(Location deathLoc) {
        this.deathLoc = deathLoc;
    }

    /**
     * @param respawn
     * the respawn to set
     */
    public void setRespawn(Location respawn) {
        this.respawn = respawn;
    }

    /**
     * @param deaths
     * the deaths to set
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * @param timeStamp
     * the timeStamp to set
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the deathLoc
     */
    public Location getDeathLoc() {
        return deathLoc;
    }

    /**
     * @return the timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Update all the lines.
     */
    public void updateAll() {
        setLine(1, playerName);
        setLine(2, deaths + " Deaths");
        setLine(3, reason);

    }

    /**
     * Update the new block
     */
    public void updateNewBlock() {
        DeathTpPlus.getBukkitServer().getScheduler()
                .scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                    public void run() {
                        Sign sign;
                        Block block = lastBlock;
                        if (isSign(block)) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            sign = (Sign) block.getState();
                            sign.setLine(1, cutMsg(playerName));
                            sign.setLine(2, cutMsg(deaths + " Deaths"));
                            if (reason != null && !reason.isEmpty())
                                sign.setLine(3, cutMsg(reason));
                            sign.update(true);

                        }
                    }
                });
    }

    /**
     *
     * @param sign
     * block to be tested
     * @return if the block is a sign
     */
    private boolean isSign(Block sign) {
        if (sign.getType() == Material.WALL_SIGN || sign.getType() == Material.SIGN
                || sign.getType() == Material.SIGN_POST || sign.getState() instanceof Sign)
            return true;
        else {
            log.severe("DTPTomb of " + playerName + " Block :(" + sign.getWorld().getName()
                    + ", " + sign.getX() + ", " + sign.getY() + ", " + sign.getZ()
                    + ") is not a sign it's a " + sign.getType());
            return false;
        }

    }

    /**
     * @param signBlock
     * the signBlock to set
     */
    public void addSignBlock(Block sign) {
        if (isSign(sign)) {
            try {
                sema.acquire();
            } catch (InterruptedException e) {
                log.debug("Couldn't acquire semaphore", e);
// e.printStackTrace();
            }
            this.signBlocks.add(sign);
            log.info("DTPTomb of " + playerName + " Block :("
                    + sign.getWorld().getName() + ", " + sign.getX() + ", " + sign.getY() + ", "
                    + sign.getZ() + ") Added.");
            lastBlock = sign;
            sema.release();
        } else
            throw new IllegalArgumentException("The block must be a SIGN or WALL_SIGN or SIGN_POST");

    }

    /**
     * Clear the signBlock vector
     */
    public void resetTombBlocks() {
        DeathTpPlus.getBukkitServer().getScheduler()
                .scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                    public void run() {

                        try {
                            sema.acquire();
                        } catch (InterruptedException e) {
                            log.debug("Couldn't acquire semaphore", e);
// e.printStackTrace();
                        }
                        for (Block block : signBlocks) {
                            if (isSign(block))
                            {
                                block.getWorld().dropItem(block.getLocation(),new ItemStack(Material.SIGN,1));
                                block.setType(Material.AIR);
                            }
                            try {
                                Thread.sleep(110);
                            } catch (InterruptedException e) {

                            }
                        }
                        signBlocks.clear();
                        log.info("[resetTombBlocks] DTPTomb of " + playerName
                                + " reseted.");
                        sema.release();
                    }
                });
    }

    /**
     * Remove the given sign from the list.
     *
     * @param sign
     */
    public void removeSignBlock(final Block sign) {
        if (hasSign(sign))
            DeathTpPlus.getBukkitServer().getScheduler()
                    .scheduleAsyncDelayedTask(DTPTombWorker.getInstance().getPlugin(), new Runnable() {
                        public void run() {

                            try {
                                sema.acquire();
                            } catch (InterruptedException e) {
// e.printStackTrace();
                            }
                            signBlocks.remove(sign);
                            log.info("[removeSignBlock]DTPTomb of " + playerName
                                    + " Block :(" + sign.getWorld().getName() + ", " + sign.getX()
                                    + ", " + sign.getY() + ", " + sign.getZ() + ") REMOVED.");
                            sema.release();
                        }
                    });
    }

    /**
     * Check if the block is used as a tomb.
     *
     * @param sign
     * @return
     */
    public boolean hasSign(Block sign) {
        return signBlocks.contains(sign);
    }

    /**
     * @return the player
     */
    public String getPlayer() {
        return playerName;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return the deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * @return the signBlocks
     */
    public CopyOnWriteArrayList<Block> getSignBlocks() {
        return signBlocks;
    }

    /**
     * @return the respawn
     */
    public Location getRespawn() {
        return respawn;
    }

    /**
     * To save the DTPTomb
     *
     * @return
     */
    public DTPTombSave save() {
        return new DTPTombSave(this);
    }

}
