package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.kagune.item.Dash;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.util.Distances;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Toka extends Antique {

    private int nearestTicks;

    public Toka() {
        super(RoleType.TOKA);
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new Dash()
        };
    }

    @Override
    public void onTick(TokyoGhoulPlugin plugin, Player player, long ticks) {
        super.onTick(plugin, player, ticks);
        boolean nearest = false;
        for (Player target : player.getWorld().getPlayers()) {
            if (target == player) continue;

            if (Distances.distance(target, player) <= 15) {
                nearest = true;
                break;
            }
        }

        if (nearest && this.nearestTicks++ % 300 == 0 && this.nearestTicks > 1) {
            player.sendMessage(ChatColor.RED + "Attention, une colombe est proche de vous.");
        }
    }

    @Override
    public String[] information() {
        return null;
    }
}
