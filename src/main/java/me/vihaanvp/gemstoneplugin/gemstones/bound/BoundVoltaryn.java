package me.vihaanvp.gemstoneplugin.gemstones.bound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static me.vihaanvp.gemstoneplugin.gemstones.Voltaryn.isVoltaryn;

public class BoundVoltaryn {
    public static ItemStack createItem(Plugin plugin) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Voltaryn");
        meta.setLore(List.of(
                ChatColor.GRAY + "Passive Speed I",
                ChatColor.GRAY + "Right-click a player or mob to strike lightning (2 min cooldown)",
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

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (isVoltaryn(event.getItem())) {
            event.setCancelled(true); // Prevent durability loss
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity) {
            ItemStack held = player.getInventory().getItemInMainHand();
            if (isVoltaryn(held)) {
                event.setDamage(0); // Set damage to 0 when Voltaryn is used
            }
        }
    }
}