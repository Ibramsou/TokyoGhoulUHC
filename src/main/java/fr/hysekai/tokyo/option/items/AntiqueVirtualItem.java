package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.option.OptionItem;
import fr.hysekai.tokyo.skin.Skin;
import org.bukkit.ChatColor;

public class AntiqueVirtualItem extends OptionItem {

    public AntiqueVirtualItem(int slot) {
        super(Skin.COFFE_HEAD.toSkullItem(), slot);
        this.setDisplayName(ChatColor.DARK_AQUA + "Temps pour se réunir dans le café");
    }

    @Override
    protected String lore() {
        int time = this.value();
        if (time < 60) {
            return this.formatValue(time, "minute");
        }
        int hour = time / 60;
        int minute = time % 60;
        return hour + "h" + (minute > 9 ? minute : "0" + minute);
    }

    @Override
    protected int value() {
        return this.options.getAntiqueTime();
    }

    @Override
    protected int min() {
        return TokyoGhoulPlugin.getInstance().getOptions().getRoleTime();
    }

    @Override
    protected int max() {
        return 105;
    }

    @Override
    public int shiftChange() {
        return 10;
    }

    @Override
    protected void change(int change) {
        this.options.setAntiqueTime(change);
    }
}
