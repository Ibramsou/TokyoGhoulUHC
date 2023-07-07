package fr.hysekai.tokyo.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Velocity {

    public static void dash(Player player, double horizontal, double vertical) {
        EntityPlayer entity = ((CraftPlayer) player).getHandle();
        entity.motX = horizontal == 0 ? 0 : -Math.sin(entity.yaw * Math.PI / 180) * horizontal;
        entity.motY = vertical;
        entity.motZ = horizontal == 0 ? 0 : Math.cos(entity.yaw * Math.PI / 180) * horizontal;
        entity.velocityChanged = true;
    }
}
