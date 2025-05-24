package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.utilities.LootBoxUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class LootBoxListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Location loc = dead.getLocation();
        // Drop 1 loot box at death location
        dead.getWorld().dropItemNaturally(loc, LootBoxUtils.createLootBox());
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // Only main hand
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (LootBoxUtils.isLootBox(item)) {
            event.setCancelled(true);
            // Give random loot
            player.getInventory().addItem(LootBoxUtils.getRandomLoot());
            // Remove one loot box from hand
            item.setAmount(item.getAmount() - 1);
            player.sendMessage("§aYou opened a Loot Box!");
        }
    }
}