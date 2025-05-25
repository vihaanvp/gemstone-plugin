package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.gemstones.bound.*;
import me.vihaanvp.gemstoneplugin.gemstones.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class BoundGemstoneTokenListener implements Listener {

    private final Plugin plugin;

    public BoundGemstoneTokenListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkAndReplace(event.getPlayer()), 2L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkAndReplace(event.getPlayer()), 2L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkAndReplace(player), 2L);
        }
    }

    private void checkAndReplace(Player player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || !item.hasItemMeta() || !item.getType().equals(org.bukkit.Material.PAPER)) continue;
            String name = item.getItemMeta().getDisplayName();
            if (name == null) continue;
            ItemStack replacement = null;
            if (name.equals("§dBound Blazite")) {
                replacement = BoundBlazite.createItem(plugin);
            } else if (name.equals("§dBound Aquaryte")) {
                replacement = BoundAquaryte.createItem(plugin);
            } else if (name.equals("§dBound Terranox")) {
                replacement = BoundTerranox.createItem(plugin);
            } else if (name.equals("§dBound Voltaryn")) {
                replacement = BoundVoltaryn.createItem(plugin);
            } else if (name.equals("§dBound Noctyra")) {
                replacement = BoundNoctyra.createItem(plugin);
            }
            if (replacement != null) {
                replacement.setAmount(item.getAmount());
                inv.setItem(i, replacement);
                player.sendMessage("§dYour bound gemstone has been forged!");
            }
        }
    }
}