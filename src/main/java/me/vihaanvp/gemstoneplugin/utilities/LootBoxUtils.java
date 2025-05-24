package me.vihaanvp.gemstoneplugin.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LootBoxUtils {
    private static final Random RANDOM = new Random();

    // Define your loot pool here
    public static final List<ItemStack> LOOT_POOL = Arrays.asList(
            new ItemStack(Material.DIAMOND, 2),
            new ItemStack(Material.ENCHANTED_GOLDEN_APPLE),
            new ItemStack(Material.ENDER_PEARL, 2),
            new ItemStack(Material.EMERALD, 3),
            new ItemStack(Material.EXPERIENCE_BOTTLE, 5),
            new ItemStack(Material.GOLDEN_CARROT, 5)
    );

    public static ItemStack createLootBox() {
        ItemStack lootBox = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = lootBox.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Loot Box");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Right-click to open!",
                    ChatColor.DARK_GRAY + "Contains a random reward"
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            lootBox.setItemMeta(meta);
        }
        return lootBox;
    }

    public static boolean isLootBox(ItemStack item) {
        if (item == null || item.getType() != Material.ENDER_CHEST || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Loot Box");
    }

    public static ItemStack getRandomLoot() {
        return LOOT_POOL.get(RANDOM.nextInt(LOOT_POOL.size())).clone();
    }
}