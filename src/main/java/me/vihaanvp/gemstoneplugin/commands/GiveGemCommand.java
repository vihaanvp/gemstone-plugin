package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveGemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /givegem <blazite|aquaryte|terranox|voltaryn|noctyra> [player]");
            return true;
        }
        String gem = args[0].toLowerCase();
        ItemStack item = null;
        switch (gem) {
            case "blazite":
                item = Blazite.createItem();
                break;
            case "aquaryte":
                item = Aquaryte.createItem();
                break;
            case "terranox":
                item = Terranox.createItem();
                break;
            case "voltaryn":
                item = Voltaryn.createItem();
                break;
            case "noctyra":
                item = Noctyra.createItem();
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown gemstone: " + gem);
                return true;
        }
        Player target;
        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                return true;
            }
            target = (Player) sender;
        }
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Gave " + capitalize(gem) + " to " + target.getName());
        if (target != sender) {
            target.sendMessage(ChatColor.GREEN + "You received a " + capitalize(gem) + "!");
        }
        return true;
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}