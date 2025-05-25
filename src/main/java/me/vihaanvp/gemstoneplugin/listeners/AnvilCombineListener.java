package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import me.vihaanvp.gemstoneplugin.utilities.BoundGemstoneUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class AnvilCombineListener implements Listener {

    private final Plugin plugin;

    public AnvilCombineListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * This event prepares the anvil result as a "Bound <Gemstone>" token (PAPER).
     */
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack left = inventory.getItem(0);
        ItemStack right = inventory.getItem(1);

        if (left == null || right == null) return;

        boolean leftIsBinder = BoundGemstoneUtils.isGemstoneBinder(left);
        boolean rightIsBinder = BoundGemstoneUtils.isGemstoneBinder(right);

        if (leftIsBinder == rightIsBinder) return; // Both binder or both not, invalid combination

        ItemStack gemstone = leftIsBinder ? right : left;
        ItemStack binder = leftIsBinder ? left : right;

        if (!BoundGemstoneUtils.isGemstoneBinder(binder)) return;

        String boundName = null;
        if (Blazite.isBlazite(gemstone)) {
            boundName = "Bound Blazite";
        } else if (Aquaryte.isAquaryte(gemstone)) {
            boundName = "Bound Aquaryte";
        } else if (Terranox.isTerranox(gemstone)) {
            boundName = "Bound Terranox";
        } else if (Voltaryn.isVoltaryn(gemstone)) {
            boundName = "Bound Voltaryn";
        } else if (Noctyra.isNoctyra(gemstone)) {
            boundName = "Bound Noctyra";
        }

        if (boundName != null) {
            ItemStack token = new ItemStack(Material.PAPER);
            ItemMeta meta = token.getItemMeta();
            meta.setDisplayName("§d" + boundName);
            meta.setLore(List.of("§7Take this to automatically receive your bound gemstone!"));
            // Optionally, add persistent data so you can easily detect this token later
            NamespacedKey key = new NamespacedKey(plugin, "bound_gemstone_token");
            meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, boundName);
            token.setItemMeta(meta);
            event.setResult(token);
        }
    }

    /**
     * This event handles clicking the anvil result slot. It gives the player the token and removes one binder and one gemstone.
     * (If you use an inventory-scan listener to turn tokens into real bound gemstones, this is all you need.)
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only handle anvil result slot
        if (event.getView().getTopInventory().getType() != InventoryType.ANVIL || event.getRawSlot() != 2) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || clicked.getType() != Material.PAPER) return;

        ItemMeta meta = clicked.getItemMeta();
        String name = meta.getDisplayName();
        if (name == null || !name.startsWith("§dBound ")) return;

        Player player = (Player) event.getWhoClicked();
        AnvilInventory anvil = (AnvilInventory) event.getInventory();
        ItemStack left = anvil.getItem(0);
        ItemStack right = anvil.getItem(1);

        // Remove one binder and one gemstone from input slots
        int binderSlot = BoundGemstoneUtils.isGemstoneBinder(left) ? 0 : 1;
        int gemSlot = BoundGemstoneUtils.isGemstoneBinder(left) ? 1 : 0;

        ItemStack binder = anvil.getItem(binderSlot);
        ItemStack gemstone = anvil.getItem(gemSlot);

        if (binder != null) {
            binder.setAmount(binder.getAmount() - 1);
            anvil.setItem(binderSlot, binder.getAmount() > 0 ? binder : null);
        }
        if (gemstone != null) {
            gemstone.setAmount(gemstone.getAmount() - 1);
            anvil.setItem(gemSlot, gemstone.getAmount() > 0 ? gemstone : null);
        }

        // Give the token to the player (handled by vanilla, but optionally ensure it)
        // Optionally, clear the result slot (not required)
        anvil.setItem(2, null);

        // Prevent vanilla behavior (dupe bug prevention)
        event.setCancelled(true);

        // Place the token on their cursor
        player.setItemOnCursor(clicked);

        // Optionally, send a message
        player.sendMessage("§dTake your bound gemstone token! It will turn into the real thing in your inventory.");
    }
}