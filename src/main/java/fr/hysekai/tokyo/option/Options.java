package fr.hysekai.tokyo.option;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Options {

    private int maxTeamSize = 6;
    private int roleTime = 20;
    private int antiqueTime = 60;
    private int formationTime = 15;
    private int rallierTime = 10;

    public void startTasks(TokyoGhoulPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            plugin.getRoleManager().setupRoles();
            plugin.registerTasks();
        }, this.roleTime * 1200L);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            plugin.getAntiqueManager().setReady(true);
            Player player = plugin.getRoleManager().getByType(RoleType.KUZEN);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Vous pouvez désormais utiliser la commande /tg antique pour vous réunir dans le café de l'antique.");
            }
        }, this.antiqueTime * 1200L);
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public void setRoleTime(int roleTime) {
        this.roleTime = roleTime;
    }

    public void setAntiqueTime(int antiqueTime) {
        this.antiqueTime = antiqueTime;
    }

    public void setFormationTime(int formationTime) {
        this.formationTime = formationTime;
    }

    public void setRallierTime(int rallierTime) {
        this.rallierTime = rallierTime;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public int getRoleTime() {
        return roleTime;
    }

    public int getAntiqueTime() {
        return antiqueTime;
    }

    public int getFormationTime() {
        return formationTime;
    }

    public int getRallierTime() {
        return rallierTime;
    }
}
