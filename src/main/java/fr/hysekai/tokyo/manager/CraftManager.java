package fr.hysekai.tokyo.manager;

import com.google.common.collect.Maps;
import fr.hysekai.tokyo.recipes.CraftingRecipe;
import fr.hysekai.tokyo.recipes.nms.CustomShapedRecipe;
import fr.hysekai.tokyo.util.Reflection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.lang.reflect.Field;
import java.util.*;

public class CraftManager extends CraftingManager {

    private boolean callCraftEvents = true;

    public CraftManager() {
        super();

        Field field = Reflection.accessField(CraftingManager.class, "a");
        Reflection.set(null, field, this, true); // Overwrite instance
    }

    public void addRecipes(CraftingRecipe... recipes) {
        for (CraftingRecipe recipe : recipes) {
            this.addRecipe(recipe);
        }
    }

    @SuppressWarnings("deprecation")
    private void addRecipe(CraftingRecipe recipe) {
        Object[] data;
        String[] shape = recipe.getShape();
        Map<Character, org.bukkit.inventory.ItemStack> ingredientMap = recipe.getIngredientMap();
        int datalen = shape.length;
        datalen += ingredientMap.size() * 2;
        int i = 0;
        data = new Object[datalen];
        for (; i < shape.length; i++) {
            data[i] = shape[i];
        }
        for (char c : ingredientMap.keySet()) {
            org.bukkit.inventory.ItemStack itemData = ingredientMap.get(c);
            if (itemData == null) continue;
            data[i] = c;
            i++;
            int id = itemData.getTypeId();
            short dmg = itemData.getDurability();
            data[i] = new net.minecraft.server.v1_8_R3.ItemStack(CraftMagicNumbers.getItem(id), 1, dmg);
            i++;
        }

        StringBuilder s = new StringBuilder();
        int index = 0;
        int width = 0;
        int height = 0;


        if (data[index] instanceof String[]) {
            String[] strings = (String[]) data[index++];

            for (String string : strings) {
                ++height;
                width = string.length();
                s.append(string);
            }
        } else {
            while (data[index] instanceof String) {
                String string = (String) data[index++];

                ++height;
                width = string.length();
                s.append(string);
            }
        }

        Map<Character, ItemStack> hashmap;

        for (hashmap = Maps.newHashMap(); index < data.length; index += 2) {
            Character character = (Character) data[index];
            ItemStack item = null;

            if (data[index + 1] instanceof Item) {
                item = new ItemStack((Item) data[index + 1]);
            } else if (data[index + 1] instanceof Block) {
                item = new ItemStack((Block) data[index + 1], 1, 32767);
            } else if (data[index + 1] instanceof ItemStack) {
                item = (ItemStack) data[index + 1];
            }

            hashmap.put(character, item);
        }

        ItemStack[] stacks = new ItemStack[width * height];

        for (int i1 = 0; i1 < width * height; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(c0)) {
                stacks[i1] = hashmap.get(c0).cloneItemStack();
            } else {
                stacks[i1] = null;
            }
        }

        CustomShapedRecipe customRecipe = new CustomShapedRecipe(recipe, width, height, stacks);
        this.recipes.add(customRecipe);
    }

    @Override
    public ItemStack craft(InventoryCrafting crafting, World world) {
        Iterator<IRecipe> iterator = this.recipes.iterator();

        IRecipe recipe;

        do {
            if (!iterator.hasNext()) {
                crafting.currentRecipe = null;
                return null;
            }

            recipe = iterator.next();
        } while (!recipe.a(crafting, world));

        ItemStack result = null;

        crafting.currentRecipe = recipe;
        if (recipe instanceof CustomShapedRecipe) {
            CustomShapedRecipe customRecipe = (CustomShapedRecipe) recipe;
            Player player = (Player) crafting.getOwner();
            org.bukkit.inventory.ItemStack item = customRecipe.getRecipe().result(player);
            if (item != null) result = CraftItemStack.asNMSCopy(item);
        } else {
            result = recipe.craftItem(crafting);
            if (this.callCraftEvents) {
                CraftInventoryCrafting inventory = new CraftInventoryCrafting(crafting, crafting.resultInventory);
                inventory.setResult(CraftItemStack.asCraftMirror(result));
                PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, false);
                Bukkit.getPluginManager().callEvent(event);
                result = CraftItemStack.asNMSCopy(event.getInventory().getResult());
            }
        }

        return result;
    }

    public void enableCraftEvents(boolean callCraftEvents) {
        this.callCraftEvents = callCraftEvents;
    }
}
