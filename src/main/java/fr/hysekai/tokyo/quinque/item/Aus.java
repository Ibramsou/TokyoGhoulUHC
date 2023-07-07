package fr.hysekai.tokyo.quinque.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.kagune.Kagune;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Ghoul;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Aus extends InteractiveItem {

    public Aus() {
        super(Material.GOLD_HOE, ChatColor.GREEN + "Aus");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {
        if (damagedRole != null) {
            if (!attackerRole.canUseSkill(attacker, true) || damagedRole.getType() == RoleType.SAVAGE_GHOUL || damagedRole.isRevealed()) return;

            Ghoul ghoul = (Ghoul) damagedRole;
            if (ghoul.isDamaged()) return;
            boolean isResistance = ghoul.getKagune() == Kagune.RESISTANCE;
            if (isResistance || ghoul.getKagune() == Kagune.VITALITY) {
                if (isResistance) {
                    damaged.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                } else {
                    damaged.setMaxHealth(20);
                }

                

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Player player = Bukkit.getPlayer(damaged.getUniqueId());
                    if (player == null) return;

                    if (isResistance) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, ghoul.haveActiveTask() ? 1 : 0));
                    } else {
                        player.setMaxHealth(30);
                    }

                    ghoul.setDamager(null);
                }, 200L);

                ghoul.setDamager(attacker);

                attacker.sendMessage(ChatColor.YELLOW + "Vous avez retiré " + (isResistance ? " l'armure " : "la vitalité") + " de " + damaged.getName() + " pendant 10 secondes.");
                damaged.sendMessage(ChatColor.RED + damaged.getName() + " vous a retiré votre " + (isResistance ? "vitalité" : "resistance") + " pendant 10 secondes.");
                Location location = attacker.getLocation();
                Location targetLoc = damaged.getLocation();
                this.plugin.getEffectManager().sendSound(attacker, Sound.NOTE_PLING, location.getX(), location.getY(), location.getZ(), 1f, 1f);
                this.plugin.getEffectManager().sendSound(damaged, Sound.ANVIL_LAND, targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 1f, 1f);
            }
        }
    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {

    }
}
