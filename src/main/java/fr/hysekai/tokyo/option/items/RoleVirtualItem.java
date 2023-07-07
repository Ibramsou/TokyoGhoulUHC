package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.option.OptionItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RoleVirtualItem extends OptionItem {

    private final AntiqueVirtualItem antiqueItem;
    private final Player player;

    public RoleVirtualItem(int slot, Player player, AntiqueVirtualItem item) {
        super(Material.PAPER, slot);

        this.player = player;
        this.antiqueItem = item;
        this.setDisplayName(ChatColor.DARK_AQUA + "Temps d'annonce des roles");
    }

    @Override
    protected String lore() {
        return this.formatValue(this.value(), "minute");
    }

    @Override
    protected int value() {
        return this.options.getRoleTime();
    }

    @Override
    protected int min() {
        return 1;
    }

    @Override
    protected int max() {
        return 40;
    }

    @Override
    public int shiftChange() {
        return 10;
    }

    @Override
    protected void change(int change) {
        this.options.setRoleTime(change);
        if (change > this.options.getAntiqueTime()) {
            this.options.setAntiqueTime(change);
            this.antiqueItem.setLore(ChatColor.GRAY + antiqueItem.lore());
            this.player.getOpenInventory().setItem(this.antiqueItem.getSlot(), this.antiqueItem);
        }
    }
}
