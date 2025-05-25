package me.vihaanvp.gemstoneplugin.gemstones;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Voltaryn implements Listener {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_SECONDS = 60;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Voltaryn");
            meta.setLore(List.of(
                    ChatColor.GRAY + "Passive Speed I",
                    ChatColor.GRAY + "Right-click a player or mob to strike lightning (2 min cooldown)"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isVoltaryn(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Voltaryn");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        if (isVoltaryn(held)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true, false, false));
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isVoltaryn(held)) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!player.hasPermission("gemstone.voltaryn.bypass")) {
            if (cooldowns.containsKey(uuid)) {
                long lastUsed = cooldowns.get(uuid);
                long elapsed = (now - lastUsed) / 1000;
                if (elapsed < COOLDOWN_SECONDS) {
                    long remaining = COOLDOWN_SECONDS - elapsed;
                    player.sendMessage(ChatColor.RED + "Wait " + remaining + " seconds before using Voltaryn again.");
                    return;
                }
            }
            cooldowns.put(uuid, now);
        }

        target.getWorld().strikeLightning(target.getLocation());
        player.sendMessage(ChatColor.YELLOW + "You struck " + target.getName() + " with lightning!");

        if (target instanceof Player p) {
            p.sendMessage(ChatColor.RED + "You were struck by lightning!");
        } else if (target instanceof LivingEntity) {
            // Optional: could add effects/messages to mobs if needed
        }
    }

    @EventHandler
    public void onLightningDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING &&
                event.getEntity() instanceof Player player) {

            ItemStack held = player.getInventory().getItemInMainHand();
            if (isVoltaryn(held)) {
                event.setCancelled(true); // Immune only if *holder* gets hit
            }
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (isVoltaryn(event.getItem())) {
            event.setCancelled(true); // Prevent durability loss
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity) {
            ItemStack held = player.getInventory().getItemInMainHand();
            if (isVoltaryn(held)) {
                event.setDamage(0); // Set damage to 0 when Voltaryn is used
            }
        }
    }
}