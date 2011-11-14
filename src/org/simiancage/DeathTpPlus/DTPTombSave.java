package org.simiancage.DeathTpPlus;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTombSave
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:28
 */

public class DTPTombSave implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 312699013882578456L;
    protected ArrayList<DTPLocSave> signBlocks = new ArrayList<DTPLocSave>();
    protected int deaths;
    protected String player;
    protected String reason;
    protected DTPLocSave deathLoc;
    protected DTPLocSave respawn;
    private DTPConfig config;
    private DTPLogger log;

    public DTPTombSave(DTPTomb DTPTomb) {
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        for (Block b : DTPTomb.getSignBlocks())
            signBlocks.add(new DTPLocSave(b));
        reason = DTPTomb.getReason();
        player = DTPTomb.getPlayer();
        deaths = DTPTomb.getDeaths();
        if (DTPTomb.getDeathLoc() != null)
            try {
                deathLoc = new DTPLocSave(DTPTomb.getDeathLoc());
            } catch (NullPointerException e) {
                deathLoc = null;
                log.warning("Player :" + player + " : NPE avoided with deathLoc");
            }

        else
            deathLoc = null;
        if (DTPTomb.getRespawn() != null)
            try {
                respawn = new DTPLocSave(DTPTomb.getRespawn());
            } catch (NullPointerException e) {
                respawn = null;
                log.warning("Player :" + player + " : NPE avoided with respawn");
            }
        else
            respawn = null;
    }

    public DTPTomb load() {
        DTPTomb DTPTomb = new DTPTomb();
        if (deathLoc != null)
            DTPTomb.setDeathLoc(deathLoc.getLoc());
        else
            DTPTomb.setDeathLoc(null);

        if (respawn != null)
            DTPTomb.setRespawn(respawn.getLoc());
        else
            DTPTomb.setRespawn(null);
        DTPTomb.setDeaths(deaths);
        DTPTomb.setPlayer(player);
        DTPTomb.setReason(reason);
        for (DTPLocSave loc : signBlocks) {
            try {
                Block b = loc.getBlock();
                if (b != null)
                    DTPTomb.addSignBlock(b);
            } catch (IllegalArgumentException e) {
                log.info("One of the DTPTomb of " + player + " was destroyed. :\n"
                        + loc);
            }
        }
        return DTPTomb;
    }
}

class DTPLocSave implements Serializable {
    /**
     *
     */
    private DTPLogger log;
    private DTPConfig config;
    private static final long serialVersionUID = 8631716113887974333L;
    private double x;
    private double y;
    private double z;
    private String world;

    public DTPLocSave(Location loc) throws NullPointerException {
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        world = loc.getWorld().getName();

    }

    public DTPLocSave(Block block) {
        this(block.getLocation());
    }

    public Location getLoc() {
        return new Location(DeathTpPlus.getBukkitServer().getWorld(world), x, y, z);
    }

    public Block getBlock() {
        World w = DeathTpPlus.getBukkitServer().getWorld(world);
        if (w == null) {
            log.info("World is not loaded :\n" + this);
            return null;
        }
        return w.getBlockAt(getLoc());
    }

    /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
        return "DTPLocSave={World=" + world + ", x=" + x + ", y=" + y + ", z=" + z + "}";

    }
}


