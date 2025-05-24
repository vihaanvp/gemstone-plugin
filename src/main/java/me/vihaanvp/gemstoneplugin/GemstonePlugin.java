package me.vihaanvp.gemstoneplugin;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import me.vihaanvp.gemstoneplugin.listeners.*;
import me.vihaanvp.gemstoneplugin.utilities.*;
import me.vihaanvp.gemstoneplugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GemstonePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register listeners
        Bukkit.getPluginManager().registerEvents(new Blazite(), this);
        Bukkit.getPluginManager().registerEvents(new Aquaryte(), this);
        Bukkit.getPluginManager().registerEvents(new Terranox(), this);
        Bukkit.getPluginManager().registerEvents(new Voltaryn(), this);
        Bukkit.getPluginManager().registerEvents(new Noctyra(), this);
        Bukkit.getPluginManager().registerEvents(new LootBoxListener(), this);
        DeathDebuffListener deathDebuffListener = new DeathDebuffListener(this);
        Bukkit.getPluginManager().registerEvents(deathDebuffListener, this);
        getLogger().info("Registered Gemstones and Listeners Successfully!");

        // Register commands
        getCommand("givegem").setExecutor(new GiveGemCommand());
        getCommand("giverandomizer").setExecutor(new GiveRandomizerCommand());
        getCommand("givelootbox").setExecutor(new GiveLootBoxCommand(this));
        getCommand("deathdebuff").setExecutor(new DeathDebuffCommand(deathDebuffListener.getDebuffedPlayers(), this));
        getLogger().info("Registered Commands Successfully!");

        // Register crafting recipes
        CraftingUtils.registerAllRecipes(this);
        getLogger().info("Registered Crafting Recipes Successfully!");

        getLogger().info("Gemstone Plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Gemstone Plugin disabled.");
    }
}