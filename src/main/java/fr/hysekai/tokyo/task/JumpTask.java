package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.type.antique.Enji;
import fr.hysekai.tokyo.util.Velocity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class JumpTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;
    private final Enji enji;
    private final Player player;

    public JumpTask(TokyoGhoulPlugin plugin, Enji enji, Player player) {
        this.plugin = plugin;
        this.enji = enji;
        this.player = player;

        Location location = player.getLocation();
        this.plugin.getEffectManager().broadcastSound(player.getWorld(), Sound.ENDERDRAGON_WINGS, location.getX(), location.getY(), location.getZ(), 1f, 1f);
        Velocity.dash(player, 2.5F, 0.65);
    }

    @Override
    public void run() {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (!player.isOnline() || ep.onGround || ep.getWorld().getWorld().getBlockAt((int) ep.locX, (int) ep.locY, (int) ep.locZ).isLiquid()) {
            this.cancel();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                enji.setCanJump(true);
                Player player = Bukkit.getPlayer(this.player.getUniqueId());
                if (player != null) {
                    player.setAllowFlight(true);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.GREEN + "Vous pouvez de nouveau utiliser votre double saut.");
                }
            }, 12000L);
            return;
        }

        this.plugin.getEffectManager().broadcastParticle(player.getWorld(), EnumParticle.FLAME, ep.locX, ep.locY, ep.locZ);
    }
}
