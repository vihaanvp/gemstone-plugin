package me.vihaanvp.gemstoneplugin.listeners;

import me.vihaanvp.gemstoneplugin.utilities.DeathBanManager;
import me.vihaanvp.gemstoneplugin.utilities.ReviveBookUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReviveBookListener implements Listener {

    private final DeathBanManager deathBanManager;
    private final Set<UUID> awaitingInput = new HashSet<>();

    public ReviveBookListener(DeathBanManager deathBanManager) {
        this.deathBanManager = deathBanManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !ReviveBookUtils.isReviveBook(item)) return;

        event.setCancelled(true); // Prevent default use

        if (awaitingInput.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the process of reviving someone. Type the player's name in chat, or type 'cancel' to cancel.");
            return;
        }

        awaitingInput.add(player.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "Please type the name of the player you wish to revive in chat. Type 'cancel' to cancel.");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!awaitingInput.contains(player.getUniqueId())) return;

        event.setCancelled(true);

        String msg = event.getMessage();
        if (msg.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.YELLOW + "Revive cancelled.");
            awaitingInput.remove(player.getUniqueId());
            return;
        }

        String targetName = msg.trim();
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (target == null || target.getUniqueId() == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            awaitingInput.remove(player.getUniqueId());
            return;
        }

        if (!deathBanManager.isDeathbanned(target.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + targetName + " is not currently deathbanned!");
            awaitingInput.remove(player.getUniqueId());
            return;
        }

        deathBanManager.addPendingRevive(target.getName());

        // Remove one revive book
        for (ItemStack item : player.getInventory().getContents()) {
            if (ReviveBookUtils.isReviveBook(item)) {
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }

        player.sendMessage(ChatColor.GREEN + "You have prepared to revive " + targetName + ". They can now rejoin!");
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " has successfully revived " + targetName + "!");
        awaitingInput.remove(player.getUniqueId());
    }
}