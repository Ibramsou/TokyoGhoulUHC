package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class CoordinatesArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        if (role.getType() != RoleType.RENJI) {
            player.sendMessage(ChatColor.RED + "Seul Renji peut utiliser cette commande.");
            return false;
        }

        Player target = plugin.getRoleManager().getByType(RoleType.KUZEN);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Le Kuzen n'est pas présent dans cette partie.");
            return false;
        }

        Role targetRole = plugin.getRoleManager().getRole(target);
        if (targetRole.isDead()) {
            player.sendMessage(ChatColor.RED + "Kuzen est mort.");
            return false;
        }

        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        target.sendMessage(ChatColor.GREEN + "Renji vous a envoyé ses coordonnées:");
        target.sendMessage(ChatColor.GREEN + " » X: " + ChatColor.WHITE + x);
        target.sendMessage(ChatColor.GREEN + " » Y: " + ChatColor.WHITE + y);
        target.sendMessage(ChatColor.GREEN + " » Z: " + ChatColor.WHITE + z);

        player.sendMessage(ChatColor.GREEN + "Vous venez d'envoyer vos coordonnées à Kuzen.");
        return true;
    }
}
