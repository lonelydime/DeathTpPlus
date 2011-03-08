package com.lonelydime.DeathTpPlus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;

//permissions
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.anjocaido.groupmanager.GroupManager;
//iconomy
import com.nijiko.coelho.iConomy.iConomy;
//craftirc
import com.ensifera.animosity.craftirc.CraftIRC;

public class DeathTpPlus extends JavaPlugin{
	//damage and death listener
	private final DTPEntityListener entityListener = new DTPEntityListener(this);
	
	//plugin variables
	private final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();
    public static HashMap<String, String> deathconfig = new HashMap<String, String>();
    
    //permissions
    public static PermissionHandler Permissions = null;
    public static GroupManager gm = null;
    
    //iconomy
    private static PluginListener PluginListener = null;
    private static iConomy iConomy = null;
    private static Server Server = null;
    private boolean useiConomy = false;
    
    //craftirc
    public static CraftIRC craftircHandle = null;
    
	public void onDisable() {
		log.info("[DeathTpPlus] Disabled");
	}

	public void onEnable() {
		
		File yml = new File(getDataFolder()+"/config.yml");
        File locsName = new File(getDataFolder()+"/locs.txt");
		File streakFile = new File(getDataFolder()+"/streak.txt");
        
        if (!yml.exists()) {
        	new File(getDataFolder().toString()).mkdir();
    	    try {
    	    	yml.createNewFile();
    	    }
    	    catch (IOException ex) {
    	    	System.out.println("cannot create file "+yml.getPath());
    	    }
        }	
		
		if (!locsName.exists()) {
			try {
				locsName.createNewFile();
			} catch (IOException e) {
				System.out.println("cannot create file "+locsName.getPath()+"/"+locsName.getName());
			}
		}
		
		if (!streakFile.exists()) {
			try {
				streakFile.createNewFile();
			} catch (IOException e) {
				System.out.println("cannot create file "+streakFile.getPath()+"/"+streakFile.getName());
			}
		}
		
		//Death Event nodes
		deathevents.put("DMGFALL", getConfiguration().getStringList("fall", null));
		deathevents.put("DMGDROWNING", getConfiguration().getStringList("drowning", null));
		deathevents.put("DMGFIRE", getConfiguration().getStringList("fire", null));
		deathevents.put("DMGFIRE_TICK", getConfiguration().getStringList("fire_tick", null));
		deathevents.put("DMGLAVA", getConfiguration().getStringList("lava", null));
		deathevents.put("DMGBLOCK_EXPLOSION", getConfiguration().getStringList("block_explosion", null));
		deathevents.put("DMGCREEPER", getConfiguration().getStringList("creeper", null));
		deathevents.put("DMGSKELETON", getConfiguration().getStringList("skeleton", null));
		deathevents.put("DMGSPIDER", getConfiguration().getStringList("spider", null));
		deathevents.put("DMGZOMBIE", getConfiguration().getStringList("zombie", null));
		deathevents.put("DMGCONTACT", getConfiguration().getStringList("contact", null));
		deathevents.put("DMGPIGZOMBIE", getConfiguration().getStringList("pigzombie", null));
		deathevents.put("DMGGHAST", getConfiguration().getStringList("ghast", null));
		deathevents.put("DMGSLIME", getConfiguration().getStringList("slime", null));
		deathevents.put("DMGPVP", getConfiguration().getStringList("pvp", null));
		deathevents.put("DMGFISTS", getConfiguration().getStringList("pvp-fists", null));
		deathevents.put("DMGSUFFOCATION", getConfiguration().getStringList("suffocation", null));
		deathevents.put("DMGUNKNOWN", getConfiguration().getStringList("unknown", null));
		//Configuration nodes
		deathconfig.put("SHOW_DEATHNOTIFY", getConfiguration().getString("show-deathnotify", "false"));
		deathconfig.put("ALLOW_DEATHTP", getConfiguration().getString("allow-deathtp", "false"));
		deathconfig.put("SHOW_STREAKS", getConfiguration().getString("show-streaks", "false"));
		deathconfig.put("CHARGE_ITEM_ID", getConfiguration().getString("charge-item", "false"));
		deathconfig.put("SHOW_SIGN", getConfiguration().getString("show-sign", "false"));
		deathconfig.put("ICONOMY_COST", getConfiguration().getString("deathtp-cost", "0"));
		deathconfig.put("CRAFT_IRC_TAG", getConfiguration().getString("deathtp-tag", null));
		//Kill Streak nodes
		killstreak.put("KILL_STREAK", getConfiguration().getStringList("killstreak", null));
		//Death Streak nodes
		deathstreak.put("DEATH_STREAK", getConfiguration().getStringList("deathstreak", null));
		
		log.info("[DeathTpPlus] "+killstreak.get("KILL_STREAK").size()+" Kill Streaks loaded.");
		log.info("[DeathTpPlus] "+deathstreak.get("DEATH_STREAK").size()+" Death Streaks loaded.");
		//System.out.println("[DeathTpPlus] "+killstreak.get("KILL_STREAK").size()+" Kill Streaks loaded.");
		//System.out.println("[DeathTpPlus] "+deathstreak.get("DEATH_STREAK").size()+" Death Streaks loaded.");
		
        //Create the pluginmanage pm.
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        
        if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true")) {
        	pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        }
        
        if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
        	pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
        }
        
        //permissions include
        setupPermissions();
        
        //iconomy
        Server = getServer();
        PluginListener = new PluginListener();
        // Event Registration
        
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, PluginListener, Priority.Monitor, this);
        //end iconomy
        
        //craftirc
        Plugin checkplugin = this.getServer().getPluginManager().getPlugin("CraftIRC");
        if (checkplugin != null) {
	        try {
	            // Get handle to CraftIRC, add&register your custom listener
	            craftircHandle = (CraftIRC) checkplugin;
	            log.info("[DeathTpPlus] CraftIRC Support Enabled.");
	        } 
	        catch (ClassCastException ex) {
	            //ex.printStackTrace();
	            //log.warning("MyPlugin can't cast plugin handle as CraftIRC plugin!");
	        }
        }
        
        // print success
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info("[DeathTpPlus] version " + pdfFile.getVersion() + " by lonelydime is enabled!");
	}
	
	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
		
		if(Permissions == null) {
		    if(test != null) {
		    	Permissions = ((Permissions)test).getHandler();
		    	log.info("[DeathTpPlus] Using Permissions");
		    }
		}
		
		if (p != null) {
            if (!p.isEnabled()) {
                this.getServer().getPluginManager().enablePlugin(p);
                log.info("[DeathTpPlus] Using GroupManager");
            }
            gm = (GroupManager) p;
        } 
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String command = cmd.getName();
		boolean canUseCommand = true;
		boolean teleportok = true;
		
		if (command.equals("deathtp")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				double iconomyCost = Double.valueOf(deathconfig.get("ICONOMY_COST").trim()).doubleValue();
				
				if (Permissions != null) {
					canUseCommand = Permissions.has(player, "deathtpplus.deathtp");
				}
				else if (gm != null) {
					canUseCommand = gm.getWorldsHolder().getWorldPermissions(player).has(player,"deathtpplus.deathtp");
				}
				else {
					canUseCommand = deathconfig.get("ALLOW_DEATHTP").equals("true");
				}
				
				if (canUseCommand) {
					//costs item in inventory
					if (!deathconfig.get("CHARGE_ITEM_ID").equals("0") ) {
						if (player.getItemInHand().getType().getId() != Integer.parseInt(deathconfig.get("CHARGE_ITEM_ID"))) {
							player.sendMessage("You must be holding a "+Material.getMaterial(Integer.parseInt(deathconfig.get("CHARGE_ITEM_ID"))).toString()+" to teleport.");
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
					
					//costs iconomy
					if (iconomyCost > 0) {
						if (checkiConomy()) {
							
							if (com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(player.getName()).hasEnough(iconomyCost)) {
								com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(player.getName()).subtract(iconomyCost);
								com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(player.getName()).save();
							}
							else {
								player.sendMessage("You need "+iconomyCost+" coins to use /deathtp");
								teleportok = false;
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

				return true;
			}
			
			else {
				System.out.println("This is only a player command.");
				return true;
			}
			
		}
		
		else if (command.equals("suicide")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setHealth(0);
			}
			else {
				sender.sendMessage("This is only a player command.");
				return true;
			}
		}
		
		else if (command.equals("streak")) {
			canUseCommand = true;
			
			if (sender instanceof Player) {
				if (Permissions != null) {
					canUseCommand = Permissions.has((Player)sender, "deathtpplus.streak");
				}
				else if (DeathTpPlus.gm != null) {
					canUseCommand = gm.getWorldsHolder().getWorldPermissions((Player)sender).has((Player)sender,"deathtpplus.streak");
				}
			}
				
			if (canUseCommand) {
				if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
					File streakFile = new File("plugins/DeathTpPlus/streak.txt");
					String line;
					String[] splittext;
					Player check;
					String playername = "player";
					
					if (args.length > 1) {
						playername = args[0];
					}
					else {
						if (sender instanceof Player) {
							check = (Player)sender;
							playername = check.getName();
						}
					}
					
					List<Player> lookup = this.getServer().matchPlayer(playername);
					
					if (lookup.size() == 0) {
						sender.sendMessage(ChatColor.RED+ "No matching player.");
						return true;
					} 
					else if (lookup.size() != 1) {
						sender.sendMessage(ChatColor.RED+ "Matched more than one player!  Be more specific!");
						return true;
					} 
					else {
						check = lookup.get(0);
						
						try {
							FileReader fr = new FileReader(streakFile);
							BufferedReader br = new BufferedReader(fr);
							boolean entryfound = false;
							while((line = br.readLine()) != null) {
								if (!line.startsWith("#")) {
									splittext = line.split(":");
									if (check.getName().matches(splittext[0])) {
										if (Integer.parseInt(splittext[1]) < 0) {
											sender.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1].replace("-", "")+" Death(s)");
										}
										else {
											sender.sendMessage(ChatColor.GRAY+check.getName()+"'s Current Streak: "+splittext[1]+" Kill(s)");
										}
										
										entryfound = true;
									}
								}
							}
							if (!entryfound) {
								sender.sendMessage("No streak found");
							}
							br.close();
							return true;
						}
						catch (IOException e) {
							System.out.println(e);
						}
					}
					
					
				}
			}

		}
		
		
		return false;
	}
	
	//iconomy methods
	public static Server getBukkitServer() {
        return Server;
    }

    public static iConomy getiConomy() {
        return iConomy;
    }
    
    public static boolean setiConomy(iConomy plugin) {
        if (iConomy == null) {
            iConomy = plugin;
        } else {
            return false;
        }
        return true;
    }
    
    public boolean checkiConomy() {
        this.useiConomy = (iConomy != null);
        return this.useiConomy;
    }
}
