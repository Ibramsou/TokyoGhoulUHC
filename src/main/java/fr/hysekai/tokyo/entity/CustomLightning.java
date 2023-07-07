package fr.hysekai.tokyo.entity;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

public class CustomLightning extends EntityLightning {

    private final double damage;
    private final int viewDistance;

    private Player owner;

    public CustomLightning(World world, double x, double y, double z, double damage) {
        super(((CraftWorld) world).getHandle(), x, y, z, true);

        this.viewDistance = this.world.getServer().getViewDistance() * 16;
        this.damage = damage;
    }

    public CustomLightning setOwner(Player player) {
        this.owner = player;
        return this;
    }

    public void strike() {
        PacketPlayOutNamedSoundEffect effect = TokyoGhoulPlugin.getInstance().getEffectManager().buildSound(Sound.AMBIENCE_THUNDER, this.locX, this.locY, this.locZ, 10000F, 0.8F);
        for (EntityHuman entity : this.world.players) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            double distance = Math.sqrt(this.h(entity));
            if (distance > this.viewDistance) continue;
            Player player = entityplayer.getBukkitEntity();

            if (this.damage > 0 && player != this.owner && distance <= 1.5) {
                TokyoGhoulPlugin.getInstance().getEffectManager().safeDamage(player, this.damage);
            }

            entityplayer.playerConnection.sendPacket(effect);
        }
    }

    public void t_() { // Empty
    }
}
