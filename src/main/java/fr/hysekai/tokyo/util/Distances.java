package fr.hysekai.tokyo.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Distances {

    private static final int maxValue = Integer.MAX_VALUE;

    public static List<Player> listPlayers(Player player, double distanceH, double distanceV) {
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(target -> {
            if (target == player) return;
            if (distanceHorizontal(player, target) <= distanceH && distanceVertical(player, target) <= distanceV) players.add(target);
        });

        return players;
    }

    public static double distance(Player playerA, Player playerB) {
        if (playerA.getWorld() != playerB.getWorld()) return maxValue;
        Location locationA = playerA.getLocation();
        Location locationB = playerB.getLocation();
        double distX = locationA.getX() - locationB.getX();
        double distY = locationA.getY() - locationB.getY();
        double distZ = locationA.getZ() - locationB.getZ();
        return Math.sqrt(NumberConversions.square(distX) + NumberConversions.square(distY) + NumberConversions.square(distZ));
    }

    public static double distanceHorizontal(Player playerA, Player playerB) {
        if (playerA.getWorld() != playerB.getWorld()) return maxValue;
        Location locationA = playerA.getLocation();
        Location locationB = playerB.getLocation();
        double distX = locationA.getX() - locationB.getX();
        double distZ = locationA.getZ() - locationB.getZ();
        return Math.sqrt(NumberConversions.square(distX) + NumberConversions.square(distZ));
    }

    public static double distanceVertical(Player playerA, Player playerB) {
        if (playerA.getWorld() != playerB.getWorld()) return maxValue;
        Location locationA = playerA.getLocation();
        Location locationB = playerB.getLocation();
        return Math.max(locationA.getY(), locationB.getY()) - Math.min(locationA.getY(), locationB.getY());
    }

    public static boolean isInRange(Player playerA, Player playerB, double range) {
        return distance(playerA, playerB) < range;
    }

    public static boolean isInVerticalRange(Player playerA, Player playerB, double range) {
        return distanceVertical(playerA, playerB) < range;
    }

    public static boolean isInHorizontalRange(Player playerA, Player playerB, double range) {
        return distanceHorizontal(playerA, playerB) < range;
    }

    public static Player rayTrace(Player player, double range) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double motX = direction.getX() * range;
        double motY = direction.getY() * range;
        double motZ = direction.getZ() * range;
        Vec3D start = new Vec3D(x, y, z);
        Vec3D end = new Vec3D(x + motX, y + motY, z + motZ);
        EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        List<net.minecraft.server.v1_8_R3.Entity> entities = world.getEntities(entityplayer, entityplayer.getBoundingBox().a(motX, motY, motZ));
        double nearestDistance = 0;
        Entity result = null;
        for (Entity entity : entities) {
            if (entity == entityplayer) continue;
            AxisAlignedBB axis = entity.getBoundingBox().grow(0.0f, 0.0f, 0.0f);
            MovingObjectPosition objectPosition = axis.a(start, end);
            if (objectPosition == null) continue;
            double distance = start.distanceSquared(objectPosition.pos);
            if (distance < nearestDistance || nearestDistance == 0) {
                result = entity;
                nearestDistance = distance;
            }
        }

        if (!(result instanceof EntityPlayer)) return null;
        EntityPlayer target = (EntityPlayer) result;
        double distance = Math.sqrt(target.h(entityplayer));
        MovingObjectPosition objectPosition = world.rayTrace(start, new Vec3D(x + direction.getX() * distance, y + direction.getY() * distance, z + direction.getZ() * distance));
        return objectPosition != null ? null : target.getBukkitEntity();
    }

    public static boolean inArea(Location location, double startX, double startY, double startZ, double endX, double endY ,double endZ) {
        return inArea(location, startX, startZ, endX, endZ) && inArea(location, startY, endY);
    }

    public static boolean inArea(Location location, double startX, double startZ, double endX, double endZ) {
        double minX = Math.min(startX, endX);
        double minZ = Math.min(startZ, endZ);
        double maxX = Math.max(startX, endX);
        double maxZ = Math.max(startZ, endZ);
        return location.getX() >= minX && location.getZ() >= minZ && location.getX() <= maxX && location.getZ() <= maxZ;
    }

    public static boolean inArea(Location location, double startY, double endY) {
        return location.getY() <= Math.max(startY, endY) && location.getY() >= Math.min(startY, endY);
    }

    public static double yawPosToAngle(double yaw, double playerX, double playerZ, double targetX, double targetZ) {
        double directionX = -Math.sin(Math.toRadians(yaw));
        double directionZ = Math.cos(Math.toRadians(yaw));

        double x = targetX - playerX;
        double z = targetZ - playerZ;

        double magnitude = Math.sqrt(x * x + z * z);

        x /= magnitude;
        z /= magnitude;

        return Math.toDegrees(Math.acos(x * directionX + z * directionZ));
    }
}
