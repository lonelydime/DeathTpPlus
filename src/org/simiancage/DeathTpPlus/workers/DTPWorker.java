package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: DeathTpPlus
 * Class: DTPWorker
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:43
 */

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;




public abstract class DTPWorker {
    private static DTPLogger log;
    private static DTPConfig config;
    protected HashMap<String, HashMap<String, Boolean>> permissions = new HashMap<String, HashMap<String, Boolean>>();
    protected static boolean disable = false;

    /**
     * Check the permissions
     *
     * @param player
     * @param perm
     * @return boolean
     */
    public boolean hasPerm(Player player, String perm) {
        return hasPerm(player, perm, true);
    }
    /**
     * @param disable the disable to set
     */
    public static void setDisable(boolean disable) {
        DTPWorker.disable = disable;
    }
    /**
     * @return the disable
     */
    public static boolean isDisable() {
        return disable;
    }

    /**
     * Check the permission with the possibility to disable the error msg
     *
     * @param player
     * @param perm
     * @param errorMsg
     * @return
     */
    public boolean hasPerm(Player player, String perm, boolean errorMsg) {
        if (permission == null) {
            if (perm.contains("admin") || perm.contains("free"))
                return player.isOp();
            return true;
        }
        String playerName = player.getName();
        if (permissions.containsKey(playerName)) {
            if (permissions.get(playerName).containsKey(perm))
                if (permissions.get(playerName).get(perm))
                    return true;
                else {
                    if (errorMsg)
                        player.sendMessage(ChatColor.RED
                                + "You don't have the Permissions to do that " + ChatColor.BLUE
                                + "(" + perm + ")");
                    return false;
                }

            if (permission.has(player, perm)) {
                permissions.get(playerName).put(perm, true);
                return true;
            } else {
                permissions.get(playerName).put(perm, false);
                if (errorMsg)
                    player.sendMessage(ChatColor.RED + "You don't have the Permissions to do that "
                            + ChatColor.BLUE + "(" + perm + ")");
            }
        } else {
            permissions.put(playerName, new HashMap<String, Boolean>());
            if (permission.has(player, perm)) {
                permissions.get(playerName).put(perm, true);
                return true;
            } else {
                permissions.get(playerName).put(perm, false);
                if (errorMsg)
                    player.sendMessage(ChatColor.RED + "You don't have the Permissions to do that "
                            + ChatColor.BLUE + "(" + perm + ")");
            }

        }

        return false;

    }

    /**
     * iConomy plugin
     *
     * @return
     */
    public static Method getPayement() {
        return method;
    }

    /**
     * Set iConomy Plugin
     *
     * @param plugin
     * @return
     */
    public static boolean setMethod(Method plugin) {
        if (method == null) {
            method = plugin;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Permission plugin
     *
     * @return
     */
    public static PermissionHandler getPermission() {
        return permission;
    }

    /**
     * Set iConomy Plugin
     *
     * @param plugin
     * @return
     */
    public static boolean setPermission(PermissionHandler plugin) {
        if (permission == null) {
            permission = plugin;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Remove all permissions node for the player from the cache.
     *
     * @param player
     */
    public void removePermissionNode(String player) {
        permissions.remove(player);
    }
}
