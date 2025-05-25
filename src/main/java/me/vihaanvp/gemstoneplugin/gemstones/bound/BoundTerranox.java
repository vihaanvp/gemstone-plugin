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

public class BoundTerranox {
    public static ItemStack createItem(Plugin plugin) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Terranox");
        meta.setLore(List.of(
                ChatColor.DARK_GREEN + "Grants Strength when standing on natural blocks",
                ChatColor.DARK_GREEN + "Right-click surface block to fully heal",
                "",
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