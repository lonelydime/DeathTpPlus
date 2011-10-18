package org.simiancage.DeathTpPlus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import com.nijikokun.register.payment.Methods;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


//Register
import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.Method;


//craftirc
import com.ensifera.animosity.craftirc.CraftIRC;

public class DeathTpPlus extends JavaPlugin{
    //damage and death listener
    private final DTPEntityListener entityListener = new DTPEntityListener(this);

    //plugin variables
    protected Logger log;
    private DeathTpPlus plugin = this;
    public static HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();
    public static HashMap<String, String> deathconfig = new HashMap<String, String>();
    public static File configFile;
    public static File locsName;
    public static File streakFile;
    public static File deathlogFile;
    protected String logName = null;
    protected String pluginName = null;
    protected String pluginVersion = null;
    protected ArrayList<String> pluginAuthor = null;
    protected String pluginPath = null;
    protected boolean worldTravel = false;
    FileConfiguration configuration;


    //Register
    static boolean Register = false;
    boolean useRegister = false;

    //craftirc
    public static CraftIRC craftircHandle = null;

    public void onDisable() {
        log.info("[DeathTpPlus] Disabled");
    }

    public void onEnable() {
        log = Bukkit.getServer().getLogger();
        pluginName = getDescription().getName();
        logName = "[" + pluginName + "] ";
        pluginVersion = getDescription().getVersion();
        pluginAuthor = getDescription().getAuthors();
        pluginPath = getDataFolder() + System.getProperty("file.separator");
        configFile = new File(pluginPath+"config.yml");
        locsName = new File(pluginPath+"locs.txt");
        streakFile = new File(pluginPath+"streak.txt");
        deathlogFile = new File(pluginPath+"deathlog.txt");


        // Todo write Helper Class for this

        if (!configFile.exists()) {
            new File(getDataFolder().toString()).mkdir();
            try {
                JarFile jar = new JarFile("plugins" + System.getProperty("file.separator") +getDescription().getName() + ".jar");
                ZipEntry config = jar.getEntry("config.yml");
                InputStream in = new BufferedInputStream(jar.getInputStream(config));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
                int c;
                while((c = in.read()) != -1){
                    out.write(c);
                }
                out.flush();
                out.close();
                in.close();
                jar.close();
                log.info(logName + "Default config created successfully!");
            } catch (Exception e)  {
                log.warning(logName + "Default config could not be created!");

            }
        }
        configuration  = this.getConfig();
        if (!locsName.exists()) {
            CreateDefaultFile(locsName);
        }

        if (!streakFile.exists()) {
            CreateDefaultFile(streakFile);
        }

        if (!deathlogFile.exists()) {
            CreateDefaultFile(deathlogFile);
        }

        DefaultConfiguration();
        //Death Event nodes
        deathevents.put("FALL", (List<String>) configuration.getList("fall"));
        deathevents.put("DROWNING", (List<String>) configuration.getList("drowning"));
        deathevents.put("FIRE", (List<String>) configuration.getList("fire"));
        deathevents.put("FIRE_TICK", (List<String>) configuration.getList("fire_tick"));
        deathevents.put("LAVA", (List<String>) configuration.getList("lava"));
        deathevents.put("BLOCK_EXPLOSION", (List<String>) configuration.getList("block_explosion"));
        deathevents.put("CREEPER", (List<String>) configuration.getList("creeper"));
        deathevents.put("SKELETON", (List<String>) configuration.getList("skeleton"));
        deathevents.put("SPIDER", (List<String>) configuration.getList("spider"));
        deathevents.put("ZOMBIE", (List<String>) configuration.getList("zombie"));
        deathevents.put("CONTACT", (List<String>) configuration.getList("contact"));
        deathevents.put("PIGZOMBIE", (List<String>) configuration.getList("pigzombie"));
        deathevents.put("GHAST", (List<String>) configuration.getList("ghast"));
        deathevents.put("SLIME", (List<String>) configuration.getList("slime"));
        deathevents.put("PVP", (List<String>) configuration.getList("pvp"));
        deathevents.put("FISTS", (List<String>) configuration.getList("pvp-fists"));
        deathevents.put("SUFFOCATION", (List<String>) configuration.getList("suffocation"));
        deathevents.put("VOID", (List<String>) configuration.getList("void"));
        deathevents.put("WOLF", (List<String>) configuration.getList("wolf"));
        deathevents.put("LIGHTNING", (List<String>) configuration.getList("lightning"));
        deathevents.put("UNKNOWN", (List<String>) configuration.getList("unknown"));
        deathevents.put("STARVATION", (List<String>) configuration.getList("starvation"));
        deathevents.put("CAVESPIDER", (List<String>) configuration.getList("cavespider"));
        deathevents.put("ENDERMAN", (List<String>) configuration.getList("enderman"));
        //Configuration nodes
        deathconfig.put("SHOW_DEATHNOTIFY", configuration.getString("show-deathnotify"));
        deathconfig.put("ALLOW_DEATHTP", configuration.getString("allow-deathtp"));
        deathconfig.put("SHOW_STREAKS", configuration.getString("show-streaks"));
        deathconfig.put("CHARGE_ITEM_ID", configuration.getString("charge-item"));
        deathconfig.put("SHOW_SIGN", configuration.getString("show-sign"));
        deathconfig.put("REGISTER_COST", configuration.getString("deathtp-cost"));
        deathconfig.put("CRAFT_IRC_TAG", configuration.getString("deathtp-tag"));
        deathconfig.put("DEATH_LOGS", configuration.getString("allow-deathlog"));
        deathconfig.put("WORLD_TRAVEL", configuration.getString("allow-worldtravel"));
        //Kill Streak nodes
        killstreak.put("KILL_STREAK", (List<String>) configuration.getList("killstreak"));
        //Death Streak nodes
        deathstreak.put("DEATH_STREAK", (List<String>) configuration.getList("deathstreak"));
        log.info(logName+killstreak.get("KILL_STREAK").size()+" Kill Streaks loaded.");
        log.info(logName+deathstreak.get("DEATH_STREAK").size()+" Death Streaks loaded.");


        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes"))
        {
            worldTravel = true;
        }

        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes")||deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("no")||deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions"))
        {
            log.info("[" + pluginName + "] allow-wordtravel is: "+deathconfig.get("WORLD_TRAVEL"));
        } else {
            log.warning("[" + pluginName + "] Wrong allow-worldtravel value of "+deathconfig.get("WORLD_TRAVEL")+". Defaulting to NO!");
            worldTravel = false;
        }



        //Create the pluginmanage pm.
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);

        if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true")) {
            pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        }

        if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
            pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        }



        //Register



        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new server(this), Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new server(this), Priority.Monitor, this);


        //craftirc
        Plugin checkCraftIRC = this.getServer().getPluginManager().getPlugin("CraftIRC");
        if (checkCraftIRC != null) {
            try {
                craftircHandle = (CraftIRC) checkCraftIRC;
                //Todo Enable Logger
                log.info(logName+"CraftIRC Support Enabled.");
            }
            catch (ClassCastException ex) {
            }
        }




        // print success
        PluginDescriptionFile pdfFile = this.getDescription();
        // Todo Enable Logger
        log.info("[DeathTpPlus] version " + pdfFile.getVersion() + " is enabled!");
    }

    private void CreateDefaultFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            // Todo Enable Logger
            log.warning(logName+ "Cannot create file "+file.getPath()+"/"+file.getName());
        }
    }


    private void DefaultConfiguration() {
        configuration.addDefault ("fall", "");
        configuration.addDefault("drowning", "");
        configuration.addDefault("fire", "");
        configuration.addDefault("fire_tick", "");
        configuration.addDefault("lava", "");
        configuration.addDefault("block_explosion", "");
        configuration.addDefault("creeper", "");
        configuration.addDefault("skeleton", "");
        configuration.addDefault("spider", "");
        configuration.addDefault("zombie", "");
        configuration.addDefault("contact", "");
        configuration.addDefault("pigzombie", "");
        configuration.addDefault("ghast", "");
        configuration.addDefault("slime", "");
        configuration.addDefault("pvp", "");
        configuration.addDefault("pvp-fists", "");
        configuration.addDefault("suffocation", "");
        configuration.addDefault("void", "");
        configuration.addDefault("wolf", "");
        configuration.addDefault("lightning", "");
        configuration.addDefault("unknown", "");
        configuration.addDefault("starvation", "");
        configuration.addDefault("cavespider", "");
        configuration.addDefault("enderman", "");
        //Configuration nodes
        configuration.addDefault("show-deathnotify", "true");
        configuration.addDefault("allow-deathtp", "true");
        configuration.addDefault("show-streaks", "true");
        configuration.addDefault("charge-item", "0");
        configuration.addDefault("show-sign", "false");
        configuration.addDefault("deathtp-cost", "0");
        configuration.addDefault("deathtp-tag", "");
        configuration.addDefault("allow-deathlog", "true");
        configuration.addDefault("allow-worldtravel", "no");
        //Kill Streak nodes
        configuration.addDefault("killstreak", "");
        //Death Streak nodes
        configuration.addDefault("deathstreak", "");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName();
        boolean canUseCommand = true;
        boolean teleportok = true;
        boolean teleported = false;

        if (command.equals("deathtp")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                String thisWorld = player.getWorld().getName().toString();
                if (player.hasPermission("deathtpplus.worldtravel") && deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions"))
                {
                    worldTravel = true;
                }
                double registerCost = Double.valueOf(deathconfig.get("REGISTER_COST").trim()).doubleValue();

                if (player.hasPermission("deathtpplus.deathtp")) {
                    canUseCommand = true;
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

                    // Todo CHange => register
                    //costs iconomy
                    if (registerCost > 0) {
                        if (useRegister) {
                            MethodAccount account = getRegisterMethod().getAccount(player.getName());
                            if (account != null && account.hasEnough(registerCost)) {
                                account.subtract(registerCost);
                                player.sendMessage("You used "+registerCost+" to use /deathtp");
                            }
                            else {
                                player.sendMessage("You need "+registerCost+" coins to use /deathtp");
                                teleportok = false;
                            }
                        }

                    }


                    if (teleportok) {

                        try {
                            String line = "";
                            String teleloc = "";
                            String[] location;
                            FileReader fr = new FileReader(locsName);
                            BufferedReader br = new BufferedReader(fr);

                            while((line = br.readLine()) != null) {
                                if (line.contains(player.getName()+":")) {
                                    teleloc = line;
                                }
                            }

                            if (teleloc != "") {
                                location = teleloc.split(":");
                                Location sendLocation = player.getLocation();
                                double x, y, z;

                                x=Double.valueOf(location[1].trim()).doubleValue();
                                y=Double.valueOf(location[2].trim()).doubleValue();
                                z=Double.valueOf(location[3].trim()).doubleValue();
                                World deathWorld = this.getServer().getWorld(location[4].trim());
                                sendLocation.setX(x);
                                sendLocation.setY(y);
                                sendLocation.setZ(z);

                                boolean safeTele = false;
                                int test1=-1, test2=-1;
                                while (!safeTele) {
                                    test1 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                    test2 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                    if (test1 == 0 && test2 == 0) {
                                        safeTele = true;
                                    }

                                    sendLocation.setY(sendLocation.getY()+1);
                                }

                                if (!thisWorld.equals(deathWorld.getName()))
                                {
                                    if (worldTravel)
                                    {
                                        sendLocation.setWorld(deathWorld);
                                        player.teleport(sendLocation);
                                        teleported = true;
                                    } else {
                                        player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                                    }
                                }
                                else {
                                    player.teleport(sendLocation);
                                    teleported = true;
                                }
                            }
                            else {
                                player.sendMessage("You do not have a last known death location.");
                            }
                            if (useRegister && !teleported) {
                                MethodAccount account = getRegisterMethod().getAccount(player.getName());
                                account.add(registerCost);
                                player.sendMessage("Giving you back "+registerCost);
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

        else if (command.equals("deaths")) {
            String playername = "";
            String cause = "";
            String line;
            int totalnum = 0;
            String[] splittext;
            boolean foundrecord = false;

            if (sender instanceof Player) {
                Player player = (Player)sender;

                if (player.hasPermission("deathtpplus.deaths")) {
                    canUseCommand = true;
                }
            }

            if (canUseCommand) {

                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player player = (Player)sender;
                        playername = player.getName();
                    }
                    else {
                        return false;
                    }
                }
                else if (args.length == 1) {
                    playername = args[0];
                }
                else if (args.length == 2) {
                    playername = args[0];
                    cause = args[1];
                }
                else {
                    return false;
                }

                try {
                    BufferedReader br = new BufferedReader(new FileReader(deathlogFile));
                    while((line = br.readLine()) != null) {
                        splittext = line.split(":");
                        //0 = name, 1 = type, 2 = cause, 3 = number
                        if (!cause.matches("")) {
                            if (splittext[0].matches(playername) && splittext[1].matches("death") && splittext[2].matches(cause.toUpperCase())) {
                                String times = "times";
                                if (splittext[2] == "1") {
                                    times = "time";
                                }
                                sender.sendMessage(playername+" has died by "+cause+" "+splittext[3]+" "+times);
                                foundrecord = true;
                            }
                        }
                        //total count
                        else {
                            if (splittext[0].matches(playername) && splittext[1].matches("death") ) {
                                totalnum = totalnum + Integer.parseInt(splittext[3]);
                            }
                        }
                    }
                    if (cause.matches("")) {
                        String times = "times";
                        if (totalnum == 1) {
                            times = "time";
                        }
                        sender.sendMessage(playername+" has died "+totalnum+" "+times);
                    }
                    else {
                        if (!foundrecord) {
                            sender.sendMessage(playername+" has died by "+cause+" 0 times");
                        }
                    }
                    return true;
                }
                catch(Exception e) {
                    // Todo implement logger
                    log.info("[DeathTpPlus] Error reading deathlog: "+deathlogFile);
                }
            }

            else {
                return true;
            }

            return false;
        }

        else if (command.equals("kills")) {
            String playername = "";
            String username = "";
            String line;
            int totalnum = 0;
            String[] splittext;
            boolean foundrecord = false;

            if (sender instanceof Player) {
                Player player = (Player)sender;

                if (player.hasPermission("deathtpplus.kills")) {
                    canUseCommand = true;
                }
            }

            if (canUseCommand) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player player = (Player)sender;
                        playername = player.getName();
                    }
                    else {
                        return false;
                    }
                }
                else if (args.length == 1) {
                    playername = args[0];
                }
                else if (args.length == 2) {
                    playername = args[0];
                    username = args[1];
                }                 else {
                    return false;
                }
                //File deathlogFile = new File(getDataFolder()+"/deathlog.txt");
                try {
                    BufferedReader br = new BufferedReader(new FileReader(deathlogFile));
                    while((line = br.readLine()) != null) {
                        splittext = line.split(":");
                        //0 = name, 1 = type, 2 = cause, 3 = number
                        if (!username.matches("")) {
                            if (splittext[0].matches(playername) && splittext[1].matches("kill") && splittext[2].matches(username)) {
                                String times = "times";
                                if (splittext[2] == "1")
                                    times = "time";
                                sender.sendMessage(playername+" has killed "+username+" "+splittext[3]+" "+times);
                                foundrecord = true;
                            }
                        }
                        //total count
                        else {
                            if (splittext[0].matches(playername) && splittext[1].matches("kill") ) {
                                totalnum = totalnum + Integer.parseInt(splittext[3]);
                            }
                        }
                    }
                    if (username.matches("")) {
                        String times = "times";
                        if (totalnum == 1) {
                            times = "time";
                        }
                        sender.sendMessage(playername+" has killed "+totalnum+" "+times);
                    }
                    else {
                        if (!foundrecord){
                           sender.sendMessage(playername+" has killed "+username+" 0 times");
                        }
                    }
                    return true;
                }
                catch(Exception e) {
                    // Todo implement logger
                    log.info("[DeathTpPlus] Error reading deathlog: "+deathlogFile);
                }
            }
            else {
                return true;
            }

            return false;
        }

        else if (command.equals("streak")) {
            // Todo ???
            canUseCommand = true;

            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (player.hasPermission("deathtpplus.streak")) {
                    canUseCommand = true;
                }
            }

            if (canUseCommand) {
                if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") ) {
                    // File streakFile = new File("plugins/DeathTpPlus/streak.txt");
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

    public String convertSamloean(String convert) {
        convert = convert.replace("&0", "�0");
        convert = convert.replace("&1", "�1");
        convert = convert.replace("&2", "�2");
        convert = convert.replace("&3", "�3");
        convert = convert.replace("&4", "�4");
        convert = convert.replace("&5", "�5");
        convert = convert.replace("&6", "�6");
        convert = convert.replace("&7", "�7");
        convert = convert.replace("&8", "�8");
        convert = convert.replace("&9", "�9");
        convert = convert.replace("&a", "�a");
        convert = convert.replace("&b", "�b");
        convert = convert.replace("&c", "�c");
        convert = convert.replace("&d", "�d");
        convert = convert.replace("&e", "�e");
        convert = convert.replace("&f", "�f");

        return convert;
    }


    public Method getRegisterMethod(){
        try{
            return Methods.getMethod();
        } catch(NoClassDefFoundError err){
        } // ugly solution, I know ...
        return null;

    }
}
