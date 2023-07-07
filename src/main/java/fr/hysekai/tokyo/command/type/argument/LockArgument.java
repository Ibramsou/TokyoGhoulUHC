package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.antique.AntiqueStatus;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LockArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        AntiqueStatus status = plugin.getAntiqueManager().getStatus();

        if (!plugin.getAntiqueManager().canLock()) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette commande qu'une seule fois.");
            return false;
        }

        if (status != AntiqueStatus.FIGHTING) {
            if (status != AntiqueStatus.STARTED) {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser cette commande pour le moment.");
            } else {
                player.sendMessage(ChatColor.RED + "Vous devez attendre que les joueurs puissent rejoindre la dimension pour utiliser cette commande.");
            }
            return false;
        }

        if (role.getType() != RoleType.KUZEN) {
            player.sendMessage(ChatColor.RED + "Seul le Kuzen peut utiliser cette commande.");
            return false;
        }

        plugin.getAntiqueManager().setLocked();
        UltraHardcoreAPI.getInstance().broadcast(ChatColor.RED + "La dimension du café de l'antique est bloquée pendant 1 minute, cela a pour conséquence d'empêcher tout le monde de d'y sortir durant cette période.");
        return true;
    }
}
