package com.lonelydime.DeathTpPlus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

public class DTPPlayerListener extends PlayerListener{
	public static DeathTpPlus plugin;
	
	public DTPPlayerListener(DeathTpPlus instance) {
        plugin = instance;
    }
	
	public void onPlayerCommand(PlayerChatEvent event) {

		String[] split = event.getMessage().split(" ");
		boolean canUseCommand = true;
		Player player = event.getPlayer();
		boolean teleportok = true;
		
		if ((split[0].equalsIgnoreCase("/deathtp")) && canUseCommand) {
			if (DeathTpPlus.Permissions != null) {
				canUseCommand = DeathTpPlus.Permissions.has(player, "deathtpplus.deathtp");
			}
			else {
				canUseCommand = DeathTpPlus.deathconfig.get("ALLOW_DEATHTP").equals("true");
			}
			
			if (canUseCommand) {
				if (!DeathTpPlus.deathconfig.get("CHARGE_ITEM_ID").equals("0") ) {
					if (player.getItemInHand().getType().getId() != Integer.parseInt(DeathTpPlus.deathconfig.get("CHARGE_ITEM_ID"))) {
						player.sendMessage("You must be holding a "+Material.getMaterial(Integer.parseInt(DeathTpPlus.deathconfig.get("CHARGE_ITEM_ID"))).toString()+" to teleport.");
						teleportok = false;
					}
					else {
						ItemStack currentitem = player.getItemInHand();
						int itemnum = currentitem.getAmount();
						itemnum--;
						if (itemnum > 0) {
							currentitem.setAmount(itemnum);
							player.setItemInHand(currentitem);
						}
						else {
							player.getInventory().clear(player.getInventory().getHeldItemSlot());
						}
					}
				}
				if (teleportok) {
					File fileName = new File("plugins/DeathTpPlus/locs.txt");
					try {
						String line = "";
						String teleloc = "";
						String[] location;
						FileReader fr = new FileReader(fileName);
						BufferedReader br = new BufferedReader(fr);
						
						while((line = br.readLine()) != null) {
							if (line.contains(player.getName()+":")) {
								teleloc = line;
							}	
						}
						
						if (teleloc != "") {
							location = teleloc.split(":");
							Location sendLocation = player.getLocation();
							sendLocation.setX(Double.valueOf(location[1].trim()).doubleValue());
							sendLocation.setY(Double.valueOf(location[2].trim()).doubleValue());
							sendLocation.setZ(Double.valueOf(location[3].trim()).doubleValue());
							player.teleportTo(sendLocation);
						}
						else {
							player.sendMessage("You do not have a last known death location.");
						}
						
					}
					catch (IOException e) {
		
					}
				}
				else {
					player.sendMessage("That command is not available");
				}
			
			}
			event.setCancelled(true);
		}
		
		//else if ((split[0].equalsIgnoreCase("/suicide"))) {
		if ((split[0].equalsIgnoreCase("/suicide"))) {
			player.setHealth(0);
		}
		
		
		
		else if ((split[0].equalsIgnoreCase("/streak"))) {
			if (DeathTpPlus.Permissions != null) {
				canUseCommand = DeathTpPlus.Permissions.has(player, "deathtpplus.streak");
			}
			
			if (canUseCommand) {
				if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
					File streakFile = new File("plugins/DeathTpPlus/streak.txt");
					String line;
					String[] splittext;
					Player check = player;
					
					if (split.length > 1) {
						List<Player> lookup = plugin.getServer().matchPlayer(split[1]);
					
						if (lookup.size() == 0) {
							player.sendMessage(ChatColor.RED+ "No matching player.");
						} 
						else if (lookup.size() != 1) {
							player.sendMessage(ChatColor.RED+ "Matched more than one player!  Be more specific!");
						} 
						else {
							check = lookup.get(0);
						}
					}
					
					try {
						FileReader fr = new FileReader(streakFile);
						BufferedReader br = new BufferedReader(fr);
						boolean entryfound = false;
						while((line = br.readLine()) != null) {
							if (!line.startsWith("#")) {
								splittext = line.split(":");
								if (check.getName().matches(splittext[0])) {
									if (Integer.parseInt(splittext[1]) < 0) {
										player.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1].replace("-", "")+" Death(s)");
									}
									else {
										player.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1]+" Kill(s)");
									}
									
									entryfound = true;
								}
							}
						}
						if (!entryfound) {
							player.sendMessage("No streak found");
						}
						br.close();
					}
					catch (IOException e) {
						System.out.println(e);
					}
				}
			}
		}

	}
}
