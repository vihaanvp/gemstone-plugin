package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.RandomizerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveRandomizerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /giverandomizer <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        target.getInventory().addItem(RandomizerUtils.createRandomizer());
        sender.sendMessage(ChatColor.GREEN + "Gave Gemstone Randomizer to " + target.getName());
        return true;
    }
}