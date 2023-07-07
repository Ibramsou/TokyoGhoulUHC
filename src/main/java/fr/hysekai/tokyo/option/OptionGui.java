package fr.hysekai.tokyo.option;

import fr.hysekai.tokyo.option.items.*;
import fr.hysekai.uhcapi.game.items.GlassPanelVirtualItem;
import fr.hysekai.uhcapi.utils.VirtualItem;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OptionGui extends AbstractGui {

    private static final int[] positions = new int[] {0, 1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44};

    private final AbstractGui parent;
    private final Player player;

    public OptionGui(Player player, AbstractGui parent) {
        super(ChatColor.RED + "Configuration", 5);

        this.player = player;
        this.parent = parent;
    }

    @Override
    public void registerItems() {
        VirtualItem glassItem = new GlassPanelVirtualItem(15);
        for (int pos : positions) {
            this.addItems(pos, glassItem);
        }
        AntiqueVirtualItem antiqueItem = new AntiqueVirtualItem(29);
        this.addItems(4, new NetherStarVirtualItem());
        this.addItems(20, new MaxTeamsVirtualItem(20));
        this.addItems(22, new RoleVirtualItem(22, this.player, antiqueItem));
        this.addItems(24, new FormationVirtualItem(24));
        this.addItems(29, antiqueItem);
        this.addItems(33, new RallierVirtualItem(33));
        this.addItems(40, new CloseVirtualItem(parent));
    }
}
