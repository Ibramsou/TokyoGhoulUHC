package fr.hysekai.tokyo.command;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.entity.Player;

public abstract class AbstractArgument {

    public abstract boolean execute(TokyoGhoulPlugin plugin, Role role, Player player, String label, String[] args);
}
