package fr.hysekai.tokyo.personality;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.quinque.Quinque;
import fr.hysekai.tokyo.role.type.antique.Kaneki;
import fr.hysekai.uhcapi.utils.VirtualItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class PersonalityVirtualItem extends VirtualItem {

        private final Personality personality;
        private final Kaneki kaneki;

        public PersonalityVirtualItem(Personality personality, Kaneki kaneki) {
            super(personality.getMaterial());
            this.setDisplayName(personality.getName());
            this.setLore(personality.getDescription());

            this.personality = personality;
            this.kaneki = kaneki;
        }

        @Override
        public void use(Player player) {
            this.kaneki.setPersonality(this.personality);
            this.kaneki.setSetup(true);
            TokyoGhoulPlugin.getInstance().getBoardManager().updateTags(this.kaneki.getTab(), player);
            if (this.personality == Personality.SASAKI) {
                Quinque[] quinques = Quinque.values();
                this.kaneki.setQuinque(quinques[ThreadLocalRandom.current().nextInt(quinques.length)]);
                player.getInventory().addItem(this.kaneki.getQuinque().getItem());
                TokyoGhoulPlugin.getInstance().getRoleManager().getAliveAntiques().remove(player);
                TokyoGhoulPlugin.getInstance().getRoleManager().getAliveHumans().add(player);
            } else if (this.personality == Personality.KING) {
                TokyoGhoulPlugin.getInstance().getRoleManager().getAliveAntiques().remove(player);
                TokyoGhoulPlugin.getInstance().getRoleManager().setAliveKing(player);
            }
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Vous avez choisis la personalité " + ChatColor.DARK_GREEN + this.personality.getName() + ChatColor.GREEN + ".");
            this.kaneki.updateSkin(player, false, false);
        }
    }