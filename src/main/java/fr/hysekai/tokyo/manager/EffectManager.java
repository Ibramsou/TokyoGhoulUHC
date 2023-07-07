package fr.hysekai.tokyo.manager;

import fr.hysekai.tokyo.entity.CustomLightning;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class EffectManager {

    public void broadcastColored(World world, Predicate<Player> predicate, double x, double y, double z, double r, double g, double b) {
        PacketPlayOutWorldParticles packet = this.buildParticle(EnumParticle.REDSTONE, x, y, z, r, g, b);
        this.broadcastPacket(world, packet, predicate);
    }

    public void broadcastColored(World world, double x, double y, double z, double r, double g, double b) {
        PacketPlayOutWorldParticles packet = this.buildParticle(EnumParticle.REDSTONE, x, y, z, r, g, b);
        this.broadcastPacket(world, packet, null);
    }

    public void broadcastParticle(World world, Predicate<Player> predicate, EnumParticle particle, double x, double y, double z) {
        PacketPlayOutWorldParticles packet = this.buildParticle(particle, x, y, z, 0, 0, 0);
        broadcastPacket(world, packet, predicate);
    }

    public void broadcastParticle(World world, EnumParticle particle, double x, double y, double z) {
        PacketPlayOutWorldParticles packet = this.buildParticle(particle, x, y, z, 0, 0, 0);
        broadcastPacket(world, packet, null);
    }

    public void sendColored(Player player, double x, double y, double z, double r, double g, double b) {
        PacketPlayOutWorldParticles packet = this.buildParticle(EnumParticle.REDSTONE, x, y, z, r, g, b);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.buildParticle(EnumParticle.REDSTONE, x, y, z, r, g, b));
    }

    public void sendParticle(Player player, EnumParticle particle, double x, double y, double z) {
        PacketPlayOutWorldParticles packet = this.buildParticle(particle, x, y, z, 0, 0, 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void broadcastSound(World world, Predicate<Player> predicate, Sound sound, double x, double y, double z, float volume, float pitch) {
        PacketPlayOutNamedSoundEffect packet = this.buildSound(sound, x, y, z, volume, pitch);
        this.broadcastPacket(world, packet, predicate);
    }

    public void broadcastSound(World world, Sound sound, double x, double y, double z, float volume, float pitch) {
        PacketPlayOutNamedSoundEffect packet = this.buildSound(sound, x, y, z, volume, pitch);
        this.broadcastPacket(world, packet, null);
    }

    public void sendSound(Player player, Sound sound, double x, double y, double z, float volume, float pitch) {
        PacketPlayOutNamedSoundEffect packet = this.buildSound(sound, x, y, z, volume, pitch);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private void broadcastPacket(World world, Packet<?> packet, Predicate<Player> predicate) {
        for (Player player : world.getPlayers()) {
            if (predicate == null || predicate.test(player)) ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public CustomLightning spawnLightning(World world, double x, double y, double z, double damage) {
        CustomLightning entity = new CustomLightning(world, x, y, z, damage);
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entity);
        this.broadcastPacket(world, packet, null);
        return entity;
    }

    public PacketPlayOutNamedSoundEffect buildSound(Sound sound, double x, double y, double z, float volume, float pitch) {
        return new PacketPlayOutNamedSoundEffect(CraftSound.getSound(sound), x, y, z, volume, pitch);
    }

    public void selfDamage(Player player, double damage) {
        double health = player.getHealth() - damage;
        if (health <= 0) {
            boolean mainThread = Bukkit.isPrimaryThread();
            if (mainThread) {
                player.damage(damage);
            } else {
                MinecraftServer.getServer().postToMainThread(() -> player.damage(damage));
            }
            return;
        }
        player.setHealth(health);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.sendPacket(new PacketPlayOutEntityStatus(ep, (byte) 2));
    }

    public void safeDamage(Player player, double damage) {
        boolean mainThread = Bukkit.isPrimaryThread();
        double health = player.getHealth() - damage;
        if (health > 0 || mainThread) {
            player.damage(damage);
        } else {
            MinecraftServer.getServer().postToMainThread(() -> player.damage(damage));
        }
    }

    private PacketPlayOutWorldParticles buildParticle(EnumParticle particle, double x, double y, double z, double r, double g, double b) {
        r = r == 0 ? 0 : r / 255F;
        g = g == 0 ? 0 : g / 255F;
        b = b == 0 ? 0 : b / 255F;
        return new PacketPlayOutWorldParticles(particle, true, (float) x, (float) y, (float) z, (float) r, (float) g, (float) b, 1F, 0);
    }
}
