package com.lonelydime.DeathTpPlus;

//import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.iConomy.*;

public class server extends ServerListener {
    private DeathTpPlus plugin;

    public server(DeathTpPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (DeathTpPlus.iConomy != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
            	DeathTpPlus.iConomy = null;
                System.out.println("[DeathTpPlus] un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (DeathTpPlus.iConomy == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled()) {
                	DeathTpPlus.iConomy = (iConomy)iConomy;
                    System.out.println("[DeathTpPlus] hooked into iConomy.");
                }
            }
        }
    }
}