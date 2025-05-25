package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.utilities.BoundGemstoneUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BoundGemstoneListener implements Listener {

    private final Plugin plugin;

    public BoundGemstoneListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Prevent all means of putting a bound gemstone into non-player inventories.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Allow taking from anvil output slot
        if (event.getView().getTopInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            return;
        }

        Inventory clickedInv = event.getClickedInventory();
        Inventory topInv = event.getView().getTopInventory();
        Player player = (event.getWhoClicked() instanceof Player) ? (Player) event.getWhoClicked() : null;
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // Prevent placing from cursor into non-player inventory (normal click, hotbar swap)
        if (BoundGemstoneUtils.isBoundGemstone(cursor, plugin) && clickedInv != null && player != null) {
            if (!clickedInv.equals(player.getInventory())) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot put a bound gemstone in other inventories!");
                return;
            }
        }

        // Prevent shift-clicking a bound gemstone into a non-player inventory (shift-click from player)
        if (event.isShiftClick() && BoundGemstoneUtils.isBoundGemstone(current, plugin) && clickedInv != null && player != null) {
            // If the top inventory is not the player's, the destination is a non-player inventory
            if (topInv != null && !topInv.getType().equals(InventoryType.PLAYER)) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot put a bound gemstone in other inventories!");
                return;
            }
        }

        // Prevent taking bound gemstone out of a non-player inventory (already handled, but for completeness)
        if (BoundGemstoneUtils.isBoundGemstone(current, plugin) && clickedInv != null && player != null) {
            if (!clickedInv.equals(player.getInventory())) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot move a bound gemstone out of your inventory!");
            }
        }
    }

    /**
     * Prevent dragging bound gemstones into non-player inventory slots.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack dragged = event.getOldCursor();
        if (!BoundGemstoneUtils.isBoundGemstone(dragged, plugin)) return;

        Player player = (event.getWhoClicked() instanceof Player) ? (Player) event.getWhoClicked() : null;
        Inventory topInv = event.getView().getTopInventory();

        // If dragging over any slot that's NOT the player's inventory, cancel!
        for (int slot : event.getRawSlots()) {
            if (topInv != null && slot < topInv.getSize()) {
                event.setCancelled(true);
                if (player != null) player.sendMessage("§cYou cannot put a bound gemstone in other inventories!");
                return;
            }
        }
    }

    /**
     * (Optional) Prevent hoppers from moving bound gemstones.
     */
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();
        if (BoundGemstoneUtils.isBoundGemstone(item, plugin)) {
            event.setCancelled(true);
        }
    }
}