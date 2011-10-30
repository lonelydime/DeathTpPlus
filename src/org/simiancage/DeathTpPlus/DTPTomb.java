package org.simiancage.DeathTpPlus;

/**
 * PluginName: DeathTpPlus
 * Class: DTPTomb
 * User: DonRedhorse
 * Date: 18.10.11
 * Time: 22:33
 * based on:
 * an updated fork of Furt https://github.com/Furt of
 * Cenopath - A Dead Man's Chest plugin for Bukkit
 * By Jim Drey (Southpaw018) <moof@moofit.com>
 * Original Copyright (C) 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */
/*

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import org.simiancage.DeathTpPlus.listeners.DTPBlockListener;
import org.simiancage.DeathTpPlus.listeners.DTPEntityListener;
import org.simiancage.DeathTpPlus.listeners.DTPPlayerListener;
import org.simiancage.DeathTpPlus.listeners.DTPServerListener;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.model.ProtectionTypes;
import org.yi.acru.bukkit.Lockette.Lockette;

public class DTPTomb {


    */
/**
     * Configuration options - Defaults
     *//*

 

    public void onEnable() {
        //loadConfig();

        */
/*
        if (versionCheck) {
            versionCheck(true);
        }
        *//*

// Start removal timer. Run every 5 seconds (20 ticks per second)


    }

*/
/*    public void loadConfig() {
        config.load();

        *//*
*/
/*configVer = config.getInt("configVer", configVer);
        if (configVer == 0) {
            try {
                log.info("[DTPTomb] Configuration error or no config file found. Downloading default config file...");
                if (!new File(getDataFolder().toString()).exists()) {
                    new File(getDataFolder().toString()).mkdir();
                }
                URL config = new URL(
                        "https://raw.github.com/Southpaw018/DTPTomb/master/config.yml");
                ReadableByteChannel rbc = Channels.newChannel(config
                        .openStream());
                FileOutputStream fos = new FileOutputStream(this
                        .getDataFolder().getPath() + "/config.yml");
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            } catch (MalformedURLException ex) {
                log.warning("[DTPTomb] Error accessing default config file URL: "
                        + ex);
            } catch (FileNotFoundException ex) {
                log.warning("[DTPTomb] Error accessing default config file URL: "
                        + ex);
            } catch (IOException ex) {
                log.warning("[DTPTomb] Error downloading default config file: "
                        + ex);
            }

        } else if (configVer < configCurrent) {
            log.warning("[DTPTomb] Your config file is out of date! Delete your config and reload to see the new options. Proceeding using set options from config file and defaults for new options...");
        }*//*
*/
/*

// Core
        logEvents = config.getBoolean("Core.logEvents", logEvents);
        cenotaphSign = config.getBoolean("Core.cenotaphSign", cenotaphSign);
        noDestroy = config.getBoolean("Core.noDestroy", noDestroy);
        pMessage = config.getBoolean("Core.playerMessage", pMessage);
        saveCenotaphList = config.getBoolean("Core.saveCenotaphList",
                saveCenotaphList);
        noInterfere = config.getBoolean("Core.noInterfere", noInterfere);
        versionCheck = config.getBoolean("Core.versionCheck", versionCheck);
        voidCheck = config.getBoolean("Core.voidCheck", voidCheck);
        creeperProtection = config.getBoolean("Core.creeperProtection",
                creeperProtection);
        signMessage = loadSign();
        dateFormat = config.getString("Core.Sign.dateFormat", dateFormat);
        timeFormat = config.getString("Core.Sign.timeFormat", timeFormat);

// Removal
        destroyQuickLoot = config.getBoolean("Removal.destroyQuickLoot",
                destroyQuickLoot);
        cenotaphRemove = config.getBoolean("Removal.cenotaphRemove",
                cenotaphRemove);
        removeTime = config.getInt("Removal.removeTime", removeTime);
        removeWhenEmpty = config.getBoolean("Removal.removeWhenEmpty",
                removeWhenEmpty);
        keepUntilEmpty = config.getBoolean("Removal.keepUntilEmpty",
                keepUntilEmpty);

// Security
        LocketteEnable = config.getBoolean("Security.LocketteEnable",
                LocketteEnable);
        lwcEnable = config.getBoolean("Security.lwcEnable", lwcEnable);
        securityRemove = config.getBoolean("Security.securityRemove",
                securityRemove);
        securityTimeout = config.getInt("Security.securityTimeout",
                securityTimeout);
        lwcPublic = config.getBoolean("Security.lwcPublic", lwcPublic);

// DeathMessages
        try {
            deathMessages = (TreeMap<String, Object>) config.getNode(
                    "DeathMessages").getAll();
        } catch (NullPointerException e) {
            log.warning("[DTPTomb] Configuration failure while loading deathMessages. Using defaults.");
        }
    }*//*










    public void onDisable() {

    }







    public String versionCheck(Boolean printToLog) {
        String thisVersion = getDescription().getVersion();
        URL url = null;
        try {
            url = new URL("http://www.moofit.com/minecraft/cenotaph.ver?v="
                    + thisVersion);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String newVersion = "";
            String line;
            while ((line = in.readLine()) != null) {
                newVersion += line;
            }
            in.close();
            if (!newVersion.equals(thisVersion)) {
                if (printToLog)
                    log.warning("[DTPTomb] DTPTomb is out of date! This version: "
                            + thisVersion
                            + "; latest version: "
                            + newVersion
                            + ".");
                return "DTPTomb is out of date! This version: " + thisVersion
                        + "; latest version: " + newVersion + ".";
            } else {
                if (printToLog)
                    log.info("[DTPTomb] DTPTomb is up to date at version "
                            + thisVersion + ".");
                return "DTPTomb is up to date at version " + thisVersion + ".";
            }
        } catch (MalformedURLException ex) {
            if (printToLog)
                log.warning("[DTPTomb] Error accessing update URL.");
            return "Error accessing update URL.";
        } catch (IOException ex) {
            if (printToLog)
                log.warning("[DTPTomb] Error checking for update.");
            return "Error checking for update.";
        }
    }

}
*/
