package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.DeathBanManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadReviveFileCommand implements CommandExecutor {

    private final DeathBanManager deathBanManager;

    public ReloadReviveFileCommand(DeathBanManager deathBanManager) {
        this.deathBanManager = deathBanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gemstones.reloadrevivefile")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        deathBanManager.reload();
        sender.sendMessage(ChatColor.GREEN + "Revive file (revives.yml) reloaded successfully!");
        return true;
    }
}