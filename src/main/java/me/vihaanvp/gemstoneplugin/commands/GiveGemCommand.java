package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import me.vihaanvp.gemstoneplugin.gemstones.bound.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class GiveGemCommand implements CommandExecutor {

    private final Plugin plugin;

    public GiveGemCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /givegem <gem> [player]
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /givegem <blazite|aquaryte|terranox|voltaryn|noctyra|super> [player]");
            return true;
        }

        String gemName = args[0].toLowerCase();
        Player target;

        if (args.length > 1) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command without a target.");
                return true;
            }
            target = (Player) sender;
        }

        ItemStack gemItem = null;

        switch (gemName) {
            case "blazite":
                gemItem = Blazite.createItem();
                break;
            case "aquaryte":
                gemItem = Aquaryte.createItem();
                break;
            case "terranox":
                gemItem = Terranox.createItem();
                break;
            case "voltaryn":
                gemItem = Voltaryn.createItem();
                break;
            case "noctyra":
                gemItem = Noctyra.createItem();
                break;
            case "boundblazite":
                gemItem = BoundBlazite.createItem(plugin);
                break;
            case "boundaquaryte":
                gemItem = BoundAquaryte.createItem(plugin);
                break;
            case "boundterranox":
                gemItem = BoundTerranox.createItem(plugin);
                break;
            case "boundvoltaryn":
                gemItem = BoundVoltaryn.createItem(plugin);
                break;
            case "boundnoctyra":
                gemItem = BoundNoctyra.createItem(plugin);
                break;
            case "super":
            case "supergem":
            case "supergemstone":
                gemItem = SuperGemstone.create(plugin);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown gem: " + gemName);
                return true;
        }

        if (gemItem != null) {
            target.getInventory().addItem(gemItem);
            sender.sendMessage(ChatColor.GREEN + "Gave " + ChatColor.AQUA + gemItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " to " + target.getName() + "!");
            if (!sender.equals(target)) {
                target.sendMessage(ChatColor.GOLD + "You received a " + ChatColor.AQUA + gemItem.getItemMeta().getDisplayName());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to create the gemstone.");
        }
        return true;
    }
}