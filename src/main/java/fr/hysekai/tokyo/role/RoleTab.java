package fr.hysekai.tokyo.role;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.personality.Personality;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.role.type.antique.Kaneki;
import fr.hysekai.tokyo.tab.PlayerTab;
import fr.hysekai.tokyo.util.OrderedTeam;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.players.ParticipantPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleTab extends PlayerTab {

    private final Role role;

    public RoleTab(Role role, Player player) {
        super(player);
        this.role = role;
    }

    @Override
    public void onUpdate(Player target) {
        ParticipantPlayer participant = UltraHardcoreAPI.getInstance().getGameManager().getParticipantManager().getParticipant(target);
        if (participant == null) return;
        OrderedTeam team = (OrderedTeam) UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getPlayerTeam(participant);
        if (team == null) return;
        String teamColor = team.getChatColor().toString();

        Role targetRole = TokyoGhoulPlugin.getInstance().getRoleManager().getRole(target);
        if (targetRole == null) return;
        String prefix, tabPrefix, suffix = "", tabSuffix = "";

        if (targetRole.isDead()) {
            tabPrefix = prefix = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + ChatColor.STRIKETHROUGH;
        } else {
            prefix = tabPrefix = teamColor;

            if (targetRole.isRevealed()) {
                prefix = tabPrefix = ChatColor.DARK_PURPLE + targetRole.getType().getName().split(" ")[0] + " ";
            } else {
                boolean isGhoul = this.role.getType() == RoleType.SAVAGE_GHOUL && targetRole.getType() == RoleType.SAVAGE_GHOUL;
                boolean isAntique = this.role instanceof Antique && targetRole instanceof Antique;

                if (TokyoGhoulPlugin.getInstance().getRoleManager().isReady() && (isGhoul || isAntique)) {
                     if (isGhoul) {
                         prefix = ChatColor.WHITE + "* " + teamColor;
                     } else if (available(targetRole) && available(role) && (targetRole.getType() == RoleType.KUZEN || role.getType() == RoleType.KUZEN)){
                         prefix = ChatColor.WHITE + targetRole.getType().getName().split(" ")[0] + " " + teamColor;
                    }
                }
            }
        }

        int teamOrder = team.getOrder();

        this.setOrder(targetRole.isRevealed() && !targetRole.isDead() ? 0 : targetRole.isDead() ? 8 : teamOrder);
        this.setTabPrefix(tabPrefix);
        this.setTabSuffix(tabSuffix);
        this.setPrefix(prefix);
        this.setSuffix(suffix);
    }

    @Override
    public int defaultOrder() {
        return 1;
    }

    @Override
    public boolean displayHealth() {
        return true;
    }

    @Override
    public boolean displayHeaderFooter() {
        return false;
    }

    @Override
    public String header() {
        return null;
    }

    @Override
    public String footer() {
        return null;
    }

    private boolean available(Role role) {
        if (role.getType() == RoleType.KANEKI) {
            Kaneki kaneki = (Kaneki) role;
            return kaneki.isSetup() && kaneki.getPersonality() == Personality.KANEKI;
        }

        return true;
    }
}
