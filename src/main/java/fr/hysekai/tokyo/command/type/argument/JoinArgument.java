package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.antique.AntiqueStatus;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JoinArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        AntiqueStatus status = plugin.getAntiqueManager().getStatus();

        if (status == null || status == AntiqueStatus.STARTED) {
            player.sendMessage(ChatColor.RED + "Cette commande n'est pas disponnible pour le moment.");
            return false;
        }

        if (status == AntiqueStatus.FINISHED) {
            player.sendMessage(ChatColor.RED + "Cet évènement est terminé.");
            return false;
        }

        if (role instanceof Antique) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans le café de l'antique");
            return false;
        }

        if (role.isChangedDimension()) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez rejoindre cette dimension qu'une seule fois.");
            return false;
        }

        RoleType type = role.getType();
        if (type == RoleType.COLOMBE || type == RoleType.SAVAGE_GHOUL && role.isRevealed()) {
            plugin.getAntiqueManager().addPlayer(role, player);
        } else {
            player.sendMessage(ChatColor.RED + "Vous devez être une colombe ou alors une ghoul sauvage (reveal) pour utiliser cette commande");
        }
        return false;
    }
}
