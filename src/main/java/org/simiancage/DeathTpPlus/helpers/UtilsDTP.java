package org.simiancage.DeathTpPlus.helpers;

import org.bukkit.entity.*;

/**
 * PluginName: DeathTpPlus
 * Class: UtilsDTP
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:17
 */

public class UtilsDTP {
    public static String convertColorCodes(String msg) {
        return msg.replaceAll("&([0-9a-fA-F])", "§$1");
    }

    public static String removeColorCodes(String msg) {
        return msg.replaceAll("§[0-9a-fA-F]", "");
    }

    public static CreatureType getCreatureType(Entity entity) {
        if (entity instanceof CaveSpider) {
            return CreatureType.CAVE_SPIDER;
        }
        if (entity instanceof Chicken) {
            return CreatureType.CHICKEN;
        }
        if (entity instanceof Cow) {
            return CreatureType.COW;
        }
        if (entity instanceof Creeper) {
            return CreatureType.CREEPER;
        }
        if (entity instanceof Enderman) {
            return CreatureType.ENDERMAN;
        }
        if (entity instanceof Ghast) {
            return CreatureType.GHAST;
        }
        if (entity instanceof Giant) {
            return CreatureType.GIANT;
        }
        if (entity instanceof Pig) {
            return CreatureType.PIG;
        }
        if (entity instanceof PigZombie) {
            return CreatureType.PIG_ZOMBIE;
        }
        if (entity instanceof Sheep) {
            return CreatureType.SHEEP;
        }
        if (entity instanceof Skeleton) {
            return CreatureType.SKELETON;
        }
        if (entity instanceof Slime) {
            return CreatureType.SLIME;
        }
        if (entity instanceof Silverfish) {
            return CreatureType.SILVERFISH;
        }
        if (entity instanceof Spider) {
            return CreatureType.SPIDER;
        }
        if (entity instanceof Squid) {
            return CreatureType.SQUID;
        }
        if (entity instanceof Zombie) {
            return CreatureType.ZOMBIE;
        }
        if (entity instanceof Wolf) {
            return CreatureType.WOLF;
        }

        // Monster is a parent class and needs to be last
        if (entity instanceof Monster) {
            return CreatureType.MONSTER;
        }

        return null;
    }


}

