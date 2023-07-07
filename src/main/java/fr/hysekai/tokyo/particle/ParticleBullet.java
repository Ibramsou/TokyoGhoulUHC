package fr.hysekai.tokyo.particle;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class ParticleBullet {

    private final Location location;
    private final EntityPlayer shooter;
    private final double directionX, directionY, directionZ;
    private final WorldServer world;
    private final Vec3D start;

    protected Player target;
    private boolean dead;


    public ParticleBullet(Player shooter) {
        Location location = shooter.getEyeLocation();
        this.shooter = ((CraftPlayer) shooter).getHandle();

        this.location = location;

        Vector direction = location.getDirection();
        this.directionX = direction.getX() / 2;
        this.directionY = direction.getY() / 2;
        this.directionZ = direction.getZ() / 2;

        this.world = ((CraftWorld) location.getWorld()).getHandle();

        this.start = new Vec3D(this.location.getX(), this.location.getY(), this.location.getZ());
    }

    public final void tick() {
        this.location.add(this.directionX, this.directionY, this.directionZ);

        double x = this.location.getX();
        double y = this.location.getY();
        double z = this.location.getZ();

        if (this.distanceSquared() >= 30 || world.rayTrace(start, new Vec3D(x, y, z)) != null) {
            this.dead = true;
            return;
        }

        Vec3D vec3D = new Vec3D(x, y, z);

        TokyoGhoulPlugin.getInstance().getEffectManager().broadcastColored(this.world.getWorld(), x, y - 0.5, z, 123, 15, 206);

        double rangeHor = 0.3f;
        double rangeVer = 1.0F;
        AxisAlignedBB axis = new AxisAlignedBB(x - rangeHor, y - rangeVer, z - rangeHor, x + rangeHor, y + rangeVer, z + rangeHor);
        List<Entity> entities = world.a((Entity) null, axis, null);

        double nearestDistance = 0;
        Entity target = null;
        for (Entity entity : entities) {
            if (entity == this.shooter) continue;
            axis = entity.getBoundingBox().grow(1.0f, 1.0f, 1.0f);
            MovingObjectPosition objectPosition = axis.a(start, vec3D);
            if (objectPosition == null) continue;
            double distance = start.distanceSquared(objectPosition.pos);
            if (distance < nearestDistance || nearestDistance == 0) {
                target = entity;
                nearestDistance = distance;
            }
        }

        if (target instanceof EntityPlayer) {
            this.dead = true;
            this.target = ((EntityPlayer) target).getBukkitEntity();
        }
    }

    private double distanceSquared() {
        double distX = this.start.a - this.location.getX();
        double distY = this.start.b - this.location.getY();
        double distZ = this.start.c - this.location.getZ();
        return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    public boolean isDead() {
        return this.dead;
    }

    public Player getTarget() {
        return this.target;
    }

    public abstract void hit();
}
