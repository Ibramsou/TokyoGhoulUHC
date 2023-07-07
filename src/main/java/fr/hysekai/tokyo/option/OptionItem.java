package fr.hysekai.tokyo.option;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.uhcapi.utils.VirtualItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class OptionItem extends VirtualItem {

    private final int slot;
    protected final Options options;

    public OptionItem(Material material, int slot) {
        this(new ItemStack(material), slot);
    }

    public OptionItem(ItemStack itemStack, int slot) {
        super(itemStack);
        this.slot = slot;
        this.options = TokyoGhoulPlugin.getInstance().getOptions();
        this.setLore(ChatColor.GRAY + this.lore());
    }

    @Override
    public void use(Player player) {
        int add = this.value() + 1;
        if (add > this.max() || add < this.min()) return;
        this.updateOption(player, add);
    }

    @Override
    public void useRight(Player player) {
        int remove = this.value() - 1;
        if (remove > this.max() || remove < this.min()) return;
        this.updateOption(player, remove);
    }

    public void useShiftLeft(Player player) {
        int add = Math.min(this.max(), this.value() + this.shiftChange());
        this.updateOption(player, add);
    }

    public void useShiftRight(Player player) {
        int remove = Math.max(this.min(), this.value() - this.shiftChange());
        this.updateOption(player, remove);
    }

    public int getSlot() {
        return slot;
    }

    private void updateOption(Player player, int change) {
        this.change(change);
        Location location = player.getLocation();
        TokyoGhoulPlugin.getInstance().getEffectManager().sendSound(player, Sound.STEP_GRAVEL, location.getX(), location.getY(), location.getZ(), 1f, 1f);
        this.setLore(ChatColor.GRAY + this.lore());
        player.getOpenInventory().setItem(this.slot, this);
        player.updateInventory();
    }

    protected String formatValue(int value, String message) {
        return value + " " + message +  (value > 1 ? "s" : "");
    }

    protected abstract String lore();

    protected abstract int value();

    protected abstract int min();

    protected abstract int max();

    public abstract int shiftChange();

    protected abstract void change(int change);
}
