package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.role.type.antique.Kuzen;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OwlTask extends BukkitRunnable {

    private final Kuzen kuzen;
    private final UUID uuid;
    private int ticks;

    public OwlTask(Kuzen kuzen, UUID uuid) {
        this.kuzen = kuzen;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        if (this.ticks++ == 11) {
            MinecraftServer.getServer().postToMainThread(() -> {
                player.setMaxHealth(20);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.sendMessage(ChatColor.RED + "Vous venez de perdre votre forme de Chouette, vous devrez attendre le prochain épidose pour l'utiliser à nouveau.");
            });

            this.kuzen.setOwl(false);
            this.kuzen.getSkin().broadcastPacketsIncludeSelf(player);
            this.cancel();
        } else {
            double health = player.getHealth();
            if (health < player.getMaxHealth()) {
                player.setHealth(Math.min(player.getMaxHealth(), health + 1));
            }
        }
    }
}
