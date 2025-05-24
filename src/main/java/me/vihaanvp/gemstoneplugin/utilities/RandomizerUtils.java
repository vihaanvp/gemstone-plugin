package me.vihaanvp.gemstoneplugin.utilities;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

public class RandomizerUtils implements Listener {

    public static ItemStack createRandomizer() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dGemstone Randomizer");
        meta.setLore(List.of("§7Right-click to get a random gemstone!"));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() == Material.NETHER_STAR
                && item.getItemMeta() != null
                && "§dGemstone Randomizer".equals(item.getItemMeta().getDisplayName())) {

            // Remove 1 randomizer
            item.setAmount(item.getAmount() - 1);

            // Give random gemstone
            ItemStack[] gemstones = {
                    Blazite.createItem(),
                    Aquaryte.createItem(),
                    Terranox.createItem(),
                    Voltaryn.createItem(),
                    Noctyra.createItem(),
            };
            ItemStack gemstone = gemstones[new Random().nextInt(gemstones.length)];
            player.getInventory().addItem(gemstone);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }
}