package fr.hysekai.tokyo.command;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public abstract class AbstractCommand extends Command {

    private final TokyoGhoulPlugin plugin;
    private final Map<String, AbstractArgument> arguments;

    public AbstractCommand(TokyoGhoulPlugin plugin, String name) {
        super(name);

        this.plugin = plugin;
        this.arguments = this.arguments();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return false;
        }

        if (!TokyoGhoulPlugin.getInstance().getRoleManager().isReady()) {
            sender.sendMessage(ChatColor.RED + "Les roles n'ont pas encore été attribués");
            return false;
        }

        if (args.length < 1) {
            this.runCommand(sender, label, args);
            return false;
        }

        Player player = (Player) sender;

        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas de role.");
            return false;
        }

        String arg0 = args[0];
        AbstractArgument argument = arguments == null || arguments.isEmpty() ? null : this.arguments.get(arg0);
        if (argument == null) {
            return this.runCommand(sender, label, args);
        }

        return argument.execute(this.plugin, role, player, arg0, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player) || !TokyoGhoulPlugin.getInstance().getRoleManager().isReady()) {
            return null;
        }

        if (this.arguments == null || this.arguments.isEmpty()) return super.tabComplete(sender, label, args);

        AbstractArgument argument = this.arguments.get(args[0].toLowerCase());

        if (argument == null) {
            Set<String> names = this.arguments.keySet();
            return StringUtil.copyPartialMatches(args[0], names, new ArrayList<>(names.size()));
        }

        return super.tabComplete(sender, label, args);
    }

    public abstract boolean runCommand(CommandSender sender, String label, String[] args);

    public abstract Map<String, AbstractArgument> arguments();
}
