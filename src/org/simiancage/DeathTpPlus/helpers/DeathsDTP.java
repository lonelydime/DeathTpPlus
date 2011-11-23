package org.simiancage.DeathTpPlus.helpers;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * PluginName: DeathTpPlus
 * Class: DeathsDTP
 * User: DonRedhorse
 * Date: 21.11.11
 * Time: 22:53
 */

public class DeathsDTP {

    //ToDo move storage of death information into here, including damage before this damage has been done.
    private static ArrayList<String> lastDamagePlayer = new ArrayList<String>();
    private static ArrayList<String> lastDamageType = new ArrayList<String>();
    private static ArrayList<String> beforeDamageType = new ArrayList<String>();
    private static ArrayList<String> deathCause = new ArrayList<String>();
    private static ArrayList<String> deathReason = new ArrayList<String>();
    private static DeathsDTP instance = new DeathsDTP();
    private static LoggerDTP log;
    private static ConfigDTP config;

    enum DeathTypes {FALL, DROWNING, SUFFOCATION, FIRE_TICK, FIRE, LAVA, BLOCK_EXPLOSION, CREEPER, SKELETON, SPIDER, PIGZOMBIE, ZOMBIE, CONTACT, SLIME, VOID, GHAST, WOLF, LIGHTNING, STARVATION, CAVESPIDER, ENDERMAN, SILVERFISH, PVP, FISTS, UNKNOWN, SUICIDE, BLAZE, FALLING_SAND, SQUID;

        @Override public String toString() {
            //only capitalize the first letter
            String s = super.toString();
            return s.substring(0, 1)+s.substring(1).toLowerCase();
        }
    }

    private DeathsDTP () {
    }

    public static DeathsDTP getInstance () {
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        return instance;
    }

    public static ArrayList<String> getLastDamagePlayer() {
        return lastDamagePlayer;
    }

    public static ArrayList<String> getLastDamageType() {
        return lastDamageType;
    }

    public static ArrayList<String> getBeforeDamageType() {
        return beforeDamageType;
    }

    public void addLastDamagePlayer (Player player) {
        lastDamagePlayer.add(player.getName());
    }

    public void addLastDamageType (String lastdamage) {
        lastDamageType.add(lastdamage);
    }

    public void addBeforeDamageType (String beforedamage) {
        beforeDamageType.add(beforedamage);
    }

    public int idxLastDamagePlayer (Player player){
            return lastDamagePlayer.indexOf(player.getName());
    }


    public void setLastDamageType (Player player, String lastdamage) {
        lastDamageType.set(idxLastDamagePlayer(player),lastdamage );
    }

   }

