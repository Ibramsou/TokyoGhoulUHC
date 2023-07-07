package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.util.Velocity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrenzyTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;
    private final Player player;
    private final Map<Double, List<Location>> particleMap = new HashMap<>();
    private final List<Player> damagedPlayers = new ArrayList<>();
    private double ticks = 1;
    private boolean showParticles, sentVelocityBack;

    private double startX, startZ;
    private int startY;
    private Block blockLiquid;

    public FrenzyTask(TokyoGhoulPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Velocity.dash(player, 0, 1.25);
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.cancel();
            return;
        }

        EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();

        double motY = entityplayer.motY;
        double y = entityplayer.locY;

        if (showParticles) {
            this.sendParticles();
        } else {

            if (!sentVelocityBack && motY < 0.5) {
                sentVelocityBack = true;
                Velocity.dash(player, 0, -2);
            } else if (sentVelocityBack) {
                boolean onGround = entityplayer.onGround;
                boolean liquid = false;

                Location location = player.getLocation();
                if (this.blockLiquid == null) {
                    for (int i = 0; i < 4; i++) {
                        Block block = player.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - i, location.getBlockZ());
                        if (block.isLiquid()) this.blockLiquid = block;
                    }
                } else if (y <= this.blockLiquid.getY() + 1) {
                    liquid = true;
                    location.setY(this.blockLiquid.getY() + 1);
                }

                if (onGround || liquid) {
                    this.showParticles = true;
                    this.plugin.getEffectManager().broadcastSound(player.getWorld(), Sound.EXPLODE, location.getX(), location.getY(), location.getZ(), 1f, 1f);
                    this.startX = Math.floor(location.getX()) + 0.5;
                    this.startY = location.getBlockY();
                    this.startZ = Math.floor(location.getZ()) + 0.5;
                    this.calculateParticles();
                    this.sendParticles();
                }
            } else if (player.getLocation().getBlock().isLiquid()) {
                Velocity.dash(player, 0, 1.2);
            }
        }
    }

    private void calculateParticles() {
        World world = player.getWorld();

        for (double count = 1; count < 6; count += 0.5) {
            List<Location> list = new ArrayList<>(128);
            for (double d0 = startX - count; d0 <= startX + count; d0 += 0.5) {
                for (double d1 = startZ - count; d1 <= startZ + count; d1 += 0.5) {
                    double dist = Math.sqrt(NumberConversions.square(startX - d0) + NumberConversions.square(startZ - d1));
                    if (dist <= count && dist >= count - 1) {
                        list.add(new Location(world, d0, this.startY, d1));
                    }
                }
            }

            particleMap.put(count, list);
        }
    }

    private void sendParticles() {
        World world = this.player.getWorld();
        List<Location> locations = this.particleMap.remove(this.ticks);
        if (locations == null) {
            this.cancel();
            return;
        }
        locations.forEach(location -> this.plugin.getEffectManager().broadcastParticle(location.getWorld(), EnumParticle.REDSTONE, location.getX(), location.getY(), location.getZ()));
        double particleDistance = Math.sqrt(NumberConversions.square(startX - (startX - this.ticks)) + NumberConversions.square(startZ - (startZ - this.ticks))) - 1;
        Bukkit.getOnlinePlayers().forEach(target -> {
            if (target.getWorld() != world || target == this.player || this.damagedPlayers.contains(target)) return;
            Location playerLoc = target.getLocation();
            double distX = playerLoc.getX() - this.startX;
            double distZ = playerLoc.getZ() - this.startZ;
            double distanceHor = Math.sqrt(NumberConversions.square(distX) + NumberConversions.square(distZ));
            double distanceVer = Math.max(this.startY, playerLoc.getY()) - Math.min(this.startY, playerLoc.getY());
            if (distanceHor <= particleDistance && distanceVer <= 1) {
                this.plugin.getEffectManager().safeDamage(target, 4.0D);
                this.damagedPlayers.add(target);
                EntityPlayer entityplayer = ((CraftPlayer) target).getHandle();
                entityplayer.motX = distX / distanceHor * 0.2;
                entityplayer.motY = 0.4;
                entityplayer.motZ = distZ / distanceHor * 0.2;
                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entityplayer));
            }
        });

        this.ticks += 0.5;
        if (ticks == 6) {
            this.cancel();
        }
    }
}
