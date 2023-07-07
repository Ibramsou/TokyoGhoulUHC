package fr.hysekai.tokyo.quinque.craft;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.recipes.CraftingRecipe;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Human;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuinqueRecipe extends CraftingRecipe {

    public QuinqueRecipe() {
        super();
        this.shape("ABA", "CDC", "EEE");
        this.setIngredient('A', Material.GOLD_BLOCK);
        this.setIngredient('B', Material.REDSTONE_BLOCK);
        this.setIngredient('C', Material.DIAMOND);
        this.setIngredient('D', Material.GOLD_SWORD);
        this.setIngredient('E', Material.IRON_BLOCK);
    }

    @Override
    public ItemStack result(Player player) {
        Role role = TokyoGhoulPlugin.getInstance().getRoleManager().getRole(player);
        if (role == null || role.getType() != RoleType.INNOCENT || !role.isRallied()) return null;
        InteractiveItem quinqueItem = ((Human) role).getQuinque().getItem();
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR) continue;
            InteractiveItem interactiveItem = InteractiveItem.getItem(content);
            if (interactiveItem == quinqueItem) return null;
        }
        return quinqueItem;
    }
}
