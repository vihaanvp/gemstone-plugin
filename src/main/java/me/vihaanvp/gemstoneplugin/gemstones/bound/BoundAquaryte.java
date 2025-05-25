package me.vihaanvp.gemstoneplugin.gemstones.bound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BoundAquaryte {
    public static ItemStack createItem(Plugin plugin) {
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Aquaryte");
        meta.setLore(List.of(
                ChatColor.GRAY + "Grants Water Breathing, Dolphin's Grace, Conduit Power when held",
                ChatColor.GRAY + "Right-click to teleport to nearest water source (60s cooldown)",
                ChatColor.GRAY + "The water source must be at surface level and exposed to the sky",
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
}