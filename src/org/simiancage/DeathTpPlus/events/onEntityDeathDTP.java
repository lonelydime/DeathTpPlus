package org.simiancage.DeathTpPlus.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LocaleHelperDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.simiancage.DeathTpPlus.listeners.EntityListenerDTP;
import org.simiancage.DeathTpPlus.objects.TombBlockDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;
import org.simiancage.DeathTpPlus.workers.TombWorkerDTP;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PluginName: DeathTpPlus
 * Class: onEntityDeathDTP
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:27
 */

public class onEntityDeathDTP {

    private LoggerDTP log;
    private ConfigDTP config;
    private DeathTpPlus plugin;
    private EntityListenerDTP entityListenerDTP;
    private TombWorkerDTP tombWorker = TombWorkerDTP.getInstance();
    private String loghowdied;


    public onEntityDeathDTP(DeathTpPlus plugin) {
        this.log = LoggerDTP.getLogger();
        this.config = ConfigDTP.getInstance();
        this.tombWorker = TombWorkerDTP.getInstance();
        this.plugin = plugin;
    }

    public void oEDeaDeathTp (DeathTpPlus plugin, EntityListenerDTP entityListenerDTP, EntityDeathEvent entityDeathEvent) {
        log.debug("onEntityDeath DeathTP executing");
        Player player = (Player) entityDeathEvent.getEntity();
        ArrayList<String> filetext = new ArrayList<String>();
        boolean readCheck = false;
        boolean newPlayerDeath = true;
        String fileOutput = "";
        String line = "";
        //text to write to file
        fileOutput = player.getName()+":"+player.getLocation().getX()+":"+player.getLocation().getY()+":"+player.getLocation().getZ()+":"+player.getWorld().getName().toString();
        try {
            FileReader fr = new FileReader(plugin.locsName);
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

            BufferedWriter out = new BufferedWriter(new FileWriter(plugin.locsName));

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
            log.debug("Succesfully wrote DeathTp location to file: " + plugin.locsName);
        }
        catch (IOException e) {
            log.warning("cannot write file "+ plugin.locsName , e);
        }
    }

    public void oEDeaGeneralDeath (DeathTpPlus plugin, EntityListenerDTP entityListenerDTP, EntityDeathEvent entityDeathEvent) {
        log.debug("onEntityDeath GeneralDeath executing");
        String eventAnnounce = "";
        Player player = (Player) entityDeathEvent.getEntity();
        String[] howtheydied;
        PlayerDeathEvent playerDeathEvent = null;
        String damagetype = entityListenerDTP.getLastDamageType().get(entityListenerDTP.getLastDamagePlayer().indexOf(player.getName()));
        howtheydied = damagetype.split(":");

        // Todo change into case statement and create methods for eventAnnounce

        if ((howtheydied[0])==null){
            howtheydied[0] = "UNKNOWN";
        }

        loghowdied = howtheydied[0];

        eventAnnounce = getEventMessage(howtheydied[0]).replace("%n", player.getDisplayName());

        if (howtheydied[0].matches("PVP")) {
            if (howtheydied[2].equals("bare hands")) {
                eventAnnounce = getEventMessage("FISTS").replace("%n", player.getDisplayName());
            }

            loghowdied = howtheydied[2];
            eventAnnounce = eventAnnounce.replace("%i", howtheydied[1]);
            eventAnnounce = eventAnnounce.replace("%a", howtheydied[2]);
            if (config.isShowStreaks()){
                writeToStreak(player.getName(), howtheydied[2]);
            }
            //write kill to deathlog
            if (config.isAllowDeathLog()) {
                writeToLog("kill", howtheydied[2], player.getName());
            }
        }
        if (eventAnnounce.equals(""))
        {
            eventAnnounce = getEventMessage("UNKNOWN").replace("%n", player.getDisplayName());
        }

        eventAnnounce = plugin.convertSamloean(eventAnnounce);

        if (config.isShowDeathNotify()) {
            ShowDeathNotify(entityDeathEvent, eventAnnounce);
        }

        //CraftIRC
        if (plugin.craftircHandle != null) {
            CraftIRCSendMessage(plugin, eventAnnounce);
        }

        if (config.isAllowDeathLog()) {
            writeToLog("death", player.getName(), loghowdied);
        }

        if (config.isShowDeathSign()) {
            ShowDeathSign(player, howtheydied, loghowdied);
        }
// Tomb part
        if (config.isEnableTomb())
        {
            UpdateTomb(player, howtheydied);
        }

// Tombstone part
        if (config.isEnableTombStone()){
            CreateTombStone(plugin, entityDeathEvent, player);
        }
    }

    private void CreateTombStone(DeathTpPlus plugin, EntityDeathEvent entityDeathEvent, Player player) {
        if (!plugin.hasPerm(player, "tombstone.use", false))
            return;

        log.debug(player.getName() + " died.");

        if (entityDeathEvent.getDrops().size() == 0) {
            plugin.sendMessage(player, "Inventory Empty.");
            log.debug(player.getName() + " inventory empty.");
            return;
        }

// Get the current player location.
        Location loc = player.getLocation();
        Block block = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(),
                loc.getBlockZ());

// If we run into something we don't want to destroy, go one up.
        if (block.getType() == Material.STEP
                || block.getType() == Material.TORCH
                || block.getType() == Material.REDSTONE_WIRE
                || block.getType() == Material.RAILS
                || block.getType() == Material.STONE_PLATE
                || block.getType() == Material.WOOD_PLATE
                || block.getType() == Material.REDSTONE_TORCH_ON
                || block.getType() == Material.REDSTONE_TORCH_OFF
                || block.getType() == Material.CAKE_BLOCK) {
            block = player.getWorld().getBlockAt(loc.getBlockX(),
                    loc.getBlockY() + 1, loc.getBlockZ());
        }

// Don't create the chest if it or its sign would be in the void
        if (config.isVoidCheck()
                && ((config.isShowTombStoneSign() && block.getY() > 126)
                || (!config.isShowTombStoneSign() && block.getY() > 127) || player
                .getLocation().getY() < 1)) {
            plugin.sendMessage(player,
                    "Your tombstone would be in the Void. Inventory dropped");
            log.debug(player.getName() + " died in the Void.");
            return;
        }

// Check if the player has a chest.
        int pChestCount = 0;
        int pSignCount = 0;
        for (ItemStack item : entityDeathEvent.getDrops()) {
            if (item == null)
                continue;
            if (item.getType() == Material.CHEST)
                pChestCount += item.getAmount();
            if (item.getType() == Material.SIGN)
                pSignCount += item.getAmount();
        }

        if (pChestCount == 0
                && !(plugin.hasPerm(player, "tombstone.freechest", false))) {
            plugin.sendMessage(player,
                    "No chest found in inventory. Inventory dropped");
            log.debug(player.getName() + " No chest in inventory.");
            return;
        }

// Check if we can replace the block.
        block = plugin.findPlace(block, false);
        if (block == null) {
            plugin.sendMessage(player,
                    "Could not find room for chest. Inventory dropped");
            log.debug(player.getName() + " Could not find room for chest.");
            return;
        }

// Check if there is a nearby chest
        if (!config.isAllowInterfere() && checkChest(block)) {
            plugin.sendMessage(player,
                    "There is a chest interfering with your tombstone. Inventory dropped");
            log.debug(player.getName()
                    + " Chest interfered with tombstone creation.");
            return;
        }

        int removeChestCount = 1;
        int removeSign = 0;

// Do the check for a large chest block here so we can check for
// interference
        Block lBlock = findLarge(block);

// Set the current block to a chest, init some variables for later use.
        block.setType(Material.CHEST);
// We're running into issues with 1.3 where we can't cast to a Chest :(
        BlockState state = block.getState();
        if (!(state instanceof Chest)) {
            plugin.sendMessage(player, "Could not access chest. Inventory dropped.");
            log.debug(player.getName() + " Could not access chest.");
            return;
        }
        Chest sChest = (Chest) state;
        Chest lChest = null;
        int slot = 0;
        int maxSlot = sChest.getInventory().getSize();

// Check if they need a large chest.
        if (entityDeathEvent.getDrops().size() > maxSlot) {
// If they are allowed spawn a large chest to catch their entire
// inventory.
            if (lBlock != null && plugin.hasPerm(player, "tombstone.large", false)) {
                removeChestCount = 2;
// Check if the player has enough chests
                if (pChestCount >= removeChestCount
                        || plugin.hasPerm(player, "tombstone.freechest", false)) {
                    lBlock.setType(Material.CHEST);
                    lChest = (Chest) lBlock.getState();
                    maxSlot = maxSlot * 2;
                } else {
                    removeChestCount = 1;
                }
            }
        }

// Don't remove any chests if they get a free one.
        if (plugin.hasPerm(player, "tombstone.freechest", false))
            removeChestCount = 0;

// Check if we have signs enabled, if the player can use signs, and if
// the player has a sign or gets a free sign
        Block sBlock = null;
        if (config.isShowTombStoneSign() && plugin.hasPerm(player, "tombstone.sign", false)
                && (pSignCount > 0 || plugin.hasPerm(player, "tombstone.freesign", false))) {
// Find a place to put the sign, then place the sign.
            sBlock = sChest.getWorld().getBlockAt(sChest.getX(),
                    sChest.getY() + 1, sChest.getZ());
            if (plugin.canReplace(sBlock.getType())) {
                createSign(sBlock, player);
                removeSign = 1;
            } else if (lChest != null) {
                sBlock = lChest.getWorld().getBlockAt(lChest.getX(),
                        lChest.getY() + 1, lChest.getZ());
                if (plugin.canReplace(sBlock.getType())) {
                    createSign(sBlock, player);
                    removeSign = 1;
                }
            }
        }
// Don't remove a sign if they get a free one
        if (plugin.hasPerm(player, "tombstone.freesign", false))
            removeSign = 0;

// Create a TombBlock for this tombstone
        TombBlockDTP tBlockDTP = new TombBlockDTP(sChest.getBlock(),
                (lChest != null) ? lChest.getBlock() : null, sBlock,
                player.getName(), (System.currentTimeMillis() / 1000));

// Protect the chest/sign if LWC is installed.
        Boolean prot = false;
        Boolean protLWC = false;
        if (plugin.hasPerm(player, "tombstone.lwc", false))
            prot = plugin.activateLWC(player, tBlockDTP);
        tBlockDTP.setLwcEnabled(prot);
        if (prot)
            protLWC = true;

// Protect the chest with Lockette if installed, enabled, and
// unprotected.
        if (plugin.hasPerm(player, "tombstone.lockette", false)){
            prot = plugin.protectWithLockette(player, tBlockDTP);
        }
// Add tombstone to list
        plugin.tombListDTP.offer(tBlockDTP);

// Add tombstone blocks to tombBlockList
        plugin.tombBlockList.put(tBlockDTP.getBlock().getLocation(), tBlockDTP);
        if (tBlockDTP.getLBlock() != null)
            plugin.tombBlockList.put(tBlockDTP.getLBlock().getLocation(), tBlockDTP);
        if (tBlockDTP.getSign() != null)
            plugin.tombBlockList.put(tBlockDTP.getSign().getLocation(), tBlockDTP);

// Add tombstone to player lookup list
        ArrayList<TombBlockDTP> pList = plugin.playerTombList.get(player.getName());
        if (pList == null) {
            pList = new ArrayList<TombBlockDTP>();
            plugin.playerTombList.put(player.getName(), pList);
        }
        pList.add(tBlockDTP);

        plugin.saveTombStoneList(player.getWorld().getName());

// Next get the players inventory using the getDrops() method.
        for (Iterator<ItemStack> iter = entityDeathEvent.getDrops().listIterator(); iter
                .hasNext();) {
            ItemStack item = iter.next();
            if (item == null)
                continue;
// Take the chest(s)
            if (removeChestCount > 0 && item.getType() == Material.CHEST) {
                if (item.getAmount() >= removeChestCount) {
                    item.setAmount(item.getAmount() - removeChestCount);
                    removeChestCount = 0;
                } else {
                    removeChestCount -= item.getAmount();
                    item.setAmount(0);
                }
                if (item.getAmount() == 0) {
                    iter.remove();
                    continue;
                }
            }

// Take a sign
            if (removeSign > 0 && item.getType() == Material.SIGN) {
                item.setAmount(item.getAmount() - 1);
                removeSign = 0;
                if (item.getAmount() == 0) {
                    iter.remove();
                    continue;
                }
            }

// Add items to chest if not full.
            if (slot < maxSlot) {
                if (slot >= sChest.getInventory().getSize()) {
                    if (lChest == null)
                        continue;
                    lChest.getInventory().setItem(
                            slot % sChest.getInventory().getSize(), item);
                } else {
                    sChest.getInventory().setItem(slot, item);
                }
                iter.remove();
                slot++;
            } else if (removeChestCount == 0)
                break;
        }

// Tell the player how many items went into chest.
        String msg = "Inventory stored in chest. ";
        if (entityDeathEvent.getDrops().size() > 0)
            msg += entityDeathEvent.getDrops().size() + " items wouldn't fit in chest.";
        plugin.sendMessage(player, msg);
        log.debug(player.getName() + " " + msg);
        if (prot && protLWC) {
            plugin.sendMessage(player, "Chest protected with LWC. "
                    + config.getRemoveTombStoneSecurityTimeOut() + "s before chest is unprotected.");
            log.debug(player.getName() + " Chest protected with LWC. "
                    + config.getRemoveTombStoneSecurityTimeOut() + "s before chest is unprotected.");
        }
        if (prot && !protLWC) {
            plugin.sendMessage(player, "Chest protected with Lockette. "
                    + config.getRemoveTombStoneSecurityTimeOut() + "s before chest is unprotected.");
            log.debug(player.getName() + " Chest protected with Lockette.");
        }
        if (config.isRemoveTombStone()) {
            plugin.sendMessage(player, "Chest will break in " + config.getRemoveTombStoneTime()
                    + "s unless an override is specified.");
            log.debug(player.getName() + " Chest will break in "
                    + config.getRemoveTombStoneTime() + "s");
        }
        if (config.isRemoveTombStoneWhenEmpty() && config.isKeepTombStoneUntilEmpty())
            plugin.sendMessage(
                    player,
                    "Break override: Your tombstone will break when it is emptied, but will not break until then.");
        else {
            if (config.isRemoveTombStoneWhenEmpty())
                plugin.sendMessage(player,
                        "Break override: Your tombstone will break when it is emptied.");
            if (config.isKeepTombStoneUntilEmpty())
                plugin.sendMessage(player,
                        "Break override: Your tombstone will not break until it is empty.");
        }
    }

    private void UpdateTomb(Player player, String[] howtheydied) {
        if (tombWorker.hasTomb(player.getName())) {
            TombDTP TombDTP = tombWorker.getTomb(player.getName());
            log.debug("TombDTP",TombDTP );
            String signtext;

            if (howtheydied[0].equals("PVP"))
                signtext = LocaleHelperDTP.getInstance().getPvpLocale(howtheydied[2]);
            else
                signtext = LocaleHelperDTP.getInstance().getLocale(
                        howtheydied[0].toLowerCase());
            int deathLimit = config.getMaxDeaths();
            TombDTP.addDeath();
            if (deathLimit != 0 && (TombDTP.getDeaths() % deathLimit) == 0) {
                TombDTP.resetTombBlocks();
                player.sendMessage(tombWorker.graveDigger
                        + "You've reached the number of deaths before TombDTP reset.("
                        + ChatColor.DARK_RED + deathLimit + ChatColor.WHITE
                        + ") All your tombs are now destroyed.");
            } else {
                TombDTP.setReason(signtext);
                TombDTP.setDeathLoc(player.getLocation());
                TombDTP.updateDeath();
            }
        }
    }

    private void ShowDeathSign(Player player, String[] howtheydied, String loghowdied) {
        //place sign
        Block signBlock = player.getWorld().getBlockAt(player.getLocation().getBlockX(),
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ());

        signBlock.setType(Material.SIGN_POST);

        BlockState state = signBlock.getState();

        if (state instanceof Sign) {
            Sign sign = (Sign)state;
            String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
            String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
            String name = player.getName();
            String reason = loghowdied.substring(0, 1)+loghowdied.substring(1).toLowerCase();
            if (howtheydied[0].equals("PVP")){
                reason = howtheydied[2];
            }
            String[] signMessage = config.getTombStoneSign();
            for (int x = 0; x < 4; x++) {
                String line = signMessage[x];
                line = line.replace("{name}", name);
                line = line.replace("{date}", date);
                line = line.replace("{time}", time);
                line = line.replace("{reason}", reason);
                if (line.length() > 15)
                    line = line.substring(0, 15);
                sign.setLine(x, line);
            }
        }
    }

    private void CraftIRCSendMessage(DeathTpPlus plugin, String eventAnnounce) {
        String ircAnnounce;
        ircAnnounce = eventAnnounce.replace("§0", "");
        ircAnnounce = ircAnnounce.replace("§2", "");
        ircAnnounce = ircAnnounce.replace("§3", "");
        ircAnnounce = ircAnnounce.replace("§4", "");
        ircAnnounce = ircAnnounce.replace("§5", "");
        ircAnnounce = ircAnnounce.replace("§6", "");
        ircAnnounce = ircAnnounce.replace("§7", "");
        ircAnnounce = ircAnnounce.replace("§8", "");
        ircAnnounce = ircAnnounce.replace("§9", "");
        ircAnnounce = ircAnnounce.replace("§a", "");
        ircAnnounce = ircAnnounce.replace("§b", "");
        ircAnnounce = ircAnnounce.replace("§c", "");
        ircAnnounce = ircAnnounce.replace("§d", "");
        ircAnnounce = ircAnnounce.replace("§e", "");
        ircAnnounce = ircAnnounce.replace("§f", "");
        plugin.craftircHandle.sendMessageToTag(ircAnnounce, config.getIrcDeathTpTag());
    }

    private void ShowDeathNotify(EntityDeathEvent entityDeathEvent, String eventAnnounce) {
        PlayerDeathEvent playerDeathEvent;//plugin.getServer().broadcastMessage(eventAnnounce);
        if (entityDeathEvent instanceof PlayerDeathEvent) {
            playerDeathEvent = (PlayerDeathEvent) entityDeathEvent;
            playerDeathEvent.setDeathMessage(eventAnnounce);
        }
    }


// Helper Methods



    private void createSign(Block signBlock, Player p) {
        String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
        String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
        String name = p.getName();
        String reason = loghowdied.substring(0, 1)+loghowdied.substring(1).toLowerCase();

        signBlock.setType(Material.SIGN_POST);
        final Sign sign = (Sign) signBlock.getState();
        String[] signMessage = config.getTombStoneSign();
        for (int x = 0; x < 4; x++) {
            String line = signMessage[x];
            line = line.replace("{name}", name);
            line = line.replace("{date}", date);
            line = line.replace("{time}", time);
            line = line.replace("{reason}", reason);

            if (line.length() > 15)
                line = line.substring(0, 15);
            sign.setLine(x, line);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        sign.update();
                    }
                });
    }

    Block findLarge(Block base) {
// Check all 4 sides for air.
        Block exp;
        exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(),
                base.getZ());
        if (plugin.canReplace(exp.getType())
                && (config.isAllowInterfere() || !checkChest(exp)))
            return exp;
        exp = base.getWorld().getBlockAt(base.getX(), base.getY(),
                base.getZ() - 1);
        if (plugin.canReplace(exp.getType())
                && (config.isAllowInterfere() || !checkChest(exp)))
            return exp;
        exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(),
                base.getZ());
        if (plugin.canReplace(exp.getType())
                && (config.isAllowInterfere() || !checkChest(exp)))
            return exp;
        exp = base.getWorld().getBlockAt(base.getX(), base.getY(),
                base.getZ() + 1);
        if (plugin.canReplace(exp.getType())
                && (config.isAllowInterfere() || !checkChest(exp)))
            return exp;
        return null;
    }

    boolean checkChest(Block base) {
// Check all 4 sides for a chest.
        Block exp;
        exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(),
                base.getZ());
        if (exp.getType() == Material.CHEST)
            return true;
        exp = base.getWorld().getBlockAt(base.getX(), base.getY(),
                base.getZ() - 1);
        if (exp.getType() == Material.CHEST)
            return true;
        exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(),
                base.getZ());
        if (exp.getType() == Material.CHEST)
            return true;
        exp = base.getWorld().getBlockAt(base.getX(), base.getY(),
                base.getZ() + 1);
        if (exp.getType() == Material.CHEST)
            return true;
        return false;
    }


    String getEventMessage (String deathType){
        int messageindex = 0;
        if (config.getDeathevents().get(deathType).size() > 1)
        {
            Random rand = new Random();
            messageindex = rand.nextInt(config.getDeathevents().get(deathType).size());
        }
        return config.getDeathevents().get(deathType).get(messageindex);
    }

    void writeToStreak(String defender, String attacker) {

        //read the file
        try {
            String line = "";
            ArrayList<String> filetext = new ArrayList<String>();

            //File streakFile = new File("plugins/DeathTpPlus/streak.txt");
            //File streakFile = new File(plugin.getDataFolder()+"/streak.txt");
            BufferedReader br = new BufferedReader(new FileReader(plugin.streakFile));
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
            HashMap<String, List<String>> deathstreak = config.getDeathstreak();
            for (int i=0;i < deathstreak.get("DEATH_STREAK").size();i++) {
                teststreak = deathstreak.get("DEATH_STREAK").get(i);
                testsplit = teststreak.split(":");
                if (Integer.parseInt(testsplit[0]) == -(defCurrentStreak)) {
                    String announce = plugin.convertSamloean(testsplit[1]);
                    plugin.getServer().broadcastMessage(announce.replace("%n", defender));
                }
            }
            //Kills
            HashMap<String, List<String>> killstreak = config.getKillstreak();
            for (int i=0;i < killstreak.get("KILL_STREAK").size();i++) {
                teststreak = killstreak.get("KILL_STREAK").get(i);
                testsplit = teststreak.split(":");
                if (Integer.parseInt(testsplit[0]) == atkCurrentStreak) {
                    String announce = plugin.convertSamloean(testsplit[1]);
                    plugin.getServer().broadcastMessage(announce.replace("%n", attacker));
                }
            }

            // Write streaks to file
            BufferedWriter out = new BufferedWriter(new FileWriter(plugin.streakFile));

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
            log.warning("Could not write to Death Streak File", e);
        }
    }

    void writeToLog(String logtype, String playername, String deathtype) {
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
                log.warning("cannot create file "+deathlogTempFile);
            }
        }

        try {
            //format name:type:mob/player:number
            PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
            BufferedReader br = new BufferedReader(new FileReader(plugin.deathlogFile));

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

            plugin.deathlogFile.delete();
            deathlogTempFile.renameTo(plugin.deathlogFile);
        }
        catch(IOException e) {
            log.warning("Could not edit deathlog", e);
        }

    }
}


