package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.particle.ParticleBullet;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BulletTask extends BukkitRunnable {

    private final ParticleBullet bullet;

    public BulletTask(ParticleBullet bullet) {
        this.bullet = bullet;
    }

    @Override
    public void run() {
        for (int i = 0; i < 8; i++) {
            if (this.bullet.isDead()) {
                Player target = this.bullet.getTarget();
                if (target != null) MinecraftServer.getServer().postToMainThread(this.bullet::hit);
                this.cancel();
                return;
            }

            this.bullet.tick();
        }
    }
}
