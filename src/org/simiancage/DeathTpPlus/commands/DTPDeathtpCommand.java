package org.simiancage.DeathTpPlus.commands;

import com.nijikokun.register.payment.Method;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * PluginName: DeathTpPlus
 * Class: DTPDeathtpCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class DTPDeathtpCommand implements CommandExecutor {

    private DeathTpPlus plugin;

    public DTPDeathtpCommand(DeathTpPlus instance) {
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName();
        boolean canUseCommand = false;
        boolean teleportok = true;
        boolean teleported = false;


        if (sender instanceof Player) {
            Player player = (Player)sender;
            String thisWorld = player.getWorld().getName().toString();
            boolean worldTravel = false;
            if (player.hasPermission("deathtpplus.worldtravel") && plugin.deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions"))
            {
                worldTravel = true;
            }
            double economyCost = Double.valueOf(plugin.deathconfig.get("ECONOMY_COST").trim()).doubleValue();

            canUseCommand = (player.hasPermission("deathtpplus.deathtp") && plugin.deathconfig.get("ALLOW_DEATHTP").equalsIgnoreCase("true"));

            if (canUseCommand) {
                //costs item in inventory
                if (!plugin.deathconfig.get("CHARGE_ITEM_ID").equals("0") ) {
                    if (player.getItemInHand().getType().getId() != Integer.parseInt(plugin.deathconfig.get("CHARGE_ITEM_ID"))) {
                        player.sendMessage("You must be holding a "+ Material.getMaterial(Integer.parseInt(plugin.deathconfig.get("CHARGE_ITEM_ID"))).toString()+" to teleport.");
                        teleportok = false;
                    }
                    else {
                        ItemStack currentitem = player.getItemInHand();
                        int itemnum = currentitem.getAmount();
                        itemnum--;
                        if (itemnum > 0) {
                            currentitem.setAmount(itemnum);
                            player.setItemInHand(currentitem);
                        }
                        else {
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        }
                    }
                }


                //costs Economy
                if (economyCost > 0) {
                    if (plugin.useRegister) {
                        Method.MethodAccount account = plugin.getRegisterMethod().getAccount(player.getName());
                        if (account != null && account.hasEnough(economyCost)) {
                            account.subtract(economyCost);
                            player.sendMessage("You used "+economyCost+" to use /deathtp");
                        }
                        else {
                            player.sendMessage("You need "+economyCost+" coins to use /deathtp");
                            teleportok = false;
                        }
                    }
                    if (plugin.useVault){
                        if (plugin.economy.getBalance(player.getName())> economyCost) {
                            plugin.economy.withdrawPlayer(player.getName(), economyCost);
                            player.sendMessage("You used "+economyCost+" to use /deathtp");
                        } else {
                            player.sendMessage("You need "+economyCost+" coins to use /deathtp");
                            teleportok = false;
                        }
                    }


                }


                if (teleportok) {

                    try {
                        String line = "";
                        String teleloc = "";
                        String[] location;
                        FileReader fr = new FileReader(plugin.locsName);
                        BufferedReader br = new BufferedReader(fr);

                        while((line = br.readLine()) != null) {
                            if (line.contains(player.getName()+":")) {
                                teleloc = line;
                            }
                        }

                        if (teleloc != "") {
                            location = teleloc.split(":");
                            Location sendLocation = player.getLocation();
                            double x, y, z;

                            x=Double.valueOf(location[1].trim()).doubleValue();
                            y=Double.valueOf(location[2].trim()).doubleValue();
                            z=Double.valueOf(location[3].trim()).doubleValue();
                            World deathWorld = plugin.getServer().getWorld(location[4].trim());
                            sendLocation.setX(x);
                            sendLocation.setY(y);
                            sendLocation.setZ(z);

                            boolean safeTele = false;
                            int test1=-1, test2=-1;
                            while (!safeTele) {
                                test1 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                test2 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                if (test1 == 0 && test2 == 0) {
                                    safeTele = true;
                                }

                                sendLocation.setY(sendLocation.getY()+1);
                            }

                            if (!thisWorld.equals(deathWorld.getName()))
                            {
                                if (worldTravel)
                                {
                                    sendLocation.setWorld(deathWorld);
                                    player.teleport(sendLocation);
                                    teleported = true;
                                } else {
                                    player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                                }
                            }
                            else {
                                player.teleport(sendLocation);
                                teleported = true;
                            }
                        }
                        else {
                            player.sendMessage("You do not have a last known death location.");
                        }
                        if (plugin.useRegister && !teleported) {
                            Method.MethodAccount account = plugin.getRegisterMethod().getAccount(player.getName());
                            account.add(economyCost);
                            player.sendMessage("Giving you back "+economyCost);
                        }

                        if (plugin.useVault && !teleported) {
                            plugin.economy.depositPlayer(player.getName(), economyCost);
                            player.sendMessage("Giving you back "+economyCost);
                        }
                    }
                    catch (IOException e) {

                    }
                }
                else {
                    player.sendMessage("That command is not available");
                }

            }

            return true;
        }

        else {
            System.out.println("This is only a player command.");
            return true;
        }


    }
}