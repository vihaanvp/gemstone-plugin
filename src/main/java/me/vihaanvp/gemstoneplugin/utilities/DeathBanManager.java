package me.vihaanvp.gemstoneplugin.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DeathBanManager implements Listener {

    private final Plugin plugin;
    private final File reviveFile;
    private YamlConfiguration reviveData;
    private static final int MAX_DEATHS = 10; // Players are banned after 5 deaths

    public DeathBanManager(Plugin plugin) {
        this.plugin = plugin;
        this.reviveFile = new File(plugin.getDataFolder(), "revives.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!reviveFile.exists()) {
            try {
                reviveFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create revives.yml!");
            }
        }
        this.reviveData = YamlConfiguration.loadConfiguration(reviveFile);
    }

    public void ensureReviveFileExists() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!reviveFile.exists()) {
            try {
                reviveFile.createNewFile();
                reviveData.save(reviveFile);
                plugin.getLogger().info("Revive file created");
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create revives.yml!");
            }
        }
    }

    public void saveReviveData() {
        try {
            reviveData.save(reviveFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save revives.yml!");
        }
    }

    public void reload() {
        this.reviveData = YamlConfiguration.loadConfiguration(reviveFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String uuid = player.getUniqueId().toString();

        if (player.hasPermission("gemstones.deathban.bypass")) return;

        // Increment death count
        int deaths = reviveData.getInt("deaths." + uuid, 0) + 1;
        reviveData.set("deaths." + uuid, deaths);
        saveReviveData();

        if (deaths >= MAX_DEATHS) {
            // Set ban flag and kick
            reviveData.set("banned." + uuid, true);
            saveReviveData();
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.kickPlayer(ChatColor.RED + "You are deathbanned! Wait for someone to revive you.");
                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " has been deathbanned!");
                plugin.getLogger().info(player.getName() + " was deathbanned.");
            });
        } else {
            player.sendMessage(ChatColor.RED + "You have died " + deaths + "/" + MAX_DEATHS + " times.");
            plugin.getLogger().info(player.getName() + " is " + (MAX_DEATHS - deaths) + " deaths away from getting death banned.");
            if (MAX_DEATHS - deaths == 1) {
                Bukkit.broadcastMessage(player.getName() + " is " + (MAX_DEATHS - deaths) + " death away from getting death banned.");
            } else {
                Bukkit.broadcastMessage(player.getName() + " is " + (MAX_DEATHS - deaths) + " deaths away from getting death banned.");
            }
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        String playerName = event.getName();
        boolean isBanned = reviveData.getBoolean("banned." + uuid, false);
        List<String> pending = reviveData.getStringList("pending");

        boolean found = false;
        Iterator<String> iter = pending.iterator();
        while (iter.hasNext()) {
            String pendingName = iter.next();
            if (pendingName.equalsIgnoreCase(playerName)) {
                iter.remove();
                found = true;
                break;
            }
        }

        if (isBanned && found) {
            // Unban and allow join
            reviveData.set("banned." + uuid, false);
            reviveData.set("deaths." + uuid, 0);
            reviveData.set("pending", pending);
            saveReviveData();
        } else if (isBanned) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    ChatColor.RED + "You are deathbanned! Wait for someone to revive you.");
        }
    }

    public boolean removeDeathBan(UUID uuid) {
        String uid = uuid.toString();
        if (reviveData.getBoolean("banned." + uid, false)) {
            reviveData.set("banned." + uid, false);
            reviveData.set("deaths." + uid, 0);
            saveReviveData();
            return true;
        }
        return false;
    }

    public boolean applyDeathBan(UUID uuid) {
        String uid = uuid.toString();
        if (reviveData.getBoolean("banned." + uid, false)) {
            return true; // already banned
        }
        reviveData.set("banned." + uid, true);
        reviveData.set("deaths." + uid, MAX_DEATHS);
        saveReviveData();
        return false;
    }

    public void addPendingRevive(String playerName) {
        List<String> pending = reviveData.getStringList("pending");
        pending.add(playerName);
        reviveData.set("pending", pending);
        saveReviveData();
    }

    public boolean isDeathbanned(UUID uuid) {
        return reviveData.getBoolean("banned." + uuid.toString(), false);
    }
}