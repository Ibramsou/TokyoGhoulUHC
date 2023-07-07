package fr.hysekai.tokyo.command.type;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.command.AbstractArgument;
import fr.hysekai.tokyo.command.AbstractCommand;
import fr.hysekai.tokyo.command.type.argument.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TgCommand extends AbstractCommand {

    public TgCommand(TokyoGhoulPlugin plugin) {
        super(plugin, "tg");

        this.setAliases(Collections.singletonList("tokyoghool"));
    }

    @Override
    public boolean runCommand(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "TokyoGhoul Commands:");
        sender.sendMessage(" - " + ChatColor.GREEN + "/tg former <joueur> -> Former un joueur à devenir une colombe");
        sender.sendMessage(" - " + ChatColor.GREEN + "/tg reveal <joueur> -> Révéler votre role");
        sender.sendMessage(" - " + ChatColor.GREEN + "/tg coords -> Envoyer vos coordonnées au Kuzen de la partie");
        sender.sendMessage(" - " + ChatColor.GREEN + "/tg rallier <joueur> -> rallier une ghoul sauvage aux membres de l'antique");
        sender.sendMessage(" - " + ChatColor.GREEN + "/tg antique <joueur> -> réunir tout les membres de l'antique au le café de l'antique");
        return true;
    }

    @Override
    public Map<String, AbstractArgument> arguments() {
        Map<String, AbstractArgument> map = new LinkedHashMap<>();
        map.put("former", new FormerArgument());
        map.put("reveal", new RevealArgument());
        map.put("coords", new CoordinatesArgument());
        map.put("rallier", new RallierArgument());
        map.put("antique", new AntiqueArgument());
        map.put("join", new JoinArgument());
        map.put("lock", new LockArgument());
        return map;
    }
}
