package me.vihaanvp.gemstoneplugin.crafting;

import me.vihaanvp.gemstoneplugin.gemstones.*;
import me.vihaanvp.gemstoneplugin.listeners.GemstoneRecipeValidator;
import me.vihaanvp.gemstoneplugin.utilities.RandomizerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class GemstoneRecipes {

    private static final GemstoneRecipeValidator validatorListener = new GemstoneRecipeValidator();

    public static void registerAllGemRecipes(Plugin plugin) {
        registerBlaziteRecipe(plugin);
        registerAquaryteRecipe(plugin);
        registerTerranoxRecipe(plugin);
        registerVoltarynRecipe(plugin);
        registerNoctyraRecipe(plugin);

        Bukkit.getPluginManager().registerEvents(validatorListener, plugin);
    }

    // --- Gemstone Recipes ---

    public static void registerBlaziteRecipe(Plugin plugin) {
        ItemStack blazite = Blazite.createItem();
        NamespacedKey key = new NamespacedKey(plugin, "blazite");
        ShapedRecipe recipe = new ShapedRecipe(key, blazite);
        recipe.shape (
                "RBR",
                "BRB",
                "RBR"
        );
        recipe.setIngredient('R', Material.NETHER_STAR); // Gemstone Randomizer
        recipe.setIngredient('B', Material.BLAZE_ROD);
        Bukkit.addRecipe(recipe);

        validatorListener.registerValidator("blazite", matrix -> {
            int[] selectorSlots = {0, 2, 4, 6, 8};
            for (int i : selectorSlots) {
                if (!RandomizerUtils.isRandomizer(matrix[i])) return false;
            }
            return true;
        });
    }

    public static void registerAquaryteRecipe(Plugin plugin) {
        ItemStack aquaryte = Aquaryte.createItem();
        NamespacedKey key = new NamespacedKey(plugin, "aquaryte");
        ShapedRecipe recipe = new ShapedRecipe(key, aquaryte);
        recipe.shape (
                "RHR",
                "HRH",
                "RHR"
        );
        recipe.setIngredient('R', Material.NETHER_STAR); // Gemstone Randomizer
        recipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        Bukkit.addRecipe(recipe);

        validatorListener.registerValidator("aquaryte", matrix -> {
            int[] selectorSlots = {0, 2, 4, 6, 8};
            for (int i : selectorSlots) {
                if (!RandomizerUtils.isRandomizer(matrix[i])) return false;
            }
            return true;
        });
    }

    public static void registerTerranoxRecipe(Plugin plugin) {
        ItemStack terranox = Terranox.createItem();
        NamespacedKey key = new NamespacedKey(plugin, "terranox");
        ShapedRecipe recipe = new ShapedRecipe(key, terranox);
        recipe.shape (
                "RER",
                "ERE",
                "RER"
        );
        recipe.setIngredient('R', Material.NETHER_STAR); // Gemstone Randomizer
        recipe.setIngredient('E', Material.EMERALD_BLOCK);
        Bukkit.addRecipe(recipe);

        validatorListener.registerValidator("terranox", matrix -> {
            int[] selectorSlots = {0, 2, 4, 6, 8};
            for (int i : selectorSlots) {
                if (!RandomizerUtils.isRandomizer(matrix[i])) return false;
            }
            return true;
        });
    }

    public static void registerVoltarynRecipe(Plugin plugin) {
        ItemStack voltaryn = Voltaryn.createItem();
        NamespacedKey key = new NamespacedKey(plugin, "voltaryn");
        ShapedRecipe recipe = new ShapedRecipe(key, voltaryn);
        recipe.shape (
                "RDR",
                "LRD",
                "RLR"
        );
        recipe.setIngredient('R', Material.NETHER_STAR); // Gemstone Randomizer
        recipe.setIngredient('D', Material.REDSTONE_BLOCK);
        recipe.setIngredient('L', Material.LIGHTNING_ROD);
        Bukkit.addRecipe(recipe);

        validatorListener.registerValidator("voltaryn", matrix -> {
            int[] selectorSlots = {0, 2, 4, 6, 8};
            for (int i : selectorSlots) {
                if (!RandomizerUtils.isRandomizer(matrix[i])) return false;
            }
            return true;
        });
    }

    public static void registerNoctyraRecipe(Plugin plugin) {
        ItemStack noctyra = Noctyra.createItem();
        NamespacedKey key = new NamespacedKey(plugin, "noctyra");
        ShapedRecipe recipe = new ShapedRecipe(key, noctyra);
        recipe.shape (
                "ROR",
                "FRO",
                "RGR"
        );
        recipe.setIngredient('R', Material.NETHER_STAR); // Gemstone Randomizer
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('F', Material.FERMENTED_SPIDER_EYE);
        recipe.setIngredient('G', Material.GOLDEN_CARROT);
        Bukkit.addRecipe(recipe);

        validatorListener.registerValidator("noctyra", matrix -> {
            int[] selectorSlots = {0, 2, 4, 6, 8};
            for (int i : selectorSlots) {
                if (!RandomizerUtils.isRandomizer(matrix[i])) return false;
            }
            return true;
        });
    }
}