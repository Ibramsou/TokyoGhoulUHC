package fr.hysekai.tokyo.manager;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.antique.AntiqueStatus;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.task.AntiqueTask;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.team.AbstractTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AntiqueManager {

    private final TokyoGhoulPlugin plugin;
    private final World world;
    private final Location spawnLocation;
    private final Location[] locations;
    private final Map<UUID, Location> savedLocations = new HashMap<>(42, 1);

    private AntiqueStatus status;
    private boolean ready;
    private boolean locked;
    private boolean canLock = true;
    private int availableLocations = 7;

    public AntiqueManager(TokyoGhoulPlugin plugin) {
        this.plugin = plugin;
        this.world = Bukkit.getWorld("world_the_end");
        this.spawnLocation = new Location(this.world, 0, 64, 0);
        this.locations = new Location[] {
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0),
                this.buildLocation(0, 0, 0, 0, 0)
        };
    }

    private Location buildLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(this.world, x, y, z, yaw, pitch);
    }

    public void teleportAntique(Player player) {
        this.savedLocations.put(player.getUniqueId(), player.getLocation());
        player.teleport(this.spawnLocation);
        player.sendMessage(ChatColor.GREEN + "Vous avez été téléporté au café de l'antique");
        String message = ChatColor.GREEN + "Le Kuzen vous a téléporté au café de l'antique pour une réunion stratégique.";
        for (Map.Entry<UUID, Role> entry : this.plugin.getRoleManager().getRoleMap().entrySet()) {
            Role role = entry.getValue();
            if (role.getType() == RoleType.KUZEN) continue;
            if (role.isDead() || role.isDisconnected()) continue;
            if (role instanceof Antique) {
                Player target = Bukkit.getPlayer(entry.getKey());
                if (target == null) continue;
                this.savedLocations.put(target.getUniqueId(), target.getLocation());
                target.teleport(this.spawnLocation);
                target.sendMessage(message);
            }
        }

        this.status = AntiqueStatus.STARTED;

        new AntiqueTask(this).runTaskTimer(this.plugin,20 * 60, 20 * 60);
    }

    public void addPlayer(Role role, Player player) {
        Location location = this.getRandomLocation();
        if (location == null) {
            player.sendMessage(ChatColor.RED + "Toutes les posistions ont été prises par d'autres équipes/joueurs");
            return;
        }
        if (role.getType() == RoleType.COLOMBE) {
            AbstractTeam team = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getPlayerTeam(UltraHardcoreAPI.getInstance().getGameManager().getParticipantManager().getParticipant(player));
            String message = ChatColor.GREEN + "La colombe de votre équipe vous a téléporté au café de l'antique";
            team.getMembers().forEach(participantPlayer -> {
                Player target = participantPlayer.getBukkitPlayer().orElse(null);
                if (target == null) return;
                Role targetRole = this.plugin.getRoleManager().getRole(target);
                if (targetRole == null || targetRole.getType() == RoleType.SAVAGE_GHOUL && targetRole.isRevealed()) return;
                this.savedLocations.put(target.getUniqueId(), location);
                target.teleport(location);

                if (target == player) {
                    target.sendMessage(ChatColor.GREEN + "Vous avez téléporté votre équipe au café de l'antique");
                } else {
                    target.sendMessage(message);
                }
            });
        } else {
            player.sendMessage(ChatColor.GREEN + "Vous avez été téléporté au café de l'antique");
        }
        player.teleport(location);
        this.savedLocations.put(player.getUniqueId(), player.getLocation());
        role.setChangedDimension(true);
    }

    public void checkPlayer(Player player) {
        Location location = this.savedLocations.remove(player.getUniqueId());
        if (location != null) player.teleport(location);
    }

    public Location getLocationBack(Player player) {
        return this.savedLocations.remove(player.getUniqueId());
    }

    public void teleportBack() {
        Set<UUID> toRemove = new HashSet<>();
        for (Map.Entry<UUID, Location> entry : this.savedLocations.entrySet()) {
            UUID uuid = entry.getKey();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            Location location = entry.getValue();
            player.teleport(location);
            toRemove.add(uuid);
        }

        toRemove.forEach(this.savedLocations::remove);
    }

    public Location getRandomLocation() {
        if (this.availableLocations == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(7);
        Location location = locations[index];
        if (location == null) {
            return this.getRandomLocation();
        }
        this.availableLocations -= 1;
        locations[index] = null;
        return location;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean canLock() {
        return canLock;
    }

    public void setLocked() {
        this.locked = true;
        this.canLock = false;
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.locked = false;
            UltraHardcoreAPI.getInstance().broadcast(ChatColor.GREEN + "La dimension du café de l'antique n'est plus bloquée, vous pouvez désormais en sortir.");
        }, 1200L);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setStatus(AntiqueStatus status) {
        this.status = status;
    }

    public AntiqueStatus getStatus() {
        return status;
    }
}
