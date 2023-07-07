package fr.hysekai.tokyo.command.type.argument;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RevealArgument extends AbstractArgument {

    @Override
    public boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args) {
        if (!role.isGhoul()) {
            player.sendMessage(ChatColor.RED + "Vous devez être une ghoul vous révéler.");
            return false;
        }

        if (role.isRevealed()) {
            player.sendMessage(ChatColor.RED + "Vous vous êtes déjà révélé.");
            return false;
        }

        role.reveal(player);
        return true;
    }
}
