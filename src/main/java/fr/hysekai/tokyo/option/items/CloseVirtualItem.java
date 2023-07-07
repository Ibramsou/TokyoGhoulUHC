package fr.hysekai.tokyo.option.items;

import fr.hysekai.uhcapi.utils.VirtualItem;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CloseVirtualItem extends VirtualItem {

    private final AbstractGui parent;

    public CloseVirtualItem(AbstractGui parent) {
        super(Material.BARRIER);
        this.parent = parent;
        this.setDisplayName(ChatColor.RED + "Retour au menu");
    }

    @Override
    public void use(Player player) {
        parent.open(player);
    }
}
