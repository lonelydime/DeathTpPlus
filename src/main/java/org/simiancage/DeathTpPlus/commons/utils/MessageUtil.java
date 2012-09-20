package org.simiancage.DeathTpPlus.commons.utils;


/**
 * PluginName: DeathTpPlus
 * Class: MessageUtil
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:17
 */

public class MessageUtil {
	public static String convertColorCodes(String msg) {
		return msg.replaceAll("(?i)&([a-fklmnor0-9])", "§$1");
	}

	public static String removeColorCodes(String msg) {
		return msg.replaceAll("(?i)§[a-fklmnor0-9]", "");
	}

}

