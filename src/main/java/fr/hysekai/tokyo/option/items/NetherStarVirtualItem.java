package fr.hysekai.tokyo.option.items;

import fr.hysekai.uhcapi.utils.VirtualItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class NetherStarVirtualItem extends VirtualItem {

    public NetherStarVirtualItem() {
        super(Material.NETHER_STAR);

        this.setDisplayName(ChatColor.GREEN + "Mode de Jeu: " + ChatColor.RED + "Tokyo Ghoul");
    }
}
