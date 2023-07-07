package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class GameTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;

    public GameTask(TokyoGhoulPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, Role> entry : this.plugin.getRoleManager().getRoleMap().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            Role role = entry.getValue();

            role.addTicks();
            role.onTick(this.plugin, player, role.ticks());
        }
    }
}
