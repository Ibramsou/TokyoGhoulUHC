package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.type.Ghoul;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ResistanceTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;
    private final UUID uuid;
    private final double[][] particleCoords = new double[81][];
    private final int totalTicks = particleCoords.length;
    private final long start = System.currentTimeMillis();
    private final Ghoul ghoul;
    private int ticks;
    private double lastX, lastZ;

    public ResistanceTask(TokyoGhoulPlugin plugin, UUID uuid, Ghoul ghoul) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.ghoul = ghoul;
        this.ghoul.setActiveTask(this);
        double radius = 0.6;
        int count = 0;
        for (double y = 0; y < 2; y += 0.025) {
            double x = radius * Math.cos(y * 6);
            double z = radius * Math.sin(y * 6);
            particleCoords[count] = new double[] {x, y, z};
            count++;
        }
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            this.cancel();
            return;
        }

        boolean damaged = ghoul.isDamaged();

        long end = 1000L * 30;
        if (System.currentTimeMillis() - start >= end) {
            MinecraftServer.getServer().postToMainThread(() -> {
                if (!damaged) {
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 0));
                }
                player.sendMessage(ChatColor.RED + "Votre protection vous a été enlevé");
            });
            this.cancel();
            this.ghoul.setActiveTask(null);
            return;
        }

        if (damaged) return;

        Location location = player.getLocation();
        boolean hasMoved = (lastX != 0 && lastZ != 0) && (location.getX() != lastX || location.getZ() != lastZ);
        this.lastX = location.getX();
        this.lastZ = location.getZ();
        if (hasMoved) {
            this.plugin.getEffectManager().broadcastColored(location.getWorld(), location.getX(), location.getY() + 2, location.getZ(), 8, 71, 75);
            return;
        }


        if (ticks == totalTicks) ticks = 0;
        double[] array = particleCoords[ticks];
        location.setX(location.getX() + array[0]);
        location.setY(location.getY() + array[1]);
        location.setZ(location.getZ() + array[2]);
        this.plugin.getEffectManager().broadcastColored(location.getWorld(), location.getX(), location.getY(), location.getZ(), 8, 71, 75);
        ticks++;
    }
}
