package com.lonelydime.DeathTpPlus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

public class DeathTpPlus extends JavaPlugin{
	private final DTPEntityListener entityListener = new DTPEntityListener(this);
	private final DTPPlayerListener playerListener = new DTPPlayerListener(this);
	
	private final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();
    public static HashMap<String, String> deathconfig = new HashMap<String, String>();
    public static PermissionHandler Permissions = null;
    
	public void onDisable() {
		log.info("[DeathTpPlus] Disabled");
	}
	
	/*public static void showinLog(String message) {
		System.out.println("DeathTpPlus: "+message);
		log.info(message);
	}*/

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
		deathevents.put("DMGUNKNOWN", getConfiguration().getStringList("unknown", null));
		//Configuration nodes
		deathconfig.put("SHOW_DEATHNOTIFY", getConfiguration().getString("show-deathnotify", "false"));
		deathconfig.put("ALLOW_DEATHTP", getConfiguration().getString("allow-deathtp", "false"));
		deathconfig.put("SHOW_STREAKS", getConfiguration().getString("show-streaks", "false"));
		deathconfig.put("CHARGE_ITEM_ID", getConfiguration().getString("charge-item", "false"));
		deathconfig.put("SHOW_SIGN", getConfiguration().getString("show-sign", "false"));
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
            //pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_BLOCK, entityListener, Priority.Normal, this);
        }
        
        if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
	        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
	        //pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_PROJECTILE, entityListener, Priority.Normal, this);
	        //pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);
        }
        
        if (DeathTpPlus.deathconfig.get("ALLOW_DEATHTP").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
        	pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener, Event.Priority.Normal, this);
        }
        
        setupPermissions();
        
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info("[DeathTpPlus] version " + pdfFile.getVersion() + " by lonelydime is enabled!");
	}
	
	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if(Permissions == null) {
		    if(test != null) {
		    	Permissions = ((Permissions)test).getHandler();
		    }
		}
	}
}
