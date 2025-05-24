package me.vihaanvp.gemstoneplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class GemstoneRecipeValidator implements Listener {
    private final Map<String, Predicate<ItemStack[]>> validators = new HashMap<>();

    /**
     * Registers a custom validator for a given recipe key.
     * @param key Recipe key name (e.g. "blazite")
     * @param validator Predicate that returns true if the matrix is valid, false otherwise
     */
    public void registerValidator(String key, Predicate<ItemStack[]> validator) {
        validators.put(key, validator);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof ShapedRecipe shapedRecipe)) return;

        String key = shapedRecipe.getKey().getKey();
        Predicate<ItemStack[]> validator = validators.get(key);
        if (validator != null) {
            ItemStack[] matrix = event.getInventory().getMatrix();
            if (!validator.test(matrix)) {
                event.getInventory().setResult(null);
            }
        }
    }
}