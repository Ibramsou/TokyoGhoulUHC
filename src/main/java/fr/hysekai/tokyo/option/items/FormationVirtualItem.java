package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.option.OptionItem;
import fr.hysekai.tokyo.skin.Skin;
import org.bukkit.ChatColor;

public class FormationVirtualItem extends OptionItem {

    public FormationVirtualItem(int slot) {
        super(Skin.COLOMBE_HEAD.toSkullItem(), slot);
        this.setDisplayName(ChatColor.DARK_AQUA + "Temps de formation d'un humain");
    }

    @Override
    protected String lore() {
        return this.formatValue(this.value(), "minute");
    }

    @Override
    protected int value() {
        return this.options.getFormationTime();
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
        this.options.setFormationTime(change);
    }
}
