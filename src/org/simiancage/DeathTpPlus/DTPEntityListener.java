package org.simiancage.DeathTpPlus;

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
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;


import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class DTPEntityListener extends EntityListener {
    public static DeathTpPlus plugin;
    public ArrayList<String> lastDamagePlayer = new ArrayList<String>();
    public ArrayList<String> lastDamageType = new ArrayList<String>();
    public String beforedamage = "";
    public PlayerDeathEvent playerDeathEvent = null;
    enum DeathTypes {FALL, DROWNING, SUFFOCATION, FIRE_TICK, FIRE, LAVA, BLOCK_EXPLOSION, CREEPER, SKELETON, SPIDER, PIGZOMBIE, ZOMBIE, CONTACT, SLIME, VOID, GHAST, WOLF, LIGHTNING, STARVATION, CAVESPIDER, ENDERMAN, PVP, FISTS, UNKNOWN;

        @Override public String toString() {
            //only capitalize the first letter
            String s = super.toString();
            return s.substring(0, 1)+s.substring(1).toLowerCase();
        }
    }
    
    public DTPEntityListener(DeathTpPlus instance) {
        plugin = instance;
    }

    public String getEvent (String deathType){
        int messageindex = 0;
        if (DeathTpPlus.deathevents.get(deathType).size() > 1)
        {
            Random rand = new Random();
            messageindex = rand.nextInt(DeathTpPlus.deathevents.get(deathType).size());
        }
        return DeathTpPlus.deathevents.get(deathType).get(messageindex);
    }



    public void onEntityDeath(EntityDeathEvent event) {

        beforedamage = "";
        try {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                String damagetype = lastDamageType.get(lastDamagePlayer.indexOf(player.getDisplayName()));
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
                    fileOutput = player.getName()+":"+player.getLocation().getX()+":"+player.getLocation().getY()+":"+player.getLocation().getZ()+":"+player.getWorld().getName().toString();
                    try {
                        FileReader fr = new FileReader(DeathTpPlus.locsName);
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

                        BufferedWriter out = new BufferedWriter(new FileWriter(DeathTpPlus.locsName));

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
                        System.out.println("cannot read file "+DeathTpPlus.locsName);
                        System.out.println(e);
                    }
                }

                if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") || DeathTpPlus.deathconfig.get("DEATH_LOGS").equals("true") ) {
                    howtheydied = damagetype.split(":");
                    loghowdied = howtheydied[0];
                    // Todo change into case statement and create methods for eventAnnounce

                    eventAnnounce = getEvent(howtheydied[0]).replace("%n", player.getDisplayName());

                    if (howtheydied[0].matches("PVP")) {
                        if (howtheydied[2].equals("bare hands")) {
                            eventAnnounce = getEvent("FISTS").replace("%n", player.getDisplayName());
                        }

                        loghowdied = howtheydied[2];
                        eventAnnounce = eventAnnounce.replace("%i", howtheydied[1]);
                        eventAnnounce = eventAnnounce.replace("%a", howtheydied[2]);
                        if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").matches("true")){

                            writeToStreak(player.getDisplayName(), howtheydied[2]);
                        }
                        //write kill to deathlog
                        if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                            writeToLog("kill", howtheydied[2], player.getDisplayName());
                        }
                    }
                    if (eventAnnounce.equals(""))
                    {
                        eventAnnounce = getEvent("UNKNOWN").replace("%n", player.getDisplayName());
                    }

                    eventAnnounce = plugin.convertSamloean(eventAnnounce);

                    if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true")) {
                        //plugin.getServer().broadcastMessage(eventAnnounce);
                        if (event instanceof PlayerDeathEvent) {
                            playerDeathEvent = (PlayerDeathEvent) event;
                            playerDeathEvent.setDeathMessage(eventAnnounce);
                        }

                    }

                    //CraftIRC
                    if (DeathTpPlus.craftircHandle != null) {
                        String ircAnnounce;
                        ircAnnounce = eventAnnounce.replace("�0", "");
                        ircAnnounce = ircAnnounce.replace("�2", "");
                        ircAnnounce = ircAnnounce.replace("�3", "");
                        ircAnnounce = ircAnnounce.replace("�4", "");
                        ircAnnounce = ircAnnounce.replace("�5", "");
                        ircAnnounce = ircAnnounce.replace("�6", "");
                        ircAnnounce = ircAnnounce.replace("�7", "");
                        ircAnnounce = ircAnnounce.replace("�8", "");
                        ircAnnounce = ircAnnounce.replace("�9", "");
                        ircAnnounce = ircAnnounce.replace("�a", "");
                        ircAnnounce = ircAnnounce.replace("�b", "");
                        ircAnnounce = ircAnnounce.replace("�c", "");
                        ircAnnounce = ircAnnounce.replace("�d", "");
                        ircAnnounce = ircAnnounce.replace("�e", "");
                        ircAnnounce = ircAnnounce.replace("�f", "");

                        DeathTpPlus.craftircHandle.sendMessageToTag(ircAnnounce, DeathTpPlus.deathconfig.get("CRAFT_IRC_TAG"));
                    }

                    if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                        writeToLog("death", player.getDisplayName(), loghowdied);
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
                            sign.setLine(1, player.getDisplayName());
                            sign.setLine(2, "Died by");
                            signtext = howtheydied[0].substring(0, 1)+howtheydied[0].substring(1).toLowerCase();
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
                            writeToStreak(player.getDisplayName(), howtheydied[2]);
                    }

                    if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                        writeToLog("death", player.getDisplayName(), loghowdied);
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
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent) event;
            Entity attacker = mobevent.getDamager();
            if (attacker instanceof Fireball) {
                lastdamage = (((Fireball) attacker).getShooter().toString());
            }
            else if (attacker instanceof Arrow) {

                lastdamage = ((Arrow) attacker).getShooter().toString();
            }

            // Todo check if duplicate
            /*else if (attacker instanceof Player) {
                Player pvper = (Player) attacker;
                String usingitem = pvper.getItemInHand().getType().name();
                if (usingitem == "AIR") {
                    usingitem = "BARE_KNUCKLES";
                }
                lastdamage = "PVP:"+usingitem+":"+pvper.getName();
            }*/
            else if (attacker.toString().toLowerCase().matches("craftslime")) {
                lastdamage = "SLIME";
            }

            else if (attacker instanceof Wolf) {
                //Wolf wolf = (Wolf)attacker;
                //TODO wolf owner logic
                lastdamage = "WOLF";
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
                lastdamage = "PVP:"+usingitem+":"+pvper.getDisplayName();
            }
        }

        if ((beforedamage.equals("GHAST") && lastdamage.equals("BLOCK_EXPLOSION")) ||(beforedamage.equals("GHAST") && lastdamage.equals("GHAST"))) {
            lastdamage = "GHAST";
        }

        if (!lastDamagePlayer.contains(player.getDisplayName())) {
            lastDamagePlayer.add(player.getDisplayName());
            lastDamageType.add(event.getCause().name());
        }
        else {
            lastDamageType.set(lastDamagePlayer.indexOf(player.getDisplayName()), lastdamage);
        }

        beforedamage = lastdamage;
    }

    public void writeToStreak(String defender, String attacker) {

        //read the file
        try {
            String line = "";
            ArrayList<String> filetext = new ArrayList<String>();

            //File streakFile = new File("plugins/DeathTpPlus/streak.txt");
            //File streakFile = new File(plugin.getDataFolder()+"/streak.txt");
            BufferedReader br = new BufferedReader(new FileReader(DeathTpPlus.streakFile));
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
            BufferedWriter out = new BufferedWriter(new FileWriter(DeathTpPlus.streakFile));

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
        // File deathlogFile = new File(plugin.getDataFolder()+"/deathlog.txt");
        File deathlogTempFile = new File(plugin.getDataFolder()+System.getProperty("file.separator")+"deathtlog.tmp");
        String line = "";
        String[] splittext;
        String writeline = "";
        int newrecord = 0;
        boolean foundrecord = false;

        if (!deathlogTempFile.exists()) {
            try {
                deathlogTempFile.createNewFile();
            } catch (IOException e) {
                System.out.println("cannot create file "+deathlogTempFile);
            }
        }

        try {
            //format name:type:mob/player:number
            PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
            BufferedReader br = new BufferedReader(new FileReader(DeathTpPlus.deathlogFile));

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

            DeathTpPlus.deathlogFile.delete();
            deathlogTempFile.renameTo(DeathTpPlus.deathlogFile);
        }
        catch(IOException e) {
            System.out.println("Could not edit deathlog: "+e);
        }

    }
}
