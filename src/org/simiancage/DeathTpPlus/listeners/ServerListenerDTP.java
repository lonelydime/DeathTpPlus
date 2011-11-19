package org.simiancage.DeathTpPlus.listeners;

//import org.bukkit.event.Listener;
import com.griefcraft.lwc.LWCPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.helpers.ConfigDTP;
import org.simiancage.DeathTpPlus.helpers.LoggerDTP;
import org.yi.acru.bukkit.Lockette.Lockette;

public class ServerListenerDTP extends ServerListener {
    private static DeathTpPlus plugin;

    private LoggerDTP log;
    private ConfigDTP config;
    private boolean missingEconomyWarn = true;


    public ServerListenerDTP(DeathTpPlus plugin) {
        this.plugin = plugin;
        log = LoggerDTP.getLogger();
        config = ConfigDTP.getInstance();
        log.debug("ServerListener active");

    }


    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        log.debug("onPluginDisable executing");
        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkRegister = pm.getPlugin("Register");
        Plugin checkVault = pm.getPlugin("Vault");
        if ((checkVault == null) && plugin.useVault) {
            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider == null)
            {
                plugin.useVault = false;
                plugin.economyActive = false;
                log.info("un-hooked from Vault.");
                log.info("as Vault was unloaded / disabled.");
            }
        }

        if (event.getPlugin() == plugin.lwcPlugin) {
            log.info("LWC plugin lost.");
            plugin.lwcPlugin = null;
        }

        if (event.getPlugin() == plugin.LockettePlugin) {
            log.info( "Lockette plugin lost.");
            plugin.LockettePlugin = null;
        }

    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        log.debug("onPluginEnable executing");
        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkVault = pm.getPlugin("Vault");
        if (checkVault !=null && !plugin.useVault)
        {
            plugin.useVault = true;
            log.info( "Vault detected");
            log.info("Checking ecnomony providers now!");
        }


        if ((!plugin.economyActive) && plugin.useVault) {

            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                plugin.economy = economyProvider.getProvider();
                plugin.economyActive = true;
                log.info( "Economy provider found: "+plugin.economy.getName());

            } else {
                if (missingEconomyWarn){
                    log.warning("No economy provider found.");
                    log.info("Still waiting for economy provider to show up.");
                    missingEconomyWarn = false;
                }
            }
        }

        if (plugin.lwcPlugin == null) {
            if (event.getPlugin().getDescription().getName()
                    .equalsIgnoreCase("LWC")) {
                plugin.lwcPlugin = (LWCPlugin) plugin.checkPlugin(event
                        .getPlugin());
            }
        }

        if (plugin.LockettePlugin == null) {
            if (event.getPlugin().getDescription().getName()
                    .equalsIgnoreCase("Lockette")) {
                plugin.LockettePlugin = (Lockette) plugin.checkPlugin(event
                        .getPlugin());
            }
        }
    }
}
