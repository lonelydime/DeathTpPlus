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

	public static EntityType getEntityType(Entity entity) {
		if (entity instanceof Blaze) {
			return EntityType.BLAZE;
		}
		if (entity instanceof CaveSpider) {
			return EntityType.CAVE_SPIDER;
		}
		if (entity instanceof Chicken) {
			return EntityType.CHICKEN;
		}
		if (entity instanceof Cow) {
			return EntityType.COW;
		}
		if (entity instanceof Creeper) {
			return EntityType.CREEPER;
		}
		if (entity instanceof EnderDragon) {
			return EntityType.ENDER_DRAGON;
		}
		if (entity instanceof Enderman) {
			return EntityType.ENDERMAN;
		}
		if (entity instanceof Ghast) {
			return EntityType.GHAST;
		}
		if (entity instanceof Giant) {
			return EntityType.GIANT;
		}
		if (entity instanceof MagmaCube) {
			return EntityType.MAGMA_CUBE;
		}
		if (entity instanceof MushroomCow) {
			return EntityType.MUSHROOM_COW;
		}
		if (entity instanceof Pig) {
			return EntityType.PIG;
		}
		if (entity instanceof PigZombie) {
			return EntityType.PIG_ZOMBIE;
		}
		if (entity instanceof Sheep) {
			return EntityType.SHEEP;
		}
		if (entity instanceof Skeleton) {
			return EntityType.SKELETON;
		}
		if (entity instanceof Slime) {
			return EntityType.SLIME;
		}
		if (entity instanceof Silverfish) {
			return EntityType.SILVERFISH;
		}
		if (entity instanceof Snowman) {
			return EntityType.SNOWMAN;
		}
		if (entity instanceof Spider) {
			return EntityType.SPIDER;
		}
		if (entity instanceof Squid) {
			return EntityType.SQUID;
		}
		if (entity instanceof Villager) {
			return EntityType.VILLAGER;
		}
		if (entity instanceof Zombie) {
			return EntityType.ZOMBIE;
		}
		if (entity instanceof Wolf) {
			return EntityType.WOLF;
		}
		if (entity instanceof IronGolem) {
			return EntityType.IRON_GOLEM;
		}

		return null;
	}


}

