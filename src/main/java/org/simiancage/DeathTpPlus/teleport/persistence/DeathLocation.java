package org.simiancage.DeathTpPlus.teleport.persistence;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * PluginName: DeathTpPlus
 * Class: DeathLocation
 * User: DonRedhorse
 * Date: 25.11.11
 * Time: 19:39
 */

public class DeathLocation {
    private String playerName;
    private Location location;
    private String worldName;

    public DeathLocation() {
    }

    public DeathLocation(Player player) {
        this.playerName = player.getName();
        this.location = player.getLocation();
        this.worldName = player.getWorld().getName();
    }

    public DeathLocation(String record) {
        if (record != null) {
            String[] parts = record.split(":");

            if (parts.length == 5) {
                playerName = parts[0];
                location = new Location(null, Double.valueOf(parts[1]), Double.valueOf(parts[2]), Double.valueOf(parts[3]));
                worldName = parts[4];
            }
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public String toString() {
        // Fix for issues with , instead of .
        String x = Double.toString(location.getX()).replace(",", ".");
        String y = Double.toString(location.getY()).replace(",", ".");
        String z = Double.toString(location.getZ()).replace(",", ".");
        return String.format("%s:%s:%s:%sf:%s", playerName, x, y, z, worldName);
    }
}
