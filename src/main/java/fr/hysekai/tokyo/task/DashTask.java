package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.util.Distances;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DashTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;
    private final Player player;
    private int ticks;

    public DashTask(TokyoGhoulPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) return;

        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        this.plugin.getEffectManager().broadcastColored(player.getWorld(), ep.locX, ep.locY, ep.locZ, 255, 0, 0);

        if (ticks++ > 1 && (ep.onGround || ep.getWorld().getWorld().getBlockAt((int) ep.locX, (int) ep.locY, (int) ep.locZ).isLiquid())) {
            Location location = player.getLocation();
            Vector vector = location.toVector();

            double x = ep.locX;
            double y = ep.locY + 1;
            double z = ep.locZ;

            Vec3D vec3D = new Vec3D(x, y, z);
            boolean touched = false;

            for (EntityHuman human : ep.world.players) {
                EntityPlayer entityplayer = (EntityPlayer) human;
                if (entityplayer == ep) continue;

                Role role = this.plugin.getRoleManager().getRole(entityplayer.getUniqueID());
                if (role == null || role instanceof Antique) continue;

                Player target = entityplayer.getBukkitEntity();
                double angle = Distances.yawPosToAngle(ep.yaw, ep.locX, ep.locZ, entityplayer.locX, entityplayer.locZ);
                double distance = Math.sqrt(entityplayer.h(ep));

                if (angle >= 90 && distance <= 30) {
                    if (entityplayer.world.rayTrace(vec3D, new Vec3D(entityplayer.locX, entityplayer.locY + 1, entityplayer.locZ)) != null) continue;

                    Vector direction = location.setDirection(target.getLocation().toVector().subtract(vector)).getDirection();
                    double directionX = direction.getX();
                    double directionY = direction.getY();
                    double directionZ = direction.getZ();
                    for (double i = 0; i < distance; i += 0.25) {
                        this.plugin.getEffectManager().broadcastColored(player.getWorld(), x + directionX * i, y + directionY * i, z + directionZ * i, 255, 0, 0);
                    }

                    this.plugin.getEffectManager().sendSound(target, Sound.ANVIL_LAND, entityplayer.locX, entityplayer.locY, entityplayer.locZ, 1f, 1f);
                    this.plugin.getEffectManager().safeDamage(target, 4.0D);
                    player.sendMessage(ChatColor.GREEN + "Vous avez touché " + target.getName());
                    target.sendMessage(ChatColor.RED + player.getName() + " vous a touché.");

                    touched = true;
                }
            }

            if (touched) this.plugin.getEffectManager().sendSound(player, Sound.NOTE_PLING, ep.locX, ep.locY, ep.locZ, 1f, 1f);
            MinecraftServer.getServer().postToMainThread(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1)));
            this.cancel();
        }
    }
}
