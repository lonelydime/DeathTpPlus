package org.simiancage.DeathTpPlus.listeners;

//import org.bukkit.event.Listener;
import com.griefcraft.lwc.LWCPlugin;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.nijikokun.register.payment.Methods;
import com.nijikokun.register.payment.Method;
import org.bukkit.plugin.PluginManager;
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
        if ((checkRegister == null) && plugin.useRegister) {
            Methods.setMethod(pm);
            if (Methods.getMethod() == null)
            {
                plugin.useRegister = false;
                plugin.log.info(plugin.logName +"un-hooked from Register.");
                plugin.log.info(plugin.logName +"as Register was unloaded / disabled.");
            }
        }
        if (event.getPlugin() == plugin.lwcPlugin) {
            plugin.log.info("[DTPTomb] LWC plugin lost.");
            plugin.lwcPlugin = null;
        }

        if (event.getPlugin() == plugin.LockettePlugin) {
            plugin.log.info("[DTPTomb] Lockette plugin lost.");
            plugin.LockettePlugin = null;
        }

    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {

        PluginManager pm = plugin.getServer().getPluginManager();
        Plugin checkRegister = pm.getPlugin("Register");
        if (checkRegister != null) {
            Methods.setMethod(pm);
            if (Methods.getMethod() != null)
            {
                setEconomy(Methods.getMethod());
                plugin.log.info(plugin.logName +"Economy method found: "+ getEconomy().getName()+ " v "+ getEconomy().getVersion());
                plugin.useRegister = true;

            } else {
                plugin.log.warning(plugin.logName +"Register detected but no economy plugin found!");
                plugin.useRegister = false;
            }
        } else {
            plugin.log.info(plugin.logName + "Register not detected, will attach later.");
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