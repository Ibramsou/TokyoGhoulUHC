package fr.hysekai.tokyo.quinque.craft;

import fr.hysekai.uhcapi.game.items.GlassPanelVirtualItem;
import fr.hysekai.uhcapi.utils.VirtualItem;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class QuinqueGui extends AbstractGui {

    public QuinqueGui() {
        super(ChatColor.GREEN + "Crafter une Quinque", 5);
    }

    @Override
    public void registerItems() {
        for (int i = 0; i < 45; i++) {
            this.addItems(i, new GlassPanelVirtualItem(15));
        }

        VirtualItem gold_block = new VirtualItem(Material.GOLD_BLOCK);
        VirtualItem redstone_block = new VirtualItem(Material.REDSTONE_BLOCK);
        VirtualItem diamond = new VirtualItem(Material.DIAMOND);
        VirtualItem gold_sword = new VirtualItem(Material.GOLD_SWORD);
        VirtualItem iron_block = new VirtualItem(Material.IRON_BLOCK);

        this.addItems(12, gold_block);
        this.addItems(13, redstone_block);
        this.addItems(14, gold_block);
        this.addItems(21, diamond);
        this.addItems(22, gold_sword);
        this.addItems(23, diamond);
        this.addItems(30, iron_block);
        this.addItems(31, iron_block);
        this.addItems(32, iron_block);
    }
}
