package com.lonelydime.DeathTpPlus;

//import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import com.nijikokun.register.Register;
import com.nijikokun.register.payment.Methods;

public class server extends ServerListener {
    private DeathTpPlus plugin;
    private Methods Methods = null;

    public server(DeathTpPlus plugin) {
        this.plugin = plugin;

    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
//        if (DeathTpPlus.Register != null) {
//            if (event.getPlugin().getDescription().getName().equals("Register")) {
//            	DeathTpPlus.Register = null;
//                System.out.println("[DeathTpPlus] un-hooked from Register.");
//            }
//        }
        // Check to see if the plugin thats being disabled is the one we are using
        if (this.Methods != null && this.Methods.hasMethod()) {
            Boolean check = this.Methods.checkDisabled(event.getPlugin());

            if(check) {
                Methods.reset();
                // Todo implement Logger
                plugin.useRegister = false;
                System.out.println("[DeathTpPlus] un-hooked from Register.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
//        if (DeathTpPlus.Register == null) {
//            Plugin Register = plugin.getServer().getPluginManager().getPlugin("Register");
//
//            if (Register != null) {
//                if (Register.isEnabled()) {
//                	DeathTpPlus.Register = (Register)Register;
//                    System.out.println("[DeathTpPlus] hooked into Register.");
//                }
//            }
//        }

        // Check to see if we need a payment method
        Plugin checkRegister = plugin.getServer().getPluginManager().getPlugin("Register");
        if (checkRegister != null) {
            this.Methods = new Methods();
            if (!this.Methods.hasMethod()) {

                if(this.Methods.setMethod(plugin.getServer().getPluginManager())) {
                    if(this.Methods.hasMethod()){
                        plugin.useRegister = true;
                        System.out.println("[DeathTpPlus] Payment method found (" + this.Methods.getMethod().getName() + " version: " + this.Methods.getMethod().getVersion() + ")");
                    }
                }
            }
        }
    }
}