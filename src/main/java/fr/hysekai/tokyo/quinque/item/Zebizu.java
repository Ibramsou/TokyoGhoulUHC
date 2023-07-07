package fr.hysekai.tokyo.quinque.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.util.Distances;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class Zebizu extends InteractiveItem {

    public Zebizu() {
        super(Material.GOLD_HOE, ChatColor.RED + "Zebizu");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {}

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action == InteractiveAction.LEFT_CLICK || !role.canUseSkill(player, false)) return;
        Player target = action == InteractiveAction.ENTITY_CLICK ? clicked : action == InteractiveAction.RIGHT_CLICK ? Distances.rayTrace(player, 30.0D) : null;
        if (target == null) return;
        Role targetRole = this.plugin.getRoleManager().getRole(target);
        if (targetRole == null || !targetRole.canAffect(player, target)) return;
        Location location = player.getLocation();
        Vector direction = location.getDirection();
        double x = location.getX();
        double y = location.getY() + 1.25;
        double z = location.getZ();

        double distance = Distances.distance(player, target);

        for (double i = 0.5; i < distance; i += 0.25) {
            this.plugin.getEffectManager().broadcastColored(player.getWorld(), x + direction.getX() * i, y + direction.getY() * i, z + direction.getZ() * i, 1, 1, 1);
        }

        Location targetLoc = target.getLocation();
        this.plugin.getEffectManager().broadcastParticle(player.getWorld(), EnumParticle.EXPLOSION_HUGE, targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
        player.getWorld().createExplosion(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 4, false, true);

        player.sendMessage(ChatColor.GREEN + "Vous avez explosé " + target.getName() + ".");
        player.sendMessage(ChatColor.RED + player.getName() + " vous a explosé.");
        role.addSkillUse();
    }
}
