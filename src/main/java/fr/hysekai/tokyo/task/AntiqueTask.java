package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.antique.AntiqueStatus;
import fr.hysekai.tokyo.manager.AntiqueManager;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiqueTask extends BukkitRunnable {

    private final AntiqueManager manager;

    public AntiqueTask(AntiqueManager manager) {
        this.manager = manager;
    }

    private int ticks = 0;

    @Override
    public void run() {
        this.ticks += 1;
        if (ticks == 1) {
            manager.setStatus(AntiqueStatus.FIGHTING);
            UltraHardcoreAPI.getInstance().broadcast(ChatColor.GREEN + "Les membres de l'antique se sont réunis au café de l'antique. Pour les rejoindre, vous pouvez utiliser la commande /tg join");
        } else if (ticks == 10) {
            manager.setStatus(AntiqueStatus.FINISHED);
            manager.teleportBack();
            this.cancel();
        }
    }
}
