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

public class BoundBlazite {
    public static ItemStack createItem(Plugin plugin) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Blazite");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Grants Fire Resistance when held");
        lore.add(ChatColor.GRAY + "Right-click an entity to set it on fire (60s cooldown)");
        lore.add("");
        lore.add(ChatColor.DARK_PURPLE + "Bound Gemstone");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        // Mark as bound
        NamespacedKey key = new NamespacedKey(plugin, "bound");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
        return item;
    }
}