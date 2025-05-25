package me.vihaanvp.gemstoneplugin.utilities;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BoundGemstoneUtils {

    /**
     * Checks whether the given ItemStack is a bound gemstone.
     * @param item The ItemStack to check.
     * @param plugin Your main plugin instance (for NamespacedKey).
     * @return true if the item is a bound gemstone, false otherwise.
     */
    public static boolean isBoundGemstone(ItemStack item, Plugin plugin) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "bound");
        Byte value = container.get(key, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

    /**
     * Optionally, mark an ItemStack as a bound gemstone.
     * @param item The ItemStack to mark.
     * @param plugin Your main plugin instance (for NamespacedKey).
     */
    public static void setBound(ItemStack item, Plugin plugin) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "bound");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    public static ItemStack createGemstoneBinder() {
        ItemStack binder = new ItemStack(Material.PAPER); // Use a unique material or a custom model if possible
        ItemMeta meta = binder.getItemMeta();
        meta.setDisplayName("§bGemstone Binder");
        meta.setLore(List.of(
                "§7Combine with a Gemstone in an anvil",
                "§7to bind it to your soul."
        ));
        binder.setItemMeta(meta);
        return binder;
    }

    public static boolean isGemstoneBinder(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return item.getType() == Material.PAPER &&
                meta.hasDisplayName() &&
                "§bGemstone Binder".equals(meta.getDisplayName());
    }
}