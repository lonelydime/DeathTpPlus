package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.listeners.EntityListenerDTP;

/**
 * PluginName: DeathTpPlus
 * Class: onEntityDamageDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:28
 */

public class onEntityDamageDTP {

    private LoggerDTP log;
    private ConfigDTP config;
    private DeathTpPlus plugin;
    private EntityListenerDTP entityListenerDTP;

    public onEntityDamageDTP() {
        this.log = LoggerDTP.getLogger();
        this.config = ConfigDTP.getInstance();

    }

    public void setLastDamageDone (EntityListenerDTP entityListenerDTP, EntityDamageEvent entityDamageEvent)
    {
        log.debug("onEntityDamageDTP executing");
        Player player = (Player) entityDamageEvent.getEntity();
        String lastdamage = entityDamageEvent.getCause().name();
        //checks for mob/PVP damage
        log.debug("lastdamage", lastdamage );
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) entityDamageEvent;
            Entity attacker = mobevent.getDamager();
            if (attacker instanceof Fireball) {
                lastdamage = (((Fireball) attacker).getShooter().toString());
                log.debug("lastdamage",lastdamage );
            }
            else if (attacker instanceof Arrow) {
                 lastdamage = ((Arrow) attacker).getShooter().toString();
                log.debug("lastdamage",lastdamage );
            }
            else if (attacker instanceof Player) {
                Player pvper = (Player) attacker;
                String usingitem = pvper.getItemInHand().getType().name();
                if (usingitem == "AIR") {
                    usingitem = "BARE_KNUCKLES";
                }
                lastdamage = "PVP:"+usingitem+":"+pvper.getName();
                log.debug("lastdamage",lastdamage );
            }
            else if (attacker.toString().toLowerCase().matches("craftslime")) {
                lastdamage = "SLIME";
                log.debug("lastdamage",lastdamage );
            }

            else if (attacker instanceof Wolf) {
                lastdamage = "WOLF";
                log.debug("lastdamage",lastdamage );
            }

            else if (attacker instanceof Monster) {
                Monster mob = (Monster) attacker;

                if (mob instanceof PigZombie) {
                    lastdamage = "PIGZOMBIE";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Zombie) {
                    lastdamage = "ZOMBIE";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Creeper) {
                    lastdamage = "CREEPER";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof CaveSpider) {
                    lastdamage = "CAVESPIDER";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Spider) {
                    lastdamage = "SPIDER";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Skeleton) {
                    lastdamage = "SKELETON";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Ghast) {
                    lastdamage = "GHAST";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Slime) {
                    lastdamage = "SLIME";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Enderman) {
                    lastdamage = "ENDERMAN";
                    log.debug("lastdamage",lastdamage );
                }
                else if (mob instanceof Silverfish) {
                    lastdamage = "SILVERFISH";
                    log.debug("lastdamage",lastdamage );
                }
            }
            else if (attacker instanceof Player) {
                Player pvper = (Player) attacker;
                String usingitem = pvper.getItemInHand().getType().name();
                if (usingitem == "AIR") {
                    usingitem = "fist";
                }
                usingitem = usingitem.toLowerCase();
                usingitem = usingitem.replace("_", " ");
                lastdamage = "PVP:"+usingitem+":"+pvper.getName();
                log.debug("lastdamage",lastdamage );
            }
        }

        // ToDO Figure out what beforedamage is.
        /*if ((beforedamage.equals("GHAST") && lastdamage.equals("BLOCK_EXPLOSION")) ||(beforedamage.equals("GHAST") && lastdamage.equals("GHAST"))) {
            lastdamage = "GHAST";
        }*/


        if (!entityListenerDTP.getLastDamagePlayer().contains(player.getName())) {
            // ToDo create correct setters for those 3 variables
            entityListenerDTP.lastDamagePlayer.add(player.getName());
            // entityListenerDTP.lastDamageType.add(entityDamageEvent.getCause().name());
            entityListenerDTP.lastDamageType.add(lastdamage);
        }
        else {
            entityListenerDTP.lastDamageType.set(entityListenerDTP.getLastDamagePlayer().indexOf(player.getName()), lastdamage);
        }

        // beforedamage = lastdamage;

    }


}


