package fr.hysekai.tokyo.quinque.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.particle.ParticleBullet;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.task.BulletTask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Tetoro extends InteractiveItem {

    public Tetoro() {
        super(Material.DIAMOND_HOE, ChatColor.AQUA + "Tetoro");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action == InteractiveAction.LEFT_CLICK || action == InteractiveAction.ENTITY_CLICK || !role.canUseSkill(player, false)) return;

        Location location = player.getLocation();
        this.plugin.getEffectManager().broadcastSound(player.getWorld(), Sound.FIREWORK_LAUNCH, location.getX(), location.getY(), location.getZ(), 1f, 1f);

        ParticleBullet bullet = new ParticleBullet(player) {
            @Override
            public void hit() {
                Role targetRole = plugin.getRoleManager().getRole(target);
                if (targetRole == null || !targetRole.canAffect(player, target)) return;

                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, targetRole.isGhoul() ? 3 : 2));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    target.removePotionEffect(PotionEffectType.SLOW);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, targetRole.isGhoul() ? 200 : 100, 0));
                }, 40L);


                if (player.isOnline()) {
                    Location location = player.getLocation();
                    plugin.getEffectManager().sendSound(player, Sound.NOTE_PLING, location.getX(), location.getY(), location.getZ(), 1f, 1f);
                    player.sendMessage(ChatColor.GREEN + "Vous avez ralenti " + target.getName());
                }

                Location location = target.getLocation();
                plugin.getEffectManager().sendSound(target, Sound.ANVIL_LAND, location.getX(), location.getY(), location.getZ(), 1f, 1f);
                target.sendMessage(ChatColor.RED + player.getName() + " vous a ralenti.");
            }
        };

        new BulletTask(bullet).runTaskTimerAsynchronously(this.plugin, 0L, 1L);

        role.addSkillUse();
    }
}
