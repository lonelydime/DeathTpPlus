package org.simiancage.DeathTpPlus.listeners;

//java imports

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.onEntityDamageDTP;
import org.simiancage.DeathTpPlus.events.onEntityDeathDTP;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.objects.TombBlockDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

import java.util.ArrayList;

//bukkit imports

public class EntityListenerDTP extends EntityListener {
    private DeathTpPlus plugin;
    public ArrayList<String> lastDamagePlayer = new ArrayList<String>();
    public ArrayList<String> lastDamageType = new ArrayList<String>();
    private String beforedamage = "";
    private PlayerDeathEvent playerDeathEvent = null;
    private String loghowdied;
    protected TombWorkerDTP worker = TombWorkerDTP.getInstance();

    private ConfigDTP config;
    private LoggerDTP log;
    private onEntityDamageDTP oedam;
    private onEntityDeathDTP oedea;
    private EntityListenerDTP instance;

    public EntityListenerDTP(DeathTpPlus plugin) {
        this.plugin = plugin;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.debug("EntityListener active");
        instance = this;
    }



    public ArrayList<String> getLastDamagePlayer() {
        return lastDamagePlayer;
    }

    public void setLastDamagePlayer(ArrayList<String> lastDamagePlayer) {
        this.lastDamagePlayer = lastDamagePlayer;
    }

    public ArrayList<String> getLastDamageType() {
        return lastDamageType;
    }

    public void setLastDamageType(ArrayList<String> lastDamageType) {
        this.lastDamageType = lastDamageType;
    }

    public String getBeforedamage() {
        return beforedamage;
    }

    public void setBeforedamage(String beforedamage) {
        this.beforedamage = beforedamage;
    }

    public PlayerDeathEvent getPlayerDeathEvent() {
        return playerDeathEvent;
    }

    public void setPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
        this.playerDeathEvent = playerDeathEvent;
    }





    public void onEntityDeath(EntityDeathEvent event) {

        if (event.getEntity() instanceof Player){
            if (config.isEnableDeathtp()){
                oedea = new onEntityDeathDTP(plugin);
                oedea.oEDeaDeathTp(plugin, instance, event);
            }

            if (config.isShowDeathNotify() || config.isShowStreaks() || config.isAllowDeathLog() || config.isEnableTombStone()|| config.isEnableTomb() ) {
                oedea = new onEntityDeathDTP(plugin);
                oedea.oEDeaGeneralDeath(plugin, instance, event);
            }
        }

    }

    public void onEntityExplode(EntityExplodeEvent event) {
        log.debug("onEntityExplodeDTP executing");
        if (event.isCancelled())
            return;
        if (!config.isCreeperProtection())
            return;
        for (Block block : event.blockList()) {
            TombBlockDTP tBlockDTP = plugin.tombBlockList.get(block.getLocation());
            if (tBlockDTP != null) {
                event.setCancelled(true);
            }
        }
    }



    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()){
            return;
        }
        if(event.getEntity() instanceof Player) {
            oedam = new onEntityDamageDTP();
            oedam.setLastDamageDone(instance, event);

        }
    }


}
