package me.vihaanvp.gemstoneplugin.gemstones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Noctyra implements Listener {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private static final int COOLDOWN_SECONDS = 600;
    private static final int DURATION_SECONDS = 180;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Noctyra");
            meta.setLore(List.of(
                    ChatColor.GRAY + "Right-click to become invisible for 3 minutes",
                    ChatColor.GRAY + "Cooldown: 10 minutes",
                    ChatColor.BLACK + " ",
                    ChatColor.DARK_PURPLE + "Bound Gemstone"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isNoctyra(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Noctyra");
    }

    private boolean hasNoctyraInInventory(Player player) {
        PlayerInventory inv = player.getInventory();
        // Check if Noctyra is in the player's inventory (main, offhand, or armor slots)
        for (ItemStack item : inv.getContents()) {
            if (isNoctyra(item)) {
                return true;
            }
        }
        for (ItemStack item : inv.getArmorContents()) {
            if (isNoctyra(item)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!hasNoctyraInInventory(player)) return;

        // Apply Night Vision if the player has Noctyra in their inventory
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 1, false, false));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;
        if (!isNoctyra(held)) return;
        event.setCancelled(true); // Prevent throwing Eye of Ender

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!player.hasPermission("gemstone.noctyra.bypass")) {
            if (cooldowns.containsKey(uuid)) {
                long lastUsed = cooldowns.get(uuid);
                long elapsed = (now - lastUsed) / 1000;
                if (elapsed < COOLDOWN_SECONDS) {
                    long remaining = COOLDOWN_SECONDS - elapsed;
                    player.sendMessage(ChatColor.RED + "Wait " + remaining + " seconds before using Noctyra again.");
                    return;
                }
            }
            cooldowns.put(uuid, now);
        }

        // Apply invisibility with no particles
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * DURATION_SECONDS, 0, true, false, false));
        player.sendMessage(ChatColor.DARK_PURPLE + "You vanished into the shadows... (but your armor hasn't)");
    }
}