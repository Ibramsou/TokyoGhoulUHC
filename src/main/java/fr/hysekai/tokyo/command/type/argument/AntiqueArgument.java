package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.antique.AntiqueStatus;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AntiqueArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        if (role.getType() != RoleType.KUZEN) {
            player.sendMessage(ChatColor.RED + "Seul le Kuzen peut utiliser cette commande.");
        }

        /*if (!plugin.getAntiqueManager().isReady()) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser cette commmande pour le moment");
            return false;
        }*/ // Test

        AntiqueStatus status = plugin.getAntiqueManager().getStatus();
        if (status != null) {
            if (status == AntiqueStatus.FINISHED) {
                player.sendMessage(ChatColor.RED + "Cette commande ne peut être utiliser qu'une seule fois.");
            } else {
                player.sendMessage(ChatColor.RED + "Cet évènement a déjà commencé mon reuf.");
            }
            return false;
        }

        plugin.getAntiqueManager().teleportAntique(player);
        return true;
    }
}
