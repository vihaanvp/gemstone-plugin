package me.vihaanvp.gemstoneplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DeathDebuffListener implements Listener {
    private final Set<UUID> debuffedPlayers = new HashSet<>();
    private final Plugin plugin;
    private static final double DEBUFFED_HEALTH = 12.0; // 6 hearts
    private static final int DEBUFF_SECONDS = 60; // Total debuff duration in seconds
    private static final int PVP_BLOCK_SECONDS = 30; // PvP block duration in seconds

    public DeathDebuffListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // Allow the main class and commands to access the debuffedPlayers set
    public Set<UUID> getDebuffedPlayers() {
        return debuffedPlayers;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        debuffedPlayers.add(player.getUniqueId());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (debuffedPlayers.contains(uuid)) {
            // Use a slight delay to ensure the player is fully loaded
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Set max health to 6 hearts
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(DEBUFFED_HEALTH);
                if (player.getHealth() > DEBUFFED_HEALTH) {
                    player.setHealth(DEBUFFED_HEALTH);
                }
                player.sendMessage(ChatColor.RED + "You are weakened (6 hearts) and PvP-immune for 30 seconds!");

                // Restore max health after DEBUFF_SECONDS
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                    if (player.getHealth() > 20.0) player.setHealth(20.0);
                    player.sendMessage(ChatColor.GREEN + "Your strength is back!");
                    debuffedPlayers.remove(uuid);
                }, 20L * DEBUFF_SECONDS);

                // Remove PvP immunity message after PVP_BLOCK_SECONDS
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.YELLOW + "You are now vulnerable to PvP again.");
                }, 20L * PVP_BLOCK_SECONDS);

            }, 2L); // Delay just after respawn
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player attacker)) {
            return;
        }

        boolean victimDebuffed = debuffedPlayers.contains(victim.getUniqueId());
        boolean attackerDebuffed = debuffedPlayers.contains(attacker.getUniqueId());

        if (victimDebuffed) {
            // Block incoming PvP
            event.setCancelled(true);
            attacker.sendMessage(ChatColor.RED + "That player has the death debuff!");
        } else if (attackerDebuffed) {
            // Block outgoing PvP
            event.setCancelled(true);
            attacker.sendMessage(ChatColor.RED + "You have the death debuff and cannot attack players!");
        }
    }
}