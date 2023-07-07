package fr.hysekai.tokyo.quinque.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.util.Distances;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class Narukami extends InteractiveItem {

    public Narukami() {
        super(Material.GOLD_HOE, ChatColor.GOLD + "Narukami");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

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
            this.plugin.getEffectManager().broadcastColored(player.getWorld(), x + direction.getX() * i, y + direction.getY() * i, z + direction.getZ() * i, 157, 157, 157);
        }

        Location targetLoc = target.getLocation();
        this.plugin.getEffectManager().spawnLightning(player.getWorld(), targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 0);
        target.damage(targetRole.isGhoul() ? 4 : 2);

        player.sendMessage(ChatColor.GREEN + "Vous avez fait apparaitre un éclair sur " + target.getName());
        target.sendMessage(ChatColor.RED + player.getName() + " a fait apparaitre un éclair sur vous");

        role.addSkillUse();
    }
}
