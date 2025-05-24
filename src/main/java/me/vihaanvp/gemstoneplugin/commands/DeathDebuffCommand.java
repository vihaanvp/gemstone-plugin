package me.vihaanvp.gemstoneplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class DeathDebuffCommand implements CommandExecutor {
    private final Set<UUID> debuffedPlayers;
    private final Plugin plugin;
    private static final double DEBUFFED_HEALTH = 12.0; // 6 hearts

    public DeathDebuffCommand(Set<UUID> debuffedPlayers, Plugin plugin) {
        this.debuffedPlayers = debuffedPlayers;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /deathdebuff <remove|apply> [player]");
            return true;
        }
        String action = args[0].toLowerCase();

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
        UUID uuid = target.getUniqueId();

        if (action.equals("remove")) {
            if (debuffedPlayers.contains(uuid)) {
                debuffedPlayers.remove(uuid);
                // Restore max health
                target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                if (target.getHealth() > 20.0) target.setHealth(20.0);
                target.sendMessage(ChatColor.GREEN + "Your death debuff has been removed!");
                sender.sendMessage(ChatColor.GREEN + "Removed death debuff from " + target.getName());
            } else {
                sender.sendMessage(ChatColor.YELLOW + target.getName() + " does not have the death debuff.");
            }
        } else if (action.equals("apply")) {
            if (!debuffedPlayers.contains(uuid)) {
                debuffedPlayers.add(uuid);
                // Set max health to 6 hearts
                target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(DEBUFFED_HEALTH);
                if (target.getHealth() > DEBUFFED_HEALTH) target.setHealth(DEBUFFED_HEALTH);
                target.sendMessage(ChatColor.RED + "You have been given the death debuff: 6 hearts and PvP protection!");
                sender.sendMessage(ChatColor.GREEN + "Applied death debuff to " + target.getName());
            } else {
                sender.sendMessage(ChatColor.YELLOW + target.getName() + " already has the death debuff.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /deathdebuff <remove|apply> [player]");
        }
        return true;
    }

    public Set<UUID> getDebuffedPlayers() {
        return debuffedPlayers;
    }
}