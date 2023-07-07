package fr.hysekai.tokyo.item;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.uhcapi.utils.VirtualItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InteractiveItem extends VirtualItem {

    public long lastInteract;

    protected final TokyoGhoulPlugin plugin = TokyoGhoulPlugin.getInstance();

    public InteractiveItem(Material material, String name) {
        super(material, 1, name);

        ItemMeta meta = this.getItemMeta();
        meta.spigot().setUnbreakable(true);
        this.setItemMeta(meta);

        playerItemsMap.put(material, this);
    }

    public abstract void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event);

    public abstract void onClick(Role role, Player player, Player clicked, InteractiveAction action);

    public static InteractiveItem getItem(ItemStack itemStack) {
        if (!VirtualItem.playerItemsMap.containsKey(itemStack.getType())) return null;

        for (VirtualItem item : VirtualItem.playerItemsMap.values()) {
            if ((!(item instanceof InteractiveItem))) continue;

            if (item.equals(itemStack)) {
                return (InteractiveItem) item;
            }
        }

        return null;
    }
}
