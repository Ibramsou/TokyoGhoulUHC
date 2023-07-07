package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Enji extends Antique {

    private boolean canJump = true;

    public Enji() {
        super(RoleType.ENJI);
    }

    public boolean canJump() {
        return this.canJump;
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new StrengthItem()
        };
    }

    @Override
    public void connect(Player player) {
        if (this.setup && this.canJump) {
            player.setAllowFlight(true);
        }

        super.connect(player);
    }

    @Override
    public void setup(Player player) {
        player.setAllowFlight(true);
        player.setFlying(false);
        super.setup(player);
    }

    @Override
    public String[] information() {
        return null;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    private class StrengthItem extends InteractiveItem {

        public StrengthItem() {
            super(Material.NETHER_STALK, ChatColor.DARK_RED + "Gain de Force");
        }

        @Override
        public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

        }

        @Override
        public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
            if (action != InteractiveAction.RIGHT_CLICK || !Enji.this.canUseSkill(player, true)) return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2400, 0));
            player.sendMessage(ChatColor.GREEN + "Vous avez reçu un effet de force pendant 2 minutes.");
        }
    }
}
