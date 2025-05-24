package me.vihaanvp.gemstoneplugin.crafting;

import me.vihaanvp.gemstoneplugin.listeners.GemstoneRecipeValidator;
import me.vihaanvp.gemstoneplugin.utilities.LootBoxUtils;
import me.vihaanvp.gemstoneplugin.utilities.RandomizerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class OtherRecipes {

    private static final GemstoneRecipeValidator validatorListener = new GemstoneRecipeValidator();

    public static void registerAllRecipes(Plugin plugin) {
        registerRandomizerRecipe(plugin);
        registerLootBoxRecipe(plugin);

        Bukkit.getPluginManager().registerEvents(validatorListener, plugin);
    }

    public static void registerRandomizerRecipe(Plugin plugin) {
        ItemStack randomizer = RandomizerUtils.createRandomizer();
        NamespacedKey key = new NamespacedKey(plugin, "gemstone_randomizer");
        ShapedRecipe recipe = new ShapedRecipe(key, randomizer);
        recipe.shape(
                "IEI",
                "EDE",
                "IEI"
        );
        recipe.setIngredient('E', Material.EMERALD_BLOCK);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        Bukkit.addRecipe(recipe);
        Bukkit.getPluginManager().registerEvents(new RandomizerUtils(), plugin);
    }

    public static void registerLootBoxRecipe(Plugin plugin) {
        ItemStack lootbox = LootBoxUtils.createLootBox();
        NamespacedKey key = new NamespacedKey(plugin, "gemstone_lootbox");
        ShapedRecipe recipe = new ShapedRecipe(key, lootbox);
        recipe.shape(
                "GDG",
                "DGD",
                "GDG"
        );
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        Bukkit.addRecipe(recipe);
    }
}