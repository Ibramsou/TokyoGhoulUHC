package fr.hysekai.tokyo.recipes;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftShapedRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CraftingRecipe extends CraftShapedRecipe {

    private static final ItemStack empty = new ItemStack(Material.AIR);

    public CraftingRecipe() {
        super(empty);
    }

    public Material getMaterial() {
        return null;
    }

    @Override
    public ItemStack getResult() {
        return null;
    }

    public abstract ItemStack result(Player player);
}
