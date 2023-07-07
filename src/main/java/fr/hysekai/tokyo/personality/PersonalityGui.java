package fr.hysekai.tokyo.personality;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.type.antique.Kaneki;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PersonalityGui extends AbstractGui {

    private final Player player;
    private final Kaneki kaneki;

    public PersonalityGui(Player player, Kaneki kaneki) {
        super(ChatColor.GREEN + "Choisissez votre personalité", 1);

        this.player = player;
        this.kaneki = kaneki;
    }

    @Override
    public void registerItems() {
        int index = 3;
        for (Personality personality : Personality.values()) {
            this.addItems(index, new PersonalityVirtualItem(personality, kaneki));
            index++;
        }
    }

    @Override
    public void close(Inventory inventory) {
        if (!this.player.isOnline()) return;
        if (this.kaneki.getPersonality() == null) Bukkit.getScheduler().runTaskLater(TokyoGhoulPlugin.getInstance(), () -> player.openInventory(inventory), 1L);
    }
}
