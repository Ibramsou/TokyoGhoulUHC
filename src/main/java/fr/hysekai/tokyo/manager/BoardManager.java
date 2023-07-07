package fr.hysekai.tokyo.manager;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleTab;
import fr.hysekai.tokyo.tab.PlayerTab;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class BoardManager {

    private final TokyoGhoulPlugin plugin;

    private final Scoreboard cachedBoard;
    private final ScoreboardObjective cachedObjective;
    private final Packet<?> createObjective, removeObjective, createDisplay;

    public BoardManager(TokyoGhoulPlugin plugin) {
        this.cachedBoard = new Scoreboard();
        this.plugin = plugin;

        this.cachedObjective = new ScoreboardObjective(this.cachedBoard, "tab", IScoreboardCriteria.g);
        this.createObjective = new PacketPlayOutScoreboardObjective(this.cachedObjective, 0);
        this.removeObjective = new PacketPlayOutScoreboardObjective(this.cachedObjective, 1);
        this.createDisplay = new PacketPlayOutScoreboardDisplayObjective(0, this.cachedObjective);
    }

    public void refreshTeams(Player player) {
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null) return;
        RoleTab tab = role.getTab();
        if (tab == null) return;
        tab.refreshTeams();
    }

    public void updateTags(PlayerTab targetTab, Player target) {
        targetTab.checkBoard();
        for (Map.Entry<UUID, Role> entry : this.plugin.getRoleManager().getRoleMap().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            Role role = entry.getValue();
            if (role.getTab() == null) continue;
            role.getTab().checkBoard();
            role.getTab().addPlayer(target);
            role.getTab().updatePlayer(target);
            targetTab.addPlayer(player);
            targetTab.updatePlayer(player);
        }
    }

    public void removeTags(PlayerTab targetTab, Player target) {
        targetTab.removeBoard();
        for (Map.Entry<UUID, Role> entry : this.plugin.getRoleManager().getRoleMap().entrySet()) {
            if (entry.getKey().equals(targetTab.getPlayer().getUniqueId())) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            Role role = entry.getValue();
            if (role.getTab() == null) continue;
            role.getTab().removePlayer(target);
        }
    }

    public void updateHealths(PlayerTab targetTab, Player target, double health) {
        targetTab.updateHealth(target, health);
        for (Map.Entry<UUID, Role> entry : this.plugin.getRoleManager().getRoleMap().entrySet()) {
            if (entry.getKey().equals(targetTab.getPlayer().getUniqueId())) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            Role role = entry.getValue();
            if (role.getTab() == null) continue;
            role.getTab().updateHealth(target, health);
        }
    }

    public void createObjective(EntityPlayer target) {
        target.playerConnection.sendPacket(this.createObjective);
    }

    public void removeObjective(EntityPlayer target) {
        target.playerConnection.sendPacket(this.removeObjective);
    }

    public void createDisplay(EntityPlayer target) {
        target.playerConnection.sendPacket(this.createDisplay);
    }

    public ScoreboardObjective getCachedObjective() {
        return this.cachedObjective;
    }

    public Scoreboard getCachedBoard() {
        return this.cachedBoard;
    }
}
