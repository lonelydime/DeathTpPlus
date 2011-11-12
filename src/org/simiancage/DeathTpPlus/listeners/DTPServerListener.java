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
import org.simiancage.DeathTpPlus.workers.DTPConfig;
import org.simiancage.DeathTpPlus.workers.DTPLogger;
import org.yi.acru.bukkit.Lockette.Lockette;

public class DTPServerListener extends ServerListener {
    private static DeathTpPlus plugin;
    private static Method economy = null;
    private DTPLogger log;
    private DTPConfig config;


    public DTPServerListener(DeathTpPlus plugin) {
        this.plugin = plugin;
        log = DTPLogger.getLogger();
        config = DTPConfig.getInstance();

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
                log.info("un-hooked from Register.");
                log.info("as Register was unloaded / disabled.");
            }
        }
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

        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkVault = pm.getPlugin("Vault");

        if ((checkVault != null) && !plugin.useVault) {
            log.info( "Vault detected");
            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                plugin.economy = economyProvider.getProvider();
                plugin.useVault = true;
                plugin.economyActive = true;
                log.info( "Economy provider found: "+plugin.economy.getName());

            } else {
                plugin.useVault = false;
                plugin.economyActive = false;
                log.warning(plugin.warnLogName + "No economy provider found.");
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
