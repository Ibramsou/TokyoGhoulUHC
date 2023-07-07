package fr.hysekai.tokyo.quinque.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.tokyo.util.Distances;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SilverCrane extends InteractiveItem {

    public SilverCrane() {
        super(Material.IRON_HOE, ChatColor.GRAY + "Crâne d'Argent");
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

        Location location = target.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();

        int startX = x - 4;
        int startZ = z - 4;
        int endX = x + 4;
        int endZ = z + 4;
        int startY = y - 1;
        int endY = y + 4;

        Location pLoc = player.getLocation();
        boolean inArea = Distances.inArea(pLoc, startX, startY, startZ, endX, endY, endZ);
        List<int[]> blocks = inArea ? new ArrayList<>(405) : null;

        for (int locY = startY; locY < endY; locY++) {
            for (int locX = startX; locX <= endX; locX++) {
                for (int locZ = startZ; locZ <= endZ; locZ++) {
                    double dist = Math.sqrt(NumberConversions.square(x - locX) + NumberConversions.square(z - locZ));
                    Block block = world.getBlockAt(locX, locY, locZ);

                    if (locY == y - 1 && !block.isEmpty() && block.getType().isSolid())
                        continue;
                    Material material = locY == y - 1 ||
                            locY == y + 3 ||
                            (locX == x && locZ == z && locY == y + 1 && targetRole.isGhoul()) ||
                            dist >= 4 ||
                            (dist >= 3 && ThreadLocalRandom.current().nextInt(4) == 2)
                            ? Material.WEB : Material.AIR;
                    block.setType(material);
                    if (blocks != null) blocks.add(new int[] {locX, locY, locZ});
                }
            }
        }

        if (blocks != null) {
            Human human = (Human) role;
            human.addBlocks(blocks);
            human.setArea(startX, startY, startZ, endX, endY, endZ);
        }

        Vector direction = pLoc.getDirection();
        double playerX = pLoc.getX();
        double playerY = pLoc.getY() + 1.25;
        double playerZ = pLoc.getZ();

        double distance = Distances.distance(player, target);

        for (double i = 0.5; i < distance; i += 0.25) {
            this.plugin.getEffectManager().broadcastColored(player.getWorld(), playerX + direction.getX() * i, playerY + direction.getY() * i, playerZ + direction.getZ() * i, 255, 255, 255);
        }

        this.plugin.getEffectManager().sendSound(player, Sound.NOTE_PLING, playerX, playerY, playerZ, 1f, 1f);
        this.plugin.getEffectManager().sendSound(target, Sound.ANVIL_LAND, x, y, z, 1f, 1f);
        player.sendMessage(ChatColor.GREEN + "Vous avez enfermé " + target.getName());
        target.sendMessage(ChatColor.RED + player.getName() + " vous a enfermé");

        role.addSkillUse();
    }
}
