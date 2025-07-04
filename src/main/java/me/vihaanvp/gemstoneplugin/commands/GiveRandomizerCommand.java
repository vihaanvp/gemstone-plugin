package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.RandomizerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class GiveRandomizerCommand implements CommandExecutor {

    private final Plugin plugin;

    public GiveRandomizerCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Usage: /giverandomizer <amount> [player]
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /giverandomizer <amount> [player]");
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

        // You must implement this utility method to return a Randomizer ItemStack.
        ItemStack randomizer = RandomizerUtils.createRandomizer();
        randomizer.setAmount(amount);

        Map<Integer, ItemStack> leftovers = target.getInventory().addItem(randomizer);
        leftovers.values().forEach(item -> target.getWorld().dropItemNaturally(target.getLocation(), item));

        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Gemstone Randomizer" + (amount == 1 ? "!" : "s!"));
        if (!target.equals(sender)) {
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Gemstone Randomizer" + (amount == 1 ? "" : "s") + " to " + target.getName() + ".");
        }
        return true;
    }
}