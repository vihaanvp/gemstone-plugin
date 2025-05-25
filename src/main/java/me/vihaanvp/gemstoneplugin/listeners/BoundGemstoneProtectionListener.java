package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.utilities.BoundGemstoneUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BoundGemstoneProtectionListener implements Listener {

    private final Plugin plugin;
    // Stores UUID -> List of bound gemstones to return on respawn
    private final Map<UUID, List<ItemStack>> boundGemstonesToRestore = new HashMap<>();

    public BoundGemstoneProtectionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // Prevent dropping bound gemstones
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (BoundGemstoneUtils.isBoundGemstone(dropped, plugin)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop a bound gemstone!");
        }
    }

    // Remove bound gemstones from drops and remember them for respawn
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> keep = new ArrayList<>();
        Iterator<ItemStack> it = event.getDrops().iterator();
        while (it.hasNext()) {
            ItemStack item = it.next();
            if (BoundGemstoneUtils.isBoundGemstone(item, plugin)) {
                keep.add(item.clone());
                it.remove();
            }
        }
        if (!keep.isEmpty()) {
            boundGemstonesToRestore.put(player.getUniqueId(), keep);
        }
    }

    // Restore bound gemstones on respawn
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        List<ItemStack> toRestore = boundGemstonesToRestore.remove(uuid);
        if (toRestore != null) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : toRestore) {
                    Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
                    leftovers.values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
                }
                player.sendMessage("§aYour bound gemstones have returned to you!");
            }, 1L); // Delay to ensure inventory is ready
        }
    }
}