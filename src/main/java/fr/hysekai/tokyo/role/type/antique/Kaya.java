package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kaya extends Antique {

    public Kaya() {
        super(RoleType.KAYA);
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new SpeedItem()
        };
    }

    @Override
    public boolean canAffect(Player player, Player target) {
        Role role = TokyoGhoulPlugin.getInstance().getRoleManager().getRole(target);
        if (role == null || Math.random() * 100 <= 66) {
            if (player.isOnline()) player.sendMessage(ChatColor.RED + target.getName() + " a esquivé votre attaque.");
            return false;
        }

        return true;
    }

    @Override
    public String[] information() {
        return null;
    }

    private class SpeedItem extends InteractiveItem {

        public SpeedItem() {
            super(Material.CARROT_ITEM, ChatColor.GREEN + "Gain de Vitesse");
        }

        @Override
        public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

        }

        @Override
        public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
            if (action != InteractiveAction.RIGHT_CLICK || !Kaya.this.canUseSkill(player, true)) return;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
            player.sendMessage(ChatColor.GREEN + "Vous avez reçu un effet de vitesse 2 pour une durée de 20 secondes.");
            Location location = player.getLocation();
            TokyoGhoulPlugin.getInstance().getEffectManager().sendSound(player, Sound.NOTE_PLING, location.getX(), location.getY(), location.getZ(), 1f, 1f);
        }
    }
}
