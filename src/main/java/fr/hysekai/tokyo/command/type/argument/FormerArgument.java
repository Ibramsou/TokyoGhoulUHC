package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.task.FormationTask;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.team.AbstractTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class FormerArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tg former <pseudo>");
            return false;
        }
        if (role.getType() != RoleType.COLOMBE) {
            player.sendMessage(ChatColor.RED + "Vous devez être une colombe pour former quelqu'un.");
            return false;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable");
            return false;
        }

        if (!role.isCanForm()) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette commande qu'une seule fois");
            return false;
        }

        AbstractTeam team = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getPlayerTeam(UltraHardcoreAPI.getInstance().getGameManager().getParticipantManager().getParticipant(player));
        AbstractTeam targetTeam = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getPlayerTeam(UltraHardcoreAPI.getInstance().getGameManager().getParticipantManager().getParticipant(target));
        if (team != targetTeam) {
            player.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans votre équipe.");
            return false;
        }

        final Role targetRole = plugin.getRoleManager().getRole(target);
        if (targetRole == null) {
            player.sendMessage(ChatColor.RED + "Ce joueur ne possède aucun role.");
            return false;
        }

        if (targetRole.getType() == RoleType.TOKA) {
            target.sendMessage(ChatColor.GRAY + player.getName() + " essaye de vous former à devenir une Apprenti Colombe");
        }

        int time = plugin.getOptions().getFormationTime() * 60;

        FormationTask formation = new FormationTask(plugin, RoleType.INNOCENT, player, target, time, time * 2);
        formation.runTaskTimerAsynchronously(plugin, 0L, 2L);
        targetRole.setFormation(formation);
        role.setCanForm(false);
        player.sendMessage(ChatColor.GREEN + target.getName() + " est désormais en formation.\n" +
                "Restez a proximité de lui pendant au moins 15 minutes\n" +
                "Si la formation n'est pas terminée au bout de 30 minutes, vous recevrez un message d'erreur\n" +
                "Vous recevrez aussi un message d'erreur si le joueur que vous avez tenté de former n'est pas un innocent.");

        return true;
    }
}
