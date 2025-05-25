package me.vihaanvp.gemstoneplugin;

import me.vihaanvp.gemstoneplugin.crafting.*;
import me.vihaanvp.gemstoneplugin.gemstones.*;
import me.vihaanvp.gemstoneplugin.listeners.*;
import me.vihaanvp.gemstoneplugin.commands.*;
import me.vihaanvp.gemstoneplugin.utilities.DeathBanManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public class GemstonePlugin extends JavaPlugin {
    DeathBanManager deathBanManager = new DeathBanManager(this);

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getLogger().warning("Data Folder not found!");
            getLogger().info("Creating data folder...");
            getDataFolder().mkdirs();
            getLogger().info("Data folder created!");
        } else {
            getLogger().info("Data folder found!");
        }

        // Register listeners
        getPluginManager().registerEvents(new Blazite(), this);
        getPluginManager().registerEvents(new Aquaryte(), this);
        getPluginManager().registerEvents(new Terranox(), this);
        getPluginManager().registerEvents(new Voltaryn(), this);
        getPluginManager().registerEvents(new Noctyra(), this);
        getPluginManager().registerEvents(new LootBoxListener(), this);
        getPluginManager().registerEvents(new BoundGemstoneListener(this), this);
        getPluginManager().registerEvents(new AnvilCombineListener(this), this);
        getPluginManager().registerEvents(new BoundGemstoneTokenListener(this), this);
        getPluginManager().registerEvents(new BoundGemstoneProtectionListener(this), this);
        getPluginManager().registerEvents(new ReviveBookListener(deathBanManager), this);
        getPluginManager().registerEvents(new SuperGemstoneListener(this), this);

        DeathDebuffListener deathDebuffListener = new DeathDebuffListener(this);
        getPluginManager().registerEvents(deathDebuffListener, this);

        getPluginManager().registerEvents(deathBanManager, this);

        getLogger().info("Registered Gemstones and Listeners Successfully!");

        // Register commands
        getCommand("givegem").setExecutor(new GiveGemCommand(this));
        getCommand("giverandomizer").setExecutor(new GiveRandomizerCommand(this));
        getCommand("givelootbox").setExecutor(new GiveLootBoxCommand(this));
        getCommand("deathdebuff").setExecutor(new DeathDebuffCommand(deathDebuffListener.getDebuffedPlayers(), this));
        getCommand("givebinder").setExecutor(new GiveBinderCommand(this));
        getCommand("giverevivebook").setExecutor(new GiveReviveBookCommand(this));
        getCommand("reloadrevivefile").setExecutor(new ReloadReviveFileCommand(deathBanManager));
        getCommand("removedeathban").setExecutor(new DeathBanCommand(deathBanManager));
        getCommand("deathban").setExecutor(new DeathBanCommand(deathBanManager));
        getLogger().info("Registered Commands Successfully!");

        // Register crafting recipes
        OtherRecipes.registerAllRecipes(this);
        GemstoneRecipes.registerAllGemRecipes(this);
        getLogger().info("Registered Crafting Recipes Successfully!");

        getLogger().info("Gemstone Plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Gemstone Plugin disabled.");
    }
}