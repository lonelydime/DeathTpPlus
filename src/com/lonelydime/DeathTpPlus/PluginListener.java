package com.lonelydime.DeathTpPlus;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;

import com.nijiko.coelho.iConomy.iConomy;
import org.bukkit.plugin.Plugin;

/**
 * Checks for plugins whenever one is enabled
 */
public class PluginListener extends ServerListener {
    public PluginListener() { }

    public void onPluginEnabled(PluginEvent event) {
        if(DeathTpPlus.getiConomy() == null) {
            Plugin iConomy = DeathTpPlus.getBukkitServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if(iConomy.isEnabled()) {
                	DeathTpPlus.setiConomy((iConomy)iConomy);
                    System.out.println("[(Plugin)] Successfully linked with iConomy.");
                }
            }
        }
    }
}