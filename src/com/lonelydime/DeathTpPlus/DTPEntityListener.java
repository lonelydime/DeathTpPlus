package com.lonelydime.DeathTpPlus;

//java imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

//bukkit imports
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class DTPEntityListener extends EntityListener {
	public static DeathTpPlus plugin;
	public ArrayList<String> lastDamagePlayer = new ArrayList<String>();
	public ArrayList<String> lastDamageType = new ArrayList<String>();
	public String beforedamage = "";
	
	public DTPEntityListener(DeathTpPlus instance) {
		plugin = instance;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		beforedamage = "";
		try {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				String damagetype = lastDamageType.get(lastDamagePlayer.indexOf(player.getName()));
				String eventAnnounce = "";
				String fileOutput = "";
				String line = "";
				String[] howtheydied;
				String loghowdied = "";
				
				if (DeathTpPlus.deathconfig.get("ALLOW_DEATHTP").equals("true") ) {
					ArrayList<String> filetext = new ArrayList<String>();
					boolean readCheck = false;
					boolean newPlayerDeath = true;
					//text to write to file
					fileOutput = player.getName()+":"+player.getLocation().getX()+":"+player.getLocation().getY()+":"+player.getLocation().getZ();
					
					//File fileName = new File("plugins/DeathTpPlus/locs.txt");
					File fileName = new File(plugin.getDataFolder()+"/locs.txt");
					//read the file
					try {
						FileReader fr = new FileReader(fileName);
						BufferedReader br = new BufferedReader(fr);
						
						while((line = br.readLine()) != null) {
							if (line.contains(player.getName()+":")) {
								line = fileOutput;
								newPlayerDeath = false;
							}
							filetext.add(line);
							readCheck = true;
						}
						
						br.close();
						
						BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
						
						for (int i = 0; i < filetext.size(); i++) {
							out.write(filetext.get(i));
							out.newLine();
						}
						
						if (!readCheck) {
							out.write(fileOutput);
							out.newLine();
						}
						
						if (newPlayerDeath && readCheck) {
							out.write(fileOutput);
							out.newLine();
						}
					    //Close the output stream
					    out.close();
					}
					catch (IOException e) {
						System.out.println("cannot read file "+fileName.getPath()+"/"+fileName.getName());
						System.out.println(e);
					}
				}
			    
				if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") || DeathTpPlus.deathconfig.get("DEATH_LOGS").equals("true") ) {
				    howtheydied = damagetype.split(":");
				    
				    int messageindex = 0;
				    Random rand = new Random();
				    loghowdied = howtheydied[0];
				    
				    if (howtheydied[0].matches("FALL")) {
				    	if (DeathTpPlus.deathevents.get("DMGFALL").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGFALL").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGFALL").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("DROWNING")) {
				    	if (DeathTpPlus.deathevents.get("DMGDROWNING").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGDROWNING").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGDROWNING").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("SUFFOCATION")) {
				    	if (DeathTpPlus.deathevents.get("DMGSUFFOCATION").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGSUFFOCATION").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGSUFFOCATION").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("FIRE_TICK")) {
				    	if (DeathTpPlus.deathevents.get("DMGFIRE_TICK").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGFIRE_TICK").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGFIRE_TICK").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("FIRE")) {
				    	if (DeathTpPlus.deathevents.get("DMGFIRE").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGFIRE").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGFIRE").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("LAVA")) {
				    	if (DeathTpPlus.deathevents.get("DMGLAVA").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGLAVA").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGLAVA").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("BLOCK_EXPLOSION")) {
				    	if (DeathTpPlus.deathevents.get("DMGBLOCK_EXPLOSION").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGBLOCK_EXPLOSION").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGBLOCK_EXPLOSION").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("CREEPER")) {
				    	if (DeathTpPlus.deathevents.get("DMGCREEPER").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGCREEPER").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGCREEPER").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("SKELETON")) {
				    	if (DeathTpPlus.deathevents.get("DMGSKELETON").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGSKELETON").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGSKELETON").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("SPIDER")) {
				    	if (DeathTpPlus.deathevents.get("DMGSPIDER").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGSPIDER").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGSPIDER").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("PIGZOMBIE")) {
				    	if (DeathTpPlus.deathevents.get("DMGPIGZOMBIE").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGPIGZOMBIE").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGPIGZOMBIE").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("ZOMBIE")) {
				    	if (DeathTpPlus.deathevents.get("DMGZOMBIE").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGZOMBIE").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGZOMBIE").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("CONTACT")) {
				    	if (DeathTpPlus.deathevents.get("DMGCONTACT").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGCONTACT").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGCONTACT").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("SLIME")) {
				    	if (DeathTpPlus.deathevents.get("DMGSLIME").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGSLIME").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGSLIME").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("VOID")) {
				    	if (DeathTpPlus.deathevents.get("DMGVOID").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGVOID").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGVOID").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("GHAST")) {
				    	if (DeathTpPlus.deathevents.get("DMGGHAST").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGGHAST").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGGHAST").get(messageindex).replace("%n", player.getName());
				    }
				    else if (howtheydied[0].matches("PVP")) {
				    	if (howtheydied[2].equals("bare hands")) {
					    	if (DeathTpPlus.deathevents.get("DMGFISTS").size() > 1)
					    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGFISTS").size());
					    	eventAnnounce = DeathTpPlus.deathevents.get("DMGFISTS").get(messageindex).replace("%n", player.getName());
				    	}
				    	else {
					    	if (DeathTpPlus.deathevents.get("DMGPVP").size() > 1)
					    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGPVP").size());
					    	eventAnnounce = DeathTpPlus.deathevents.get("DMGPVP").get(messageindex).replace("%n", player.getName());
				    	}
				    	loghowdied = howtheydied[2];
				    	eventAnnounce = eventAnnounce.replace("%i", howtheydied[1]);
						eventAnnounce = eventAnnounce.replace("%a", howtheydied[2]);
						if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").matches("true"))
							writeToStreak(player.getName(), howtheydied[2]);
						
						//write kill to deathlog
						if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
							writeToLog("kill", howtheydied[2], player.getName());
						}
				    }
				    else {
				    	if (DeathTpPlus.deathevents.get("DMGUNKNOWN").size() > 1)
				    		messageindex = rand.nextInt(DeathTpPlus.deathevents.get("DMGUNKNOWN").size());
				    	eventAnnounce = DeathTpPlus.deathevents.get("DMGUNKNOWN").get(messageindex).replace("%n", player.getName());
				    }
				    
				    eventAnnounce = plugin.convertSamloean(eventAnnounce);
				    
				    if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true")) {
				    	plugin.getServer().broadcastMessage(eventAnnounce);
				    }
					
					//CraftIRC
					if (DeathTpPlus.craftircHandle != null) {
						String ircAnnounce;
						ircAnnounce = eventAnnounce.replace("¤0", "");
						ircAnnounce = ircAnnounce.replace("¤2", "");
						ircAnnounce = ircAnnounce.replace("¤3", "");
						ircAnnounce = ircAnnounce.replace("¤4", "");
						ircAnnounce = ircAnnounce.replace("¤5", "");
						ircAnnounce = ircAnnounce.replace("¤6", "");
						ircAnnounce = ircAnnounce.replace("¤7", "");
						ircAnnounce = ircAnnounce.replace("¤8", "");
						ircAnnounce = ircAnnounce.replace("¤9", "");
						ircAnnounce = ircAnnounce.replace("¤a", "");
						ircAnnounce = ircAnnounce.replace("¤b", "");
						ircAnnounce = ircAnnounce.replace("¤c", "");
						ircAnnounce = ircAnnounce.replace("¤d", "");
						ircAnnounce = ircAnnounce.replace("¤e", "");
						ircAnnounce = ircAnnounce.replace("¤f", "");
						
						DeathTpPlus.craftircHandle.sendMessageToTag(ircAnnounce, DeathTpPlus.deathconfig.get("CRAFT_IRC_TAG"));
					}
					
					if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
						writeToLog("death", player.getName(), loghowdied);
					}
					
					if (DeathTpPlus.deathconfig.get("SHOW_SIGN").equals("true")) {
						//place sign
						Block signBlock = player.getWorld().getBlockAt(player.getLocation().getBlockX(),
				                player.getLocation().getBlockY(),
				                player.getLocation().getBlockZ());
	
				        signBlock.setType(Material.SIGN_POST);
	
				        BlockState state = signBlock.getState();
		
			            if (state instanceof Sign) {
			              String signtext;
			              Sign sign = (Sign)state;
			              sign.setLine(0, "[RIP]");
			              sign.setLine(1, player.getName());
			              sign.setLine(2, "Died by");
			              signtext = howtheydied[0].toLowerCase();
			              if (howtheydied[0].equals("PVP"))
			            	  signtext = howtheydied[2];
			            	  
			              sign.setLine(3, signtext);
			            }
					}
		            
				}
				
				//added compatibility for streaks if notify is off
				else {
					howtheydied = damagetype.split(":");
					if (howtheydied[0].matches("PVP")) {
						if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").matches("true"))
							writeToStreak(player.getName(), howtheydied[2]);
				    }
					
					if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
						writeToLog("death", player.getName(), loghowdied);
					}
				}
				
				
				
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			//player.sendMessage(event.getType().toString());
			lastDamageDone(player, event);
		}
	}
	
	public void lastDamageDone(Player player, EntityDamageEvent event) {
		String lastdamage = event.getCause().name();
		//player.sendMessage(lastdamage);
		//checks for mob/PVP damage
		if (event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent mobevent = (EntityDamageByProjectileEvent) event;
			Entity attacker = mobevent.getDamager();
			if (attacker instanceof Ghast) {
				lastdamage = "GHAST";
			}
			else if (attacker instanceof Monster) {
				lastdamage = "SKELETON";
			}
			else if (attacker instanceof Player) {
				Player pvper = (Player) attacker;
				String usingitem = pvper.getItemInHand().getType().name();
				if (usingitem == "AIR") {
					usingitem = "BARE_KNUCKLES";
				}
				lastdamage = "PVP:"+usingitem+":"+pvper.getName();
			}
		}

		else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) event;
			Entity attacker = mobevent.getDamager();
			
			if (attacker.toString().toLowerCase().matches("craftslime")) {
				lastdamage = "SLIME";
			}

			else if (attacker instanceof Monster) {
				Monster mob = (Monster) attacker;
				
				if (mob instanceof PigZombie) {
					lastdamage = "PIGZOMBIE";
				}
				else if (mob instanceof Zombie) {
					lastdamage = "ZOMBIE";
				}
				else if (mob instanceof Creeper) {
					lastdamage = "CREEPER";
				}
				else if (mob instanceof Spider) {
					lastdamage = "SPIDER";
				}
				else if (mob instanceof Skeleton) {
					lastdamage = "SKELETON";
				}
				else if (mob instanceof Ghast) {
					lastdamage = "GHAST";
				}
				else if (mob instanceof Slime) {
					lastdamage = "SLIME";
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
			}
		}
				
		if ((beforedamage.equals("GHAST") && lastdamage.equals("BLOCK_EXPLOSION")) ||(beforedamage.equals("GHAST") && lastdamage.equals("GHAST"))) {
			lastdamage = "GHAST";
		}

		if (!lastDamagePlayer.contains(player.getName())) {
			lastDamagePlayer.add(player.getName());
			lastDamageType.add(event.getCause().name());
		}
		else {
			lastDamageType.set(lastDamagePlayer.indexOf(player.getName()), lastdamage);
		}
		
		beforedamage = lastdamage;
	}
	
	public void writeToStreak(String defender, String attacker) {

		//read the file
		try {
			String line = "";
			ArrayList<String> filetext = new ArrayList<String>();
			
			//File streakFile = new File("plugins/DeathTpPlus/streak.txt");
			File streakFile = new File(plugin.getDataFolder()+"/streak.txt");
			BufferedReader br = new BufferedReader(new FileReader(streakFile));
			String[] splittext;
			int atkCurrentStreak = 0;
			int defCurrentStreak = 0;
			boolean foundDefender = false;
			boolean foundAttacker = false;
			boolean isNewFile = true;
			
			while((line = br.readLine()) != null) {
				if (line.contains(defender+":")) {
					splittext = line.split(":");
					defCurrentStreak = Integer.parseInt(splittext[1].trim());
					if (defCurrentStreak > 0) {
						defCurrentStreak = 0;
					}
					defCurrentStreak--;
					line = defender+":"+Integer.toString(defCurrentStreak);
					foundDefender = true;
				}
				if (line.contains(attacker+":")) {
					splittext = line.split(":");
					atkCurrentStreak = Integer.parseInt(splittext[1].trim());
					if (atkCurrentStreak < 0) {
						atkCurrentStreak = 0;
					}
					atkCurrentStreak++;
					line = attacker+":"+Integer.toString(atkCurrentStreak);
					foundAttacker = true;
				}
				filetext.add(line);
				isNewFile = false;
			}
			
			br.close();


			String teststreak = "";
			String testsplit[];
			
			//Check to see if we should announce a streak
			//Deaths
			for (int i=0;i < DeathTpPlus.deathstreak.get("DEATH_STREAK").size();i++) {
				teststreak = DeathTpPlus.deathstreak.get("DEATH_STREAK").get(i);
				testsplit = teststreak.split(":");
				if (Integer.parseInt(testsplit[0]) == -(defCurrentStreak)) {
					String announce = plugin.convertSamloean(testsplit[1]);
					plugin.getServer().broadcastMessage(announce.replace("%n", defender));
				}
			}
			//Kills
			for (int i=0;i < DeathTpPlus.killstreak.get("KILL_STREAK").size();i++) {
				teststreak = DeathTpPlus.killstreak.get("KILL_STREAK").get(i);
				testsplit = teststreak.split(":");
				if (Integer.parseInt(testsplit[0]) == atkCurrentStreak) {
					String announce = plugin.convertSamloean(testsplit[1]);
					plugin.getServer().broadcastMessage(announce.replace("%n", attacker));
				}
			}
		
			// Write streaks to file
			BufferedWriter out = new BufferedWriter(new FileWriter(streakFile));
			
			for (int i = 0; i < filetext.size(); i++) {
				out.write(filetext.get(i));
				out.newLine();
			}
			
			if (isNewFile) {
				out.write(attacker+":"+"1");
				out.newLine();
				out.write(defender+":"+"-1");
				out.newLine();
			}
			
			if (!foundDefender && !isNewFile) {
				out.write(defender+":"+"-1");
				out.newLine();
			}
			
			if (!foundAttacker && !isNewFile) {
				out.write(attacker+":"+"1");
				out.newLine();
			}
		    //Close the output stream
		    out.close();
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public void writeToLog(String logtype, String playername, String deathtype) {
		File deathlogFile = new File(plugin.getDataFolder()+"/deathlog.txt");
		File deathlogTempFile = new File(plugin.getDataFolder()+"/deathtlog.tmp");
		String line = "";
		String[] splittext;
		String writeline = "";
		int newrecord = 0;
		boolean foundrecord = false;
		
		if (!deathlogTempFile.exists()) {
			try {
				deathlogTempFile.createNewFile();
			} catch (IOException e) {
				System.out.println("cannot create file "+deathlogTempFile.getPath()+"/"+deathlogTempFile.getName());
			}
		}
		
		try {
			//format name:type:mob/player:number
			PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
			BufferedReader br = new BufferedReader(new FileReader(deathlogFile));
			
			while((line = br.readLine()) != null) {
				splittext = line.split(":");
				writeline = line;
				if (splittext[0].matches(playername)) {
					if (splittext[1].matches(logtype)) {
						if (splittext[2].matches(deathtype)) {
							newrecord = Integer.parseInt(splittext[3]);
							newrecord++;
							writeline = playername+":"+logtype+":"+deathtype+":"+newrecord;
							foundrecord = true;
						}
					}
				}
				
				pw.println(writeline);
			    pw.flush();
			}
			
			if (!foundrecord) {
				writeline = playername+":"+logtype+":"+deathtype+":1";
				pw.println(writeline);
			    pw.flush();
			}
			
			pw.close();
			br.close();
			
			deathlogFile.delete();
			deathlogTempFile.renameTo(deathlogFile);
		}
		catch(IOException e) {
			System.out.println("Could not edit deathlog: "+e);
		}
		
	}
}
