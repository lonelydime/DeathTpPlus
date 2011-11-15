package org.simiancage.DeathTpPlus.listeners;

//java imports

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DTPTombBlock;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.DTPTomb;
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;
import org.simiancage.DeathTpPlus.workers.DTPLocaleWorker;
import org.simiancage.DeathTpPlus.workers.DTPTombWorker;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

//bukkit imports

public class DTPEntityListener extends EntityListener {
    private DeathTpPlus plugin;
    private ArrayList<String> lastDamagePlayer = new ArrayList<String>();
    private ArrayList<String> lastDamageType = new ArrayList<String>();
    private String beforedamage = "";
    private PlayerDeathEvent playerDeathEvent = null;
    private String loghowdied;
    protected DTPTombWorker worker = DTPTombWorker.getInstance();
    enum DeathTypes {FALL, DROWNING, SUFFOCATION, FIRE_TICK, FIRE, LAVA, BLOCK_EXPLOSION, CREEPER, SKELETON, SPIDER, PIGZOMBIE, ZOMBIE, CONTACT, SLIME, VOID, GHAST, WOLF, LIGHTNING, STARVATION, CAVESPIDER, ENDERMAN, SILVERFISH, PVP, FISTS, UNKNOWN, SUICIDE;

        @Override public String toString() {
            //only capitalize the first letter
            String s = super.toString();
            return s.substring(0, 1)+s.substring(1).toLowerCase();
        }
    }
    private DTPConfig config;
    private DTPLogger log;

    public DTPEntityListener(DeathTpPlus instance) {
        plugin = instance;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();
        log.debug("EntityListener active");

    }

    String getEvent(String deathType){
        int messageindex = 0;
        if (config.getDeathevents().get(deathType).size() > 1)
        {
            Random rand = new Random();
            messageindex = rand.nextInt(config.getDeathevents().get(deathType).size());
        }
        return config.getDeathevents().get(deathType).get(messageindex);
    }



    public void onEntityDeath(EntityDeathEvent event) {

        beforedamage = "";

        if (event.getEntity() instanceof Player) {
            log.debug("onEntityDeath executing");
            Player player = (Player) event.getEntity();
            /*EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
            damageEvent.getType();*/
            String damagetype = lastDamageType.get(lastDamagePlayer.indexOf(player.getName()));
            String eventAnnounce = "";
            String fileOutput = "";
            String line = "";
            String[] howtheydied;
            if (config.isAllowDeathtp()) {
                ArrayList<String> filetext = new ArrayList<String>();
                boolean readCheck = false;
                boolean newPlayerDeath = true;
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

            if (config.isShowDeathNotify() || config.isShowStreaks() || config.isAllowDeathLog() || config.isEnableTombStone()|| config.isEnableTomb() ) {
                howtheydied = damagetype.split(":");
                loghowdied = howtheydied[0];
                // Todo change into case statement and create methods for eventAnnounce

                if (getEvent(howtheydied[0])==null){
                    howtheydied[0] = "UNKNOWN";
                }

                eventAnnounce = getEvent(howtheydied[0]).replace("%n", player.getDisplayName());

                if (howtheydied[0].matches("PVP")) {
                    if (howtheydied[2].equals("bare hands")) {
                        eventAnnounce = getEvent("FISTS").replace("%n", player.getDisplayName());
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
                    eventAnnounce = getEvent("UNKNOWN").replace("%n", player.getDisplayName());
                }

                eventAnnounce = plugin.convertSamloean(eventAnnounce);

                if (config.isShowDeathNotify()) {
                    //plugin.getServer().broadcastMessage(eventAnnounce);
                    if (event instanceof PlayerDeathEvent) {
                        playerDeathEvent = (PlayerDeathEvent) event;
                        playerDeathEvent.setDeathMessage(eventAnnounce);
                    }

                }

                //CraftIRC
                if (plugin.craftircHandle != null) {
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

                if (config.isAllowDeathLog()) {
                    writeToLog("death", player.getName(), loghowdied);
                }

                if (config.isShowDeathSign()) {
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
                // ToDo DTPTomb Integration
                if (config.isEnableTomb())
                {
                    if (worker.hasTomb(player.getName())) {
                        DTPTomb DTPTomb = worker.getTomb(player.getName());
                        String signtext;

                        if (howtheydied[0].equals("PVP"))
                            signtext = DTPLocaleWorker.getInstance().getPvpLocale(howtheydied[2]);
                        else
                            signtext = DTPLocaleWorker.getInstance().getLocale(
                                    howtheydied[0].toLowerCase());
                        int deathLimit = config.getMaxDeaths();
                        DTPTomb.addDeath();
                        if (deathLimit != 0 && (DTPTomb.getDeaths() % deathLimit) == 0) {
                            DTPTomb.resetTombBlocks();
                            player.sendMessage(worker.graveDigger
                                    + "You've reached the number of deaths before DTPTomb reset.("
                                    + ChatColor.DARK_RED + deathLimit + ChatColor.WHITE
                                    + ") All your tombs are now destroyed.");
                        } else {
                            DTPTomb.setReason(signtext);
                            DTPTomb.setDeathLoc(player.getLocation());
                            DTPTomb.updateDeath();
                        }
                    }
                }



                // Tombstone part
                if (config.isEnableTombStone()){

                    if (!plugin.hasPerm(player, "tombstone.use", false))
                        return;

                    log.debug(player.getName() + " died.");

                    if (event.getDrops().size() == 0) {
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
                    for (ItemStack item : event.getDrops()) {
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
                    if (event.getDrops().size() > maxSlot) {
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
                    DTPTombBlock tBlock = new DTPTombBlock(sChest.getBlock(),
                            (lChest != null) ? lChest.getBlock() : null, sBlock,
                            player.getName(), (System.currentTimeMillis() / 1000));

// Protect the chest/sign if LWC is installed.
                    Boolean prot = false;
                    Boolean protLWC = false;
                    if (plugin.hasPerm(player, "tombstone.lwc", false))
                        prot = plugin.activateLWC(player, tBlock);
                    tBlock.setLwcEnabled(prot);
                    if (prot)
                        protLWC = true;

// Protect the chest with Lockette if installed, enabled, and
// unprotected.
                    if (plugin.hasPerm(player, "tombstone.lockette", false)){
                        prot = plugin.protectWithLockette(player, tBlock);
                    }
// Add tombstone to list
                    plugin.tombList.offer(tBlock);

// Add tombstone blocks to tombBlockList
                    plugin.tombBlockList.put(tBlock.getBlock().getLocation(), tBlock);
                    if (tBlock.getLBlock() != null)
                        plugin.tombBlockList.put(tBlock.getLBlock().getLocation(), tBlock);
                    if (tBlock.getSign() != null)
                        plugin.tombBlockList.put(tBlock.getSign().getLocation(), tBlock);

// Add tombstone to player lookup list
                    ArrayList<DTPTombBlock> pList = plugin.playerTombList.get(player.getName());
                    if (pList == null) {
                        pList = new ArrayList<DTPTombBlock>();
                        plugin.playerTombList.put(player.getName(), pList);
                    }
                    pList.add(tBlock);

                    plugin.saveTombStoneList(player.getWorld().getName());

// Next get the players inventory using the getDrops() method.
                    for (Iterator<ItemStack> iter = event.getDrops().listIterator(); iter
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
                    if (event.getDrops().size() > 0)
                        msg += event.getDrops().size() + " items wouldn't fit in chest.";
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

                    //added compatibility for streaks if notify is off
                    else {
                        howtheydied = damagetype.split(":");
                        if (howtheydied[0].matches("PVP")) {
                            if (config.isShowStreaks())
                                writeToStreak(player.getName(), howtheydied[2]);
                        }

                        if (config.isAllowDeathLog()) {
                            writeToLog("death", player.getName(), loghowdied);
                        }
                    }



                }
            }
        }



    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()){
            return;
        }
        if(event.getEntity() instanceof Player) {
            log.debug("onEntityDamage executing");
            Player player = (Player) event.getEntity();
            //player.sendMessage(event.getType().toString());
            lastDamageDone(player, event);
        }
    }

    void lastDamageDone(Player player, EntityDamageEvent event) {
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
                else if (mob instanceof CaveSpider) {
                    lastdamage = "CAVESPIDER";
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
                else if (mob instanceof Enderman) {
                    lastdamage = "ENDERMAN";
                }
                else if (mob instanceof Silverfish) {
                    lastdamage = "SILVERFISH";
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

    public void onEntityExplode(EntityExplodeEvent event) {
        log.debug("onEntityExplode executing");
        if (event.isCancelled())
            return;
        if (!config.isCreeperProtection())
            return;
        for (Block block : event.blockList()) {
            DTPTombBlock tBlock = plugin.tombBlockList.get(block.getLocation());
            if (tBlock != null) {
                event.setCancelled(true);
            }
        }
    }

    private void createSign(Block signBlock, Player p) {
        String date = new SimpleDateFormat(config.getDateFormat())
                .format(new Date());
        String time = new SimpleDateFormat(config.getTimeFormat())
                .format(new Date());
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

        plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
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
