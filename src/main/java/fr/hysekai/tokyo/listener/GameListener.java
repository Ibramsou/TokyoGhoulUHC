package fr.hysekai.tokyo.listener;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.role.type.Ghoul;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.event.game.ChangeStateEvent;
import fr.hysekai.uhcapi.event.game.GameStartEvent;
import fr.hysekai.uhcapi.event.game.SwitchEpisodeEvent;
import fr.hysekai.uhcapi.event.player.GamePlayerDeathEvent;
import fr.hysekai.uhcapi.game.GameState;
import fr.hysekai.uhcapi.game.players.ParticipantPlayer;
import fr.hysekai.uhcapi.game.team.MultiTeam;
import fr.hysekai.uhcapi.manager.GameManager;
import fr.hysekai.uhcapi.utils.VirtualItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameListener implements Listener {

    private final TokyoGhoulPlugin plugin;

    public GameListener(TokyoGhoulPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (TokyoGhoulPlugin.testMode) return;
        for (MultiTeam team : event.getGameManager().getTeamManager().getTeams()) {
            if (team.getMembers().size() < 4) {
                UltraHardcoreAPI.getInstance().broadcast(ChatColor.RED + "Il faut au moins 4 joueurs par team pour que la partie se lance.");
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onStateChange(ChangeStateEvent event) {
        if (event.getGameState() == GameState.PLAYING) {
            this.plugin.getRoleManager().assignRoles();
            this.plugin.getOptions().startTasks(this.plugin);
        }
    }

    @EventHandler
    public void onEpisodeSwitch(SwitchEpisodeEvent event) {
        int episode = event.getEpisode();

        this.plugin.getRoleManager().getRoleMap().forEach((uuid, role) -> {
            if (role.isGhoul()) role.switchEpisode(episode);
        });
    }

    @EventHandler
    public void onPlayerDeath(GamePlayerDeathEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;

        PlayerDeathEvent deathEvent = event.getEvent();

        Player player = deathEvent.getEntity();

        ParticipantPlayer victim = event.getVictim();
        GameManager manager = UltraHardcoreAPI.getInstance().getGameManager();
        MultiTeam team = (MultiTeam) manager.getTeamManager().getPlayerTeam(victim);
        if (team == null) return;
        Role role = this.plugin.getRoleManager().getRole(victim.getUniqueId());
        if (role == null) return;
        UltraHardcoreAPI.getInstance().broadcast(ChatColor.GREEN + Bukkit.getOfflinePlayer(victim.getUniqueId()).getName() + " était " + ChatColor.DARK_GREEN + role.getType().getName());
        if (role.getType() == RoleType.KUZEN) {
            String antiqueList = this.plugin.getRoleManager().antiqueList(false);
            for (String part : antiqueList.split("\n")) {
                UltraHardcoreAPI.getInstance().broadcast(part);
            }
        } else if (role instanceof Human) {
            ParticipantPlayer participant = event.getKiller();
            Player killer = participant.getBukkitPlayer().orElse(null);
            if (killer == null || killer.getHealth() == killer.getMaxHealth()) return;
            Role killerRole = this.plugin.getRoleManager().getRole(killer);
            if (killerRole == null || !killerRole.isGhoul()) return;
            killer.sendMessage(ChatColor.GREEN + "Vous avez tué un humain, vous avez obtenu un coeur supplémentaire");
            killer.setHealth(Math.min(killer.getMaxHealth(), killer.getHealth() + 2));
            killerRole.resetTicks();
        }

        if (role.eliminate(player, false)) return;

        Set<ItemStack> toRemove = new HashSet<>();
        deathEvent.getDrops().forEach(itemStack -> {
            if (InteractiveItem.getItem(itemStack) != null) toRemove.add(itemStack);
        });
        if (!toRemove.isEmpty()) deathEvent.getDrops().removeAll(toRemove);
    }
}
