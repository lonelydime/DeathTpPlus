package org.simiancage.DeathTpPlus.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

/**
 * PluginName: DeathTpPlus
 * Class: onSignChangeDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:33
 */

public class onSignChangeDTP {

    private LoggerDTP log;
    private ConfigDTP config;
    private DeathTpPlus plugin;
    private TombWorkerDTP tombWorkerDTP;

    public onSignChangeDTP() {
        this.log = LoggerDTP.getLogger();
        this.config = ConfigDTP.getInstance();
        this.tombWorkerDTP = TombWorkerDTP.getInstance();
    }

    public void oSCTomb(SignChangeEvent event) {
        log.debug("onSignChange Tomb executing");
        String line0 = event.getLine(0);
        Player p = event.getPlayer();
        boolean admin = false;
        if (line0.indexOf(config.getTombKeyWord()) == 0) {
            if (!event.getLine(1).isEmpty() && p.hasPermission("deathtpplus.admin.tomb")) {
                admin = true;
            }
// Sign check
            TombDTP TombDTP = null;
            String deadName = event.getLine(1);
            if (admin) {
                if ((TombDTP = tombWorkerDTP.getTomb(deadName)) == null) {
                    try {
                        deadName = p.getServer().getPlayer(event.getLine(1)).getName();
                    } catch (Exception e2) {
                        p.sendMessage(tombWorkerDTP.graveDigger + "The player " + event.getLine(1)
                                + "was not found.(The player HAS to be CONNECTED)");
                        return;
                    }
                } else {
                    deadName = TombDTP.getPlayer();
                }
            } else {
                deadName = event.getPlayer().getName();
            }
            log.debug("deadName", deadName);
            if (TombDTP != null) {
                TombDTP.checkSigns();
            } else if (tombWorkerDTP.hasTomb(deadName)) {
                TombDTP = tombWorkerDTP.getTomb(deadName);
                TombDTP.checkSigns();
            }
            int nbSign = 0;
            if (TombDTP != null) {
                nbSign = TombDTP.getNbSign();
            }
// max check
            int maxTombs = config.getMaxTomb();
            if (!admin && maxTombs != 0 && (nbSign + 1) > maxTombs) {
                p.sendMessage(tombWorkerDTP.graveDigger + "You have reached your Tomb limit.");
                event.setCancelled(true);
                return;
            }
// perm and economy check
            if ((!admin && !p.hasPermission("deathtpplus.tomb.create"))
                    || !tombWorkerDTP.economyCheck(p, "creation-price")) {
                event.setCancelled(true);
                return;
            }
            Block block = event.getBlock();
            try {

                if (TombDTP != null) {
                    TombDTP.setPlayer(deadName);
                    TombDTP.addSignBlock(block);
                } else {
                    TombDTP = new TombDTP(block);
                    TombDTP.setPlayer(deadName);
                    tombWorkerDTP.setTomb(deadName, TombDTP);
                }
                TombDTP.updateNewBlock();
                if (config.isUseTombAsRespawnPoint()) {
                    TombDTP.setRespawn(p.getLocation());
                    if (admin) {
                        p.sendMessage(tombWorkerDTP.graveDigger + " When " + deadName
                                + " die, he/she will respawn here.");
                    } else {
                        p.sendMessage(tombWorkerDTP.graveDigger + " When you die you'll respawn here.");
                    }
                }
            } catch (IllegalArgumentException e2) {
                p.sendMessage(tombWorkerDTP.graveDigger
                        + "It's not a good place for a Tomb. Try somewhere else.");
            }

        }
    }

}
