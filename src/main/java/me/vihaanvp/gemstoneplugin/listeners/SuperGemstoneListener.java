package me.vihaanvp.gemstoneplugin.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SuperGemstoneListener implements Listener {
    private final Plugin plugin;

    // Modes: 0 = Defence (Terranox), 1 = Offence (Blazite+Voltaryn), 2 = Stealth (Noctyra)
    private static final String SUPER_GEM_MODE = "super_gem_mode";
    private static final String SUPER_GEM_KEY = "super_gemstone";

    // Cooldowns (milliseconds)
    private static final long MODE_SWITCH_COOLDOWN = 2000; // 2 seconds
    private static final long DEFENCE_COOLDOWN = 240_000; // 4 minutes
    private static final long OFFENCE_COOLDOWN = 180_000; // 3 minutes
    private static final long STEALTH_COOLDOWN = 600_000; // 10 minutes

    // Store cooldowns per player
    private final Map<UUID, Long> modeSwitchCooldowns = new HashMap<>();
    private final Map<UUID, Long> defenceCooldowns = new HashMap<>();
    private final Map<UUID, Long> offenceCooldowns = new HashMap<>();
    private final Map<UUID, Long> stealthCooldowns = new HashMap<>();

    // Track who was recently targeted by a super gemstone's offence ability, for lightning immunity logic
    private final Map<UUID, Long> recentlyTargetedBySuperGemstone = new HashMap<>();
    private static final long RECENT_TARGET_WINDOW = 2000; // ms

    public SuperGemstoneListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // --- PASSIVES ---

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSuperGemstone(item, plugin)) return;

        // Blazite Passive: Fire Resistance
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false, false));
        // Aquaryte Passive: Water passives
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 0, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 60, 0, true, false, false));

        // Terranox Passive: Strength on natural blocks
        Block block = player.getLocation().add(0, -1, 0).getBlock();
        if (isNaturalBlock(block.getType())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 0, true, false, false));
        }

        // Voltaryn Passive: Speed I
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true, false, false));

        // Noctyra Passive: Night Vision (long duration)
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 1, false, false));
    }

    // --- MODE SWITCHING AND ACTIVES ---
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSuperGemstone(item, plugin)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int mode = container.getOrDefault(new NamespacedKey(plugin, SUPER_GEM_MODE), PersistentDataType.INTEGER, 0);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        boolean bypassCooldown = player.hasPermission("gemstones.supergem.cooldown.bypass");

        // Left click switches mode
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            if (!bypassCooldown && modeSwitchCooldowns.containsKey(uuid) && now - modeSwitchCooldowns.get(uuid) < MODE_SWITCH_COOLDOWN) {
                long msLeft = MODE_SWITCH_COOLDOWN - (now - modeSwitchCooldowns.get(uuid));
                player.sendMessage(ChatColor.GRAY + "You must wait " + ((msLeft / 1000.0)) + "s before switching modes again.");
                return;
            }
            int newMode = (mode + 1) % 3;
            container.set(new NamespacedKey(plugin, SUPER_GEM_MODE), PersistentDataType.INTEGER, newMode);
            meta.setLore(SuperGemstoneLore.getLoreForMode(newMode));
            item.setItemMeta(meta);
            player.sendMessage(ChatColor.GOLD + "Super Gemstone mode switched to " + getModeName(newMode));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.1f);
            if (!bypassCooldown) modeSwitchCooldowns.put(uuid, now);
            return;
        }

        // Right click activates current mode ability
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            if (mode == 0) {
                // DEFENCE (Terranox): Heal if block under is surface and exposed to sky
                if (!bypassCooldown && defenceCooldowns.containsKey(uuid) && now - defenceCooldowns.get(uuid) < DEFENCE_COOLDOWN) {
                    long msLeft = DEFENCE_COOLDOWN - (now - defenceCooldowns.get(uuid));
                    player.sendMessage(ChatColor.GRAY + "Defence ability is on cooldown for " + formatTime(msLeft) + ".");
                    return;
                }
                Block block = player.getLocation().add(0, -1, 0).getBlock();
                if (block.getWorld().getHighestBlockAt(block.getLocation()).getLocation().equals(block.getLocation())) {
                    player.setHealth(player.getMaxHealth());
                    player.setFoodLevel(20);
                    player.setSaturation(10f);
                    player.sendMessage(ChatColor.GREEN + "Terranox restored your health and hunger!");
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_STEP, 1f, 1.2f);
                    if (!bypassCooldown) defenceCooldowns.put(uuid, now);
                } else {
                    player.sendMessage(ChatColor.RED + "You must right-click a surface block exposed to the sky.");
                }

            } else if (mode == 1) {
                // OFFENCE: Blazite (fire) + Voltaryn (lightning) on nearest entity in sight
                if (!bypassCooldown && offenceCooldowns.containsKey(uuid) && now - offenceCooldowns.get(uuid) < OFFENCE_COOLDOWN) {
                    long msLeft = OFFENCE_COOLDOWN - (now - offenceCooldowns.get(uuid));
                    player.sendMessage(ChatColor.GRAY + "Offence ability is on cooldown for " + formatTime(msLeft) + ".");
                    return;
                }
                LivingEntity target = getNearestLivingEntityInSight(player, 10);
                if (target != null && !target.equals(player)) {
                    // Blazite: Set on fire
                    target.setFireTicks(100);
                    // Voltaryn: Strike lightning
                    target.getWorld().strikeLightning(target.getLocation());
                    // Mark the target as recently struck by a Super Gemstone for 2 seconds
                    if (target instanceof Player targetPlayer) {
                        recentlyTargetedBySuperGemstone.put(targetPlayer.getUniqueId(), System.currentTimeMillis());
                    }
                    // Clean up expired entries
                    recentlyTargetedBySuperGemstone.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() > RECENT_TARGET_WINDOW + 1000);

                    player.sendMessage(ChatColor.RED + "You set " + target.getName() + " on fire and struck them with lightning!");
                    if (target instanceof Player pTarget) {
                        pTarget.sendMessage(ChatColor.RED + "You were set on fire and struck by lightning by a Super Gemstone!");
                    }
                    if (!bypassCooldown) offenceCooldowns.put(uuid, now);
                } else {
                    player.sendMessage(ChatColor.GRAY + "No valid target found in sight (within 10 blocks).");
                }
            } else if (mode == 2) {
                // STEALTH: Noctyra - Invisibility (longer for super)
                if (!bypassCooldown && stealthCooldowns.containsKey(uuid) && now - stealthCooldowns.get(uuid) < STEALTH_COOLDOWN) {
                    long msLeft = STEALTH_COOLDOWN - (now - stealthCooldowns.get(uuid));
                    player.sendMessage(ChatColor.GRAY + "Stealth ability is on cooldown for " + formatTime(msLeft) + ".");
                    return;
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 240, 0, true, false, false)); // 4 min (buffed)
                player.sendMessage(ChatColor.DARK_PURPLE + "You vanished into the shadows... (but your armor hasn't)");
                if (!bypassCooldown) stealthCooldowns.put(uuid, now);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.8f, 1.3f);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSuperGemstone(item, plugin)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int mode = container.getOrDefault(new NamespacedKey(plugin, SUPER_GEM_MODE), PersistentDataType.INTEGER, 0);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        boolean bypassCooldown = player.hasPermission("gemstones.supergem.cooldown.bypass");

        if (mode == 1) { // Offence mode: right click entity for fire+lightning
            if (!bypassCooldown && offenceCooldowns.containsKey(uuid) && now - offenceCooldowns.get(uuid) < OFFENCE_COOLDOWN) {
                long msLeft = OFFENCE_COOLDOWN - (now - offenceCooldowns.get(uuid));
                player.sendMessage(ChatColor.GRAY + "Offence ability is on cooldown for " + formatTime(msLeft) + ".");
                return;
            }
            LivingEntity target = (event.getRightClicked() instanceof LivingEntity living) ? living : null;
            if (target == null || target.equals(player)) return;
            event.setCancelled(true);

            target.setFireTicks(100);
            target.getWorld().strikeLightning(target.getLocation());
            // Mark the target as recently struck by a Super Gemstone for 2 seconds
            if (target instanceof Player targetPlayer) {
                recentlyTargetedBySuperGemstone.put(targetPlayer.getUniqueId(), System.currentTimeMillis());
            }
            // Clean up expired entries
            recentlyTargetedBySuperGemstone.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() > RECENT_TARGET_WINDOW + 1000);

            player.sendMessage(ChatColor.RED + "You set " + target.getName() + " on fire and struck them with lightning!");
            if (target instanceof Player pTarget) {
                pTarget.sendMessage(ChatColor.RED + "You were set on fire and struck by lightning by a Super Gemstone!");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.9f, 1.25f);
            if (!bypassCooldown) offenceCooldowns.put(uuid, now);
        }
    }

    @EventHandler
    public void onLightningDamage(EntityDamageEvent event) {
        // Only care about lightning damage
        if (event.getCause() != EntityDamageEvent.DamageCause.LIGHTNING) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // Check if player has Super Gemstone in main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSuperGemstone(item, plugin)) return;

        // Cancel damage if this is not as a result of a recent super gemstone attack from another player
        if (recentlyTargetedBySuperGemstone.containsKey(player.getUniqueId())) {
            long ts = recentlyTargetedBySuperGemstone.get(player.getUniqueId());
            if (System.currentTimeMillis() - ts <= RECENT_TARGET_WINDOW) {
                // Was recently targeted; do NOT grant immunity
                return;
            }
        }
        // Otherwise, grant immunity
        event.setCancelled(true);
    }

    // --- Utility and helpers ---

    public static boolean isSuperGemstone(ItemStack item, Plugin plugin) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, SUPER_GEM_KEY);
        Integer value = container.get(key, PersistentDataType.INTEGER);
        return value != null && value == 1;
    }

    private boolean isNaturalBlock(Material material) {
        // Replicates Terranox's natural blocks
        return switch (material) {
            case GRASS_BLOCK, DIRT, COARSE_DIRT, PODZOL, SAND, RED_SAND, STONE, GRAVEL, MOSS_BLOCK, MYCELIUM, ROOTED_DIRT -> true;
            default -> false;
        };
    }

    private String getModeName(int mode) {
        return switch (mode) {
            case 0 -> "Defence";
            case 1 -> "Offence";
            case 2 -> "Stealth";
            default -> "Unknown";
        };
    }

    /**
     * Returns the nearest living entity the player is looking at within maxDistance blocks.
     */
    private LivingEntity getNearestLivingEntityInSight(Player player, int maxDistance) {
        List<Entity> nearby = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        Location eye = player.getEyeLocation();
        @NotNull Vector to = eye.getDirection().normalize();
        for (double i = 0; i < maxDistance; i += 0.5) {
            Location check = eye.clone().add(to.clone().multiply(i));
            for (Entity e : nearby) {
                if (!(e instanceof LivingEntity le) || le.equals(player)) continue;
                if (le.getBoundingBox().expand(0.5).contains(check.toVector())) {
                    return le;
                }
            }
        }
        return null;
    }

    private String formatTime(long msLeft) {
        long seconds = msLeft / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes > 0)
            return String.format("%dm %ds", minutes, seconds);
        else
            return String.format("%ds", seconds);
    }
}

/**
 * Utility for Super Gemstone lore per mode.
 */
class SuperGemstoneLore {
    static List<String> getLoreForMode(int mode) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "The ultimate gemstone, radiating all powers.");
        lore.add(ChatColor.LIGHT_PURPLE + "Passive: All gemstone passives (buffed)");
        lore.add(""); // spacing
        lore.add(ChatColor.GREEN + "Left-Click: Switch mode");
        lore.add(ChatColor.GREEN + "Right-Click: Activate current mode's ability");
        lore.add(""); // spacing

        if (mode == 0) {
            lore.add(ChatColor.GRAY + "Current: " + ChatColor.BOLD + "Defence");
            lore.add(ChatColor.GRAY + "Terranox active - heal on surface");
        } else if (mode == 1) {
            lore.add(ChatColor.RED + "Current: " + ChatColor.BOLD + "Offence");
            lore.add(ChatColor.RED + "Blazite+Voltaryn actives - fire+lightning");
        } else if (mode == 2) {
            lore.add(ChatColor.DARK_PURPLE + "Current: " + ChatColor.BOLD + "Stealth");
            lore.add(ChatColor.DARK_PURPLE + "Noctyra active - invisibility");
        }
        lore.add(""); // spacing
        lore.add(ChatColor.DARK_PURPLE + "Bound Gemstone");
        return lore;
    }
}