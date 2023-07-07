package fr.hysekai.tokyo.recipes.nms;

import fr.hysekai.tokyo.recipes.CraftingRecipe;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.ShapedRecipes;

public class CustomShapedRecipe extends ShapedRecipes {

    private final CraftingRecipe recipe;

    public CustomShapedRecipe(CraftingRecipe recipe, int width, int height, ItemStack[] data) {
        super(width, height, data, null);

        this.recipe = recipe;
    }

    public CraftingRecipe getRecipe() {
        return this.recipe;
    }
}