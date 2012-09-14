package org.simiancage.DeathTpPlus.common.utils;


/**
 * PluginName: DeathTpPlus
 * Class: MessageUtil
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:17
 */

public class MessageUtil {
	public static String convertColorCodes(String msg) {
		return msg.replaceAll("&([0-9a-fA-F])", "§$1");
	}

	public static String removeColorCodes(String msg) {
		return msg.replaceAll("§[0-9a-fA-F]", "");
	}

}

