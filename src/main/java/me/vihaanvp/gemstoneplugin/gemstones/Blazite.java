package me.vihaanvp.gemstoneplugin.gemstones;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Blazite implements Listener {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_SECONDS = 60;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Blazite");
            meta.setLore(List.of(
                    ChatColor.GOLD + "Grants Fire Resistance when held",
                    ChatColor.GRAY + "Right-click an entity to set it on fire (60s cooldown)",
                    ChatColor.BLACK + " ",
                    ChatColor.DARK_PURPLE + "Bound Gemstone"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Fire Resistance passive
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        boolean hasBlazite = isBlazite(mainHand) || isBlazite(offHand);

        if (hasBlazite) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false, false));
        }
    }

    // Fire on right-click ability
    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();
        ItemStack held = player.getInventory().getItemInMainHand();

        if (!isBlazite(held)) return;
        if (!(clicked instanceof LivingEntity target)) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!player.hasPermission("gemstone.blazite.bypass")) {
            if (cooldowns.containsKey(uuid)) {
                long lastUsed = cooldowns.get(uuid);
                long elapsed = (now - lastUsed) / 1000;
                if (elapsed < COOLDOWN_SECONDS) {
                    long remaining = COOLDOWN_SECONDS - elapsed;
                    player.sendMessage(ChatColor.RED + "Wait " + remaining + " seconds before using Blazite again.");
                    return;
                }
            }
            cooldowns.put(uuid, now);
        }

        target.setFireTicks(100); // 5 seconds
        player.sendMessage(ChatColor.RED + "You set " + target.getName() + " on fire with Blazite!");
        if (target instanceof Player pTarget) {
            pTarget.sendMessage(ChatColor.RED + "You were set on fire by Blazite!");
        }
    }

    // Identifies item as Blazite
    private boolean isBlazite(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return item.getType() == Material.BLAZE_ROD &&
                meta.hasDisplayName() &&
                ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Blazite");
    }
}