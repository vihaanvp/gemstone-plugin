package me.vihaanvp.gemstoneplugin.gemstones;

import me.vihaanvp.gemstoneplugin.utilities.BoundGemstoneUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class SuperGemstone {
    public static final String SUPER_GEM_KEY = "super_gemstone";
    public static final String SUPER_GEM_MODE = "super_gemstone_mode";
    public static final String BOUND_KEY = "bound_gemstone";

    // Each letter is a different color, all bold
    public static String getDisplayName() {
        return ChatColor.BOLD +
                "" + ChatColor.RED + "S"
                + ChatColor.GOLD + "u"
                + ChatColor.YELLOW + "p"
                + ChatColor.GREEN + "e"
                + ChatColor.AQUA + "r"
                + ChatColor.LIGHT_PURPLE + " "
                + ChatColor.DARK_PURPLE + "G"
                + ChatColor.BLUE + "e"
                + ChatColor.DARK_AQUA + "m"
                + ChatColor.DARK_GREEN + "s"
                + ChatColor.DARK_RED + "t"
                + ChatColor.DARK_GRAY + "o"
                + ChatColor.GRAY + "n"
                + ChatColor.WHITE + "e";
    }

    public static ItemStack create(Plugin plugin) {
        ItemStack item = new ItemStack(Material.NETHER_STAR); // Or your custom item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getDisplayName());
            meta.setLore(Arrays.asList(
                    ChatColor.AQUA + "The ultimate gemstone, radiating all powers.",
                    ChatColor.LIGHT_PURPLE + "Passive: All gemstone passives (buffed)",
                    " ",
                    ChatColor.GREEN + "Left-Click: Switch mode",
                    ChatColor.GREEN + "Right-Click: Activate current mode's ability",
                    " ",
                    ChatColor.GRAY + "Defence: Terranox active (lower cooldown)",
                    ChatColor.RED + "Offence: Blazite + Voltaryn actives (lower cooldown)",
                    ChatColor.DARK_PURPLE + "Stealth: Noctyra active (longer, lower cooldown)",
                    " ",
                    ChatColor.DARK_PURPLE + "Bound Gemstone"
            ));
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, SUPER_GEM_KEY), PersistentDataType.INTEGER, 1);
            container.set(new NamespacedKey(plugin, BOUND_KEY), PersistentDataType.INTEGER, 1);
            container.set(new NamespacedKey(plugin, SUPER_GEM_MODE), PersistentDataType.INTEGER, 0); // Default mode
            item.setItemMeta(meta);
            BoundGemstoneUtils.setBound(item, plugin);
        }
        return item;
    }

    public boolean isSuperGemstone(ItemStack item, Plugin plugin) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "super_gemstone");
        Integer value = container.get(key, PersistentDataType.INTEGER);
        return value != null && value == 1;
    }

    public static ItemStack createToken() {
        ItemStack token = new ItemStack(Material.PAPER);
        ItemMeta meta = token.getItemMeta();
        meta.setDisplayName("§dSuper Gemstone Token");
        meta.setLore(List.of(
                "§7A token of ultimate power.",
                "§7Will be forged into a Super Gemstone!"
        ));
        token.setItemMeta(meta);
        return token;
    }
}