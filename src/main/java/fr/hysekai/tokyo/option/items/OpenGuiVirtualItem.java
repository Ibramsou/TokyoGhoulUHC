package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.option.OptionGui;
import fr.hysekai.uhcapi.utils.VirtualItem;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class OpenGuiVirtualItem extends VirtualItem {

    private final AbstractGui parent;

    public OpenGuiVirtualItem(AbstractGui parent) {
        super(Material.NETHER_STALK);
        this.setDisplayName(ChatColor.RED + "Tokyo Ghoul Configuration");
        this.parent = parent;
    }

    @Override
    public void use(Player player) {
        new OptionGui(player, this.parent).open(player);
    }
}
