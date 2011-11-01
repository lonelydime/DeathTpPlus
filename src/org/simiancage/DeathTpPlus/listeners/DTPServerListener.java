package org.simiancage.DeathTpPlus.listeners;

//import org.bukkit.event.Listener;
import com.griefcraft.lwc.LWCPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.nijikokun.register.payment.Methods;
import com.nijikokun.register.payment.Method;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.yi.acru.bukkit.Lockette.Lockette;

public class DTPServerListener extends ServerListener {
    private static DeathTpPlus plugin;
    public static Method economy = null;


    public DTPServerListener(DeathTpPlus plugin) {
        this.plugin = plugin;

    }

    public static void setEconomy(Method economy) {
        DTPServerListener.economy = economy;
    }

    public static Method getEconomy() {
        return economy;
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkRegister = pm.getPlugin("Register");
        Plugin checkVault = pm.getPlugin("Vault");
        if ((checkRegister == null) && plugin.useRegister) {
            Methods.setMethod(pm);
            if (Methods.getMethod() == null)
            {
                plugin.useRegister = false;
                plugin.economyActive = false;
                plugin.log.info(plugin.logName +"un-hooked from Register.");
                plugin.log.info(plugin.logName +"as Register was unloaded / disabled.");
            }
        }
        if ((checkVault == null) && plugin.useVault) {
            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider == null)
            {
                plugin.useVault = false;
                plugin.economyActive = false;
                plugin.log.info(plugin.logName +"un-hooked from Vault.");
                plugin.log.info(plugin.logName +"as Vault was unloaded / disabled.");
            }
        }

        if (event.getPlugin() == plugin.lwcPlugin) {
            plugin.log.info(plugin.logName +"LWC plugin lost.");
            plugin.lwcPlugin = null;
        }

        if (event.getPlugin() == plugin.LockettePlugin) {
            plugin.log.info(plugin.logName + "Lockette plugin lost.");
            plugin.LockettePlugin = null;
        }

    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {

        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkRegister = pm.getPlugin("Register");
        Plugin checkVault = pm.getPlugin("Vault");
        if ((checkRegister != null) && plugin.economyProvider().equalsIgnoreCase("register")) {
            Methods.setMethod(pm);
            if (Methods.getMethod() != null)
            {
                setEconomy(Methods.getMethod());
                plugin.log.info(plugin.logName +"Economy method found: "+ getEconomy().getName()+ " v "+ getEconomy().getVersion());
                plugin.log.info(plugin.logName + "configured to use "+ plugin.economyProvider());
                plugin.useRegister = true;
                plugin.economyActive = true;

            } else {
                plugin.log.warning(plugin.warnLogName +"Register detected but no economy plugin found!");
                plugin.log.info(plugin.logName + "configured to use "+ plugin.economyProvider());
                plugin.useRegister = false;
                plugin.economyActive = false;
            }
        }
        
        if ((checkVault != null) && plugin.economyProvider().equalsIgnoreCase("vault")) {
            plugin.log.info(plugin.logName + "Vault detected");
            plugin.log.info(plugin.logName + "configured to use "+ plugin.economyProvider());
            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                plugin.economy = economyProvider.getProvider();
                plugin.useVault = true;
                plugin.economyActive = true;
                plugin.log.info(plugin.logName + "Economy provider found: "+plugin.economy.toString());

            } else {
                plugin.useVault = false;
                plugin.economyActive = false;
                plugin.log.warning(plugin.warnLogName + "No economy provider found.");
            }
        } else {
            plugin.log.info(plugin.logName + "Vault not detected, will attach later.");

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