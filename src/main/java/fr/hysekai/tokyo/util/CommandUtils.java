package fr.hysekai.tokyo.util;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandUtils {

    public static void injectCommand(String fallbackPrefix, Command... commands) {
        for (Command executor : commands) {
            MinecraftServer.getServer().server.getCommandMap().register(fallbackPrefix, executor);
        }
    }

    public static void injectCommand(Plugin plugin, Command... commands) {
        injectCommand(plugin.getName().toLowerCase(), commands);
    }

    public static Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }
}
