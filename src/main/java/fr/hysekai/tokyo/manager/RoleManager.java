package fr.hysekai.tokyo.manager;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleTab;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.role.type.Ghoul;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.tokyo.role.type.antique.*;
import fr.hysekai.tokyo.util.OrderedTeam;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.players.ParticipantPlayer;
import fr.hysekai.uhcapi.game.team.MultiTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RoleManager {

    private final Map<UUID, Role> roleMap = new HashMap<>(42, 1);
    private final List<Antique> antiques = new ArrayList<>();


    private final List<Player> aliveAntiques = new ArrayList<>(8);
    private final List<Player> aliveGhouls = new ArrayList<>(7);
    private final List<Player> aliveHumans = new ArrayList<>(29);

    private Player aliveKing;

    private StringBuilder antiqueMessage;
    private StringBuilder antiqueDetails;
    private int antiqueCount;
    private boolean roleAssigned;

    public RoleManager() {
        this.antiques.addAll(
                Arrays.asList(
                        new Enji(),
                        new Kaneki(),
                        new Kaya(),
                        new Kuzen(),
                        new Nishiki(),
                        new Renji(),
                        new Toka()
                )
        );
    }

    public void assignRole(List<ParticipantPlayer> members, int index, Role role, boolean check, ChatColor teamColor) {
        if (check && index >= members.size()) return;
        final ParticipantPlayer member = members.get(index);
        this.roleMap.put(member.getUniqueId(), role);
        Player player = member.getBukkitPlayer().orElse(null);

        role.setTeamColor(teamColor);
        if (role instanceof Antique) {
            String name = player == null ? Bukkit.getOfflinePlayer(member.getUniqueId()).getName() : player.getName();
            StringBuilder builder = new StringBuilder();
            if (role.getType() != RoleType.KUZEN) {
                builder.append("\n").append(ChatColor.DARK_GREEN).append(this.antiqueCount).append(". ").append(ChatColor.GREEN).append(name);
                this.antiqueMessage.append(builder);
                this.antiqueDetails.append(builder).append(ChatColor.DARK_GREEN).append(" (").append(role.getType().getName()).append(")");
                this.antiqueCount++;
            }
            if (player != null) this.aliveAntiques.add(player);
        } else if (player != null) {
            if (role instanceof Ghoul) {
                this.aliveGhouls.add(player);
            } else if (role instanceof Human) {
                this.aliveHumans.add(player);
            }
        }

        if (player != null) {
            role.setTab(new RoleTab(role, player));
            TokyoGhoulPlugin.getInstance().getBoardManager().updateTags(role.getTab(), player);
        }
    }

    public final void assignRoles() {
        this.aliveAntiques.clear();
        this.aliveGhouls.clear();
        this.aliveHumans.clear();
        this.aliveKing = null;
        this.antiqueMessage = new StringBuilder(ChatColor.GREEN + "Voici la liste des membres de l'Antique:");
        this.antiqueDetails = new StringBuilder(ChatColor.GREEN + "Voici la liste des membres de l'Antique:");
        this.antiqueCount = 1;
        int count = 0;


        List<MultiTeam> teams = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams();
        Collections.shuffle(this.antiques);
        for (MultiTeam team : teams) {
            if (count == 7) count = 0;
            final List<ParticipantPlayer> members = new ArrayList<>(team.getMembers());
            if (members.isEmpty()) continue;
            Collections.shuffle(members, ThreadLocalRandom.current());
            members.sort(Comparator.comparing(member -> !member.isConnected()));
            ChatColor teamColor = team.getChatColor();
            this.assignRole(members, 0, this.antiques.get(count), true, teamColor);
            this.assignRole(members, 1, new Ghoul(), true, teamColor);
            this.assignRole(members, 2, new Human(true), true, teamColor);
            for (int i = 3; i < 6; i++) {
                if (i >= members.size()) break;
                this.assignRole(members, i, new Human(false), false, teamColor);
            }

            count++;
        }
    }

    public void setupRoles() {
        UltraHardcoreAPI.getInstance().broadcast(ChatColor.GREEN.toString() + "Les rôles ont été assignés");

        this.roleAssigned = true;

        this.roleMap.forEach((uuid, role) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            role.setup(player);

            if (role.getType() == RoleType.KUZEN) {
                player.sendMessage(this.antiqueList(true));
            }
        });
    }

    public boolean detectWin(Player player) {

        ParticipantPlayer participantPlayer = UltraHardcoreAPI.getInstance().getGameManager().getParticipantManager().getParticipant(player);
        if (participantPlayer == null) return false;
        MultiTeam team = (MultiTeam) UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getPlayerTeam(participantPlayer);
        if (team == null) return false;

        List<Player> winners = new ArrayList<>(7);

        team.quit(participantPlayer);
        if (team.getMembers().size() == 0) UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams().remove(team);

        boolean antiqueAlive = this.aliveAntiques.size() > 0;
        boolean humansAlive = this.aliveHumans.size() > 0;
        boolean ghoulsAlive = this.aliveGhouls.size() > 0;
        boolean kingAlive = this.aliveKing != null;

        String winningMessage = null;

        if (kingAlive && roleAssigned) {
            if (!antiqueAlive && !humansAlive && !ghoulsAlive) {
                winningMessage = ChatColor.GREEN + "Le roi borgne a gagné la partie !";
                winners.add(this.aliveKing);
                this.aliveKing = null;
            }
        } else if (antiqueAlive && !ghoulsAlive && !humansAlive && roleAssigned) {
            winningMessage = ChatColor.GREEN + "Les membres de l'Antique ont gagné la partie !";
            winners.addAll(this.aliveAntiques);
            this.aliveAntiques.clear();
        } else if (this.aliveGhouls.size() < 2 && ghoulsAlive && !humansAlive && !antiqueAlive && roleAssigned) {
            winningMessage = ChatColor.GREEN + "Une ghoul sauvage a gagné la partie !";
            winners.addAll(this.aliveGhouls);
            this.aliveGhouls.clear();
        } else {
            List<MultiTeam> teams = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams();
            if (teams.size() < 2) {
                team = teams.size() > 0 ? teams.get(0) : null;
                if (team != null) {
                    winningMessage = ChatColor.GREEN + "L'équipe " + ((OrderedTeam) team).getDisplayName() + ChatColor.GREEN  + " a gagné !";
                    team.getMembers().forEach(p -> {
                        Player target = p.getBukkitPlayer().orElse(null);
                        if (target == null) return;
                        winners.add(target);
                    });
                } else {
                    winningMessage = ChatColor.RED + "Impossible de trouver un gagnant pour cette partie.";
                }
            }
        }

        if (winners.isEmpty()) return false;

        UltraHardcoreAPI.getInstance().broadcast(winningMessage);
        UltraHardcoreAPI.getInstance().getGameManager().startFinish(winners);
        return true;
    }

    public String antiqueList(boolean showDetails) {
        return showDetails ? this.antiqueDetails.toString() : this.antiqueMessage.toString();
    }

    public boolean isReady() {
        return roleAssigned || TokyoGhoulPlugin.testMode;
    }

    public final Player getByType(RoleType type) {
        for (Map.Entry<UUID, Role> entry : this.roleMap.entrySet()) {
            if (entry.getValue().getType() == type) return Bukkit.getPlayer(entry.getKey());
        }

        return null;
    }

    public final Role getRole(Player player) {
        return this.getRole(player.getUniqueId());
    }

    public final Role getRole(UUID uniqueID) {
        Role role = this.roleMap.get(uniqueID);
        if (role == null || role.isDead() || role.isDisconnected()) return null;
        return role;
    }

    public final RoleType getType(Player player) {
        final Role role = this.getRole(player);
        return role == null ?  RoleType.UNKNOWN  : role.getType();
    }

    public final void setRole(Player player, Role role) { // Test
        this.roleMap.put(player.getUniqueId(), role);
        role.setup(player);
    }

    public void setAliveKing(Player player) {
        this.aliveKing = player;
    }

    public List<Player> getAliveAntiques() {
        return this.aliveAntiques;
    }

    public List<Player> getAliveGhouls() {
        return aliveGhouls;
    }

    public List<Player> getAliveHumans() {
        return aliveHumans;
    }

    public Map<UUID, Role> getRoleMap() {
        return roleMap;
    }
}
