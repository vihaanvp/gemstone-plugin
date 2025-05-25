package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.ReviveBookUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class GiveReviveBookCommand implements CommandExecutor {

    private final Plugin plugin;

    public GiveReviveBookCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Usage: /giverevivebook <amount> [player]
        if (!sender.hasPermission("gemstones.revivebook.give")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /giverevivebook <amount> [player]");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a positive integer!");
            return true;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found or not online.");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "You must specify a player when running this command from the console.");
            return true;
        }

        ItemStack reviveBook = ReviveBookUtils.createReviveBook();
        reviveBook.setAmount(amount);

        Map<Integer, ItemStack> leftovers = target.getInventory().addItem(reviveBook);
        leftovers.values().forEach(item -> target.getWorld().dropItemNaturally(target.getLocation(), item));

        target.sendMessage(ChatColor.GOLD + "You have received " + amount + " Revive Book" + (amount == 1 ? "!" : "s!"));
        if (!target.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "Gave " + amount + " Revive Book" + (amount == 1 ? "" : "s") + " to " + target.getName() + ".");
        }
        return true;
    }
}