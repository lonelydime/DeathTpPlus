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
    private TombWorkerDTP tombWorker;

    public onSignChangeDTP() {
        this.log = LoggerDTP.getLogger();
        this.config = ConfigDTP.getInstance();
        this.tombWorker = TombWorkerDTP.getInstance();
    }

    public void oSCTomb (DeathTpPlus plugin, SignChangeEvent event){
        log.debug("onSignChange Tomb executing");
        String line0 = event.getLine(0);
        Player p = event.getPlayer();
        boolean admin = false;
        if (line0.indexOf(config.getTombKeyWord()) == 0) {
            if (!event.getLine(1).isEmpty() && p.hasPermission("deathtpplus.admin.tomb"))
                admin = true;
// Sign check
            TombDTP TombDTP = null;
            String deadName = event.getLine(1);
            if (admin) {
                if ((TombDTP = tombWorker.getTomb(deadName)) == null)
                    try {
                        deadName = p.getServer().getPlayer(event.getLine(1)).getName();
                    } catch (Exception e2) {
                        p.sendMessage(tombWorker.graveDigger + "The player " + event.getLine(1)
                                + "was not found.(The player HAS to be CONNECTED)");
                        return;
                    }
                else
                    deadName = TombDTP.getPlayer();
            } else
                deadName = event.getPlayer().getName();
            if (TombDTP != null)
                TombDTP.checkSigns();
            else if (tombWorker.hasTomb(deadName)) {
                TombDTP = tombWorker.getTomb(deadName);
                TombDTP.checkSigns();
            }
            int nbSign = 0;
            if (TombDTP != null)
                nbSign = TombDTP.getNbSign();
// max check
            int maxTombs = config.getMaxTomb();
            if (!admin && maxTombs != 0 && (nbSign + 1) > maxTombs) {
                p.sendMessage(tombWorker.graveDigger + "You have reached your TombDTP limit.");
                event.setCancelled(true);
                return;
            }
// perm and economy check
            if ((!admin && !p.hasPermission("deathtpplus.tomb.create"))
                    || !tombWorker.economyCheck(p, "creation-price")) {
                event.setCancelled(true);
                return;
            }
            Block block = event.getBlock();
            try {

                if (TombDTP != null) {
                    TombDTP.addSignBlock(block);
                } else {
                    TombDTP = new TombDTP(block);
                    TombDTP.setPlayer(deadName);
                    tombWorker.setTomb(deadName, TombDTP);
                }
                TombDTP.updateNewBlock();
                if (config.isUseTombAsRespawnPoint()) {
                    TombDTP.setRespawn(p.getLocation());
                    if (admin)
                        p.sendMessage(tombWorker.graveDigger + " When " + deadName
                                + " die, he/she will respawn here.");
                    else
                        p.sendMessage(tombWorker.graveDigger + " When you die you'll respawn here.");
                }
            } catch (IllegalArgumentException e2) {
                p.sendMessage(tombWorker.graveDigger
                        + "It's not a good place for a Tomb. Try somewhere else.");
            }

        }
    }

}
