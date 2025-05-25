package me.vihaanvp.gemstoneplugin.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ReviveBookUtils {

    public static ItemStack createReviveBook() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Revive Book");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "A mystical tome that revives",
                    ChatColor.YELLOW + "the deathbanned from the void.",
                    ChatColor.YELLOW + "Right-Click to use"
            ));
            meta.setCustomModelData(1001); // Optional: for custom resource pack
            book.setItemMeta(meta);
        }
        return book;
    }

    public static boolean isReviveBook(ItemStack item) {
        if (item == null || item.getType() != Material.BOOK) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.hasLore()) return false;

        // Check display name (with color)
        if (!meta.getDisplayName().equals(ChatColor.GOLD + "Revive Book")) return false;

        // Check lore
        List<String> lore = meta.getLore();
        if (lore == null || lore.size() != 3) return false;

        if (!lore.get(0).equals(ChatColor.YELLOW + "A mystical tome that revives")) return false;
        if (!lore.get(1).equals(ChatColor.YELLOW + "the deathbanned from the void.")) return false;
        if (!lore.get(2).equals(ChatColor.YELLOW + "Right-Click to use")) return false;

        return true;
    }
}