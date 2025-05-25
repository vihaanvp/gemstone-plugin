package me.vihaanvp.gemstoneplugin.commands;

import me.vihaanvp.gemstoneplugin.utilities.DeathBanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeathBanCommand implements CommandExecutor {
    private final DeathBanManager deathBanManager;

    public DeathBanCommand(DeathBanManager deathBanManager) {
        this.deathBanManager = deathBanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /removedeathban <player>
        if (label.equalsIgnoreCase("removedeathban")) {
            if (!sender.hasPermission("gemstones.deathban.remove")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /removedeathban <player>");
                return true;
            }
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            boolean result = deathBanManager.removeDeathBan(target.getUniqueId());
            if (result) {
                sender.sendMessage(ChatColor.GREEN + "Removed deathban for " + targetName + ".");
            } else {
                sender.sendMessage(ChatColor.YELLOW + targetName + " was not deathbanned.");
            }
            return true;
        }

        // /deathban <player>
        if (label.equalsIgnoreCase("deathban")) {
            if (!sender.hasPermission("gemstones.deathban")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /deathban <player>");
                return true;
            }
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            if (deathBanManager.isDeathbanned(target.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + targetName + " is already deathbanned.");
                return true;
            }
            deathBanManager.applyDeathBan(target.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Deathbanned " + targetName + ".");
            Player online = Bukkit.getPlayerExact(targetName);
            if (online != null) {
                online.kickPlayer(ChatColor.RED + "You have been deathbanned by an administrator.");
            }
            return true;
        }

        // /uuid [player]
        if (label.equalsIgnoreCase("uuid")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    UUID uuid = ((Player) sender).getUniqueId();
                    sender.sendMessage(ChatColor.YELLOW + "Your UUID is: " + uuid);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /uuid <player>");
                }
            } else {
                String targetName = args[0];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                if (target == null || target.getUniqueId() == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + targetName + "'s UUID is: " + target.getUniqueId());
                }
            }
            return true;
        }

        return false;
    }
}