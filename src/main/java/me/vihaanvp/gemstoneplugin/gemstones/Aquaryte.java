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

import java.util.*;

public class Aquaryte implements Listener {

    private static final int COOLDOWN_SECONDS = 60;
    private static final int MAX_DISTANCE = 100;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Aquaryte");
            meta.setLore(List.of(
                    ChatColor.GRAY + "Grants Water Breathing, Dolphin's Grace, Conduit Power when held",
                    ChatColor.GRAY + "Right-click to teleport to nearest water source (60s cooldown)",
                    ChatColor.GRAY + "The water source must be at surface level and exposed to the sky"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isAquaryte(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Aquaryte");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (isAquaryte(mainHand) || isAquaryte(offHand)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 0, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 60, 0, true, false, false));
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isAquaryte(held)) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (!player.hasPermission("gemstones.aquaryte.bypass")) {
            if (cooldowns.containsKey(uuid)) {
                long lastUsed = cooldowns.get(uuid);
                long elapsed = (now - lastUsed) / 1000;
                if (elapsed < COOLDOWN_SECONDS) {
                    long remaining = COOLDOWN_SECONDS - elapsed;
                    player.sendMessage(ChatColor.RED + "Wait " + remaining + " seconds before using Aquaryte again.");
                    return;
                }
            }
            cooldowns.put(uuid, now);
        }

        Location nearestWater = findNearestWater(player.getLocation(), MAX_DISTANCE);
        if (nearestWater != null) {
            player.teleport(nearestWater.add(0.5, 1, 0.5));
            player.sendMessage(ChatColor.AQUA + "You were teleported to the nearest water source.");
            player.playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_SPLASH, 1f, 1f);
        } else {
            player.sendMessage(ChatColor.RED + "No water source found within " + MAX_DISTANCE + " blocks.");
        }
    }

    private Location findNearestWater(Location center, int radius) {
        World world = center.getWorld();
        if (world == null) return null;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int r = 1; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    for (int z = -r; z <= r; z++) {
                        Block block = world.getBlockAt(cx + x, cy + y, cz + z);
                        if (block.getType() == Material.WATER && isSurfaceWater(block)) {
                            return block.getLocation();
                        }
                    }
                }
            }
        }
        return null;
    }

    // Check if the water is exposed to the sky and at surface level
    private boolean isSurfaceWater(Block block) {
        // Water should be at or near the surface level and exposed to the sky
        if (block.getY() < 60) return false; // Assuming surface water is above Y-level 60

        // Check if there are no blocks directly above the water (exposed to sky)
        for (int y = block.getY() + 1; y <= block.getY() + 5; y++) {
            if (block.getWorld().getBlockAt(block.getX(), y, block.getZ()).getType() != Material.AIR) {
                return false; // Block above is not air, meaning it's not exposed to sky
            }
        }
        return true;
    }
}