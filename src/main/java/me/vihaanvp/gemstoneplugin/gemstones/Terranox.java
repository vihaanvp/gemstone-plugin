package me.vihaanvp.gemstoneplugin.gemstones;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Terranox implements Listener {

    private static final Set<Material> NATURAL_BLOCKS = Set.of(
            Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.PODZOL,
            Material.SAND, Material.RED_SAND, Material.STONE, Material.GRAVEL,
            Material.MOSS_BLOCK, Material.MYCELIUM, Material.ROOTED_DIRT
    );

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 120 * 1000; // 120 seconds

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Terranox");
            meta.setLore(List.of(
                    ChatColor.DARK_GREEN + "Grants Strength when standing on natural blocks",
                    ChatColor.DARK_GREEN + "Right-click surface block to fully heal",
                    ChatColor.BLACK + " ",
                    ChatColor.DARK_PURPLE + "Bound Gemstone"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isTerranox(ItemStack item) {
        if (item == null || item.getType() != Material.EMERALD || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null
                && meta.hasDisplayName()
                && meta.getDisplayName().equals(ChatColor.GREEN + "Terranox")
                && meta.getLore() != null
                && meta.getLore().contains(ChatColor.DARK_GREEN + "Grants Strength when standing on natural blocks");
    }

    private boolean hasTerranox(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isTerranox(item)) return true;
        }
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!hasTerranox(player)) return;

        Block block = player.getLocation().clone().add(0, -1, 0).getBlock();
        if (NATURAL_BLOCKS.contains(block.getType())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 0, true, false, false));
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT_CLICK_BLOCK")) return;

        // Only run logic if player is HOLDING a Terranox
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!isTerranox(hand)) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getWorld() == null) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Cooldown check
        if (!player.hasPermission("gemstones.terranox.bypass")) {
            if (cooldowns.containsKey(playerId)) {
                long lastUsed = cooldowns.get(playerId);
                long elapsed = (currentTime - lastUsed);
                if (elapsed < COOLDOWN_TIME) {
                    long remaining = (COOLDOWN_TIME - elapsed) / 1000;
                    player.sendMessage(ChatColor.RED + "Please wait " + remaining + " seconds before using Terranox again.");
                    return;
                }
            }
            cooldowns.put(playerId, currentTime);
        }

        // Only allow healing if the block is exposed to the sky (surface)
        if (clicked.getWorld().getHighestBlockAt(clicked.getLocation()).getLocation().equals(clicked.getLocation())) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(10f);
            player.sendMessage(ChatColor.GREEN + "Terranox restored your health and hunger!");
            player.playSound(player.getLocation(), Sound.BLOCK_GRASS_STEP, 1f, 1.2f);
        } else {
            player.sendMessage(ChatColor.RED + "You must right-click a surface block exposed to the sky.");
        }
    }
}