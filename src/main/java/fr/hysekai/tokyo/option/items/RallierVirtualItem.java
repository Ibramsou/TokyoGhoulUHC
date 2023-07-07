package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.option.OptionItem;
import fr.hysekai.tokyo.skin.Skin;
import org.bukkit.ChatColor;

public class RallierVirtualItem extends OptionItem {

    public RallierVirtualItem(int slot) {
        super(Skin.KUZEN.toSkullItem(), slot);
        this.setDisplayName(ChatColor.DARK_AQUA + "Temps de ralliement d'une ghoul");
    }

    @Override
    protected String lore() {
        return this.formatValue(this.value(), "minute");
    }

    @Override
    protected int value() {
        return this.options.getRallierTime();
    }

    @Override
    protected int min() {
        return 1;
    }

    @Override
    protected int max() {
        return 20;
    }

    @Override
    public int shiftChange() {
        return 5;
    }

    @Override
    protected void change(int change) {
        this.options.setRallierTime(change);
    }
}
