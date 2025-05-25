package me.vihaanvp.gemstoneplugin.gemstones.bound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BoundNoctyra {
    public static ItemStack createItem(Plugin plugin) {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "Noctyra");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to become invisible for 3 minutes",
                ChatColor.GRAY + "Cooldown: 10 minutes",
                ChatColor.BLACK + "",
                ChatColor.DARK_PURPLE + "Bound Gemstone"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        // Mark as bound using a NamespacedKey
        NamespacedKey key = new NamespacedKey(plugin, "bound");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        event.setCancelled(true);
    }
}