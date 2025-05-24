package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.LootBoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GiveLootBoxCommand implements CommandExecutor {

    private final Plugin plugin;

    public GiveLootBoxCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gemstoneplugin.givelootbox")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /givelootbox <amount> [player]");
            return true;
        }

        int amount = 1;

        // Parse amount
        try {
            amount = Integer.parseInt(args[0]);
            if (amount < 1) amount = 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount, defaulting to 1.");
        }

        Player target;
        if (args.length >= 2) {
            // Player specified
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
        } else {
            // No player specified, use sender if player
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                return true;
            }
        }

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(LootBoxUtils.createLootBox());
        }

        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " loot box(es) to " + target.getName() + ".");
        if (target != sender) {
            target.sendMessage(ChatColor.LIGHT_PURPLE + "You have received " + amount + " Gemstone Loot Box" + (amount > 1 ? "es!" : "!"));
        }

        return true;
    }
}