package fr.hysekai.tokyo.option.items;

import fr.hysekai.tokyo.option.OptionItem;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.players.ParticipantPlayer;
import fr.hysekai.uhcapi.game.team.MultiTeam;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Iterator;

public class MaxTeamsVirtualItem extends OptionItem {

    public MaxTeamsVirtualItem(int slot) {
        super(Material.BANNER, slot);
        BannerMeta meta = (BannerMeta) this.getItemMeta();
        meta.setBaseColor(DyeColor.GRAY);
        this.setItemMeta(meta);
        this.setDisplayName(ChatColor.DARK_AQUA + "Joueurs par team");
    }

    @Override
    protected String lore() {
        return this.formatValue(this.value(), "joueur");
    }

    @Override
    protected int value() {
        return this.options.getMaxTeamSize();
    }

    @Override
    protected int min() {
        return 4;
    }

    @Override
    protected int max() {
        return 6;
    }

    @Override
    public int shiftChange() {
        return 1;
    }

    @Override
    protected void change(int change) {
        this.options.setMaxTeamSize(change);
        for (MultiTeam team : UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams()) {
            team.setMaxSize(change);
            Iterator<ParticipantPlayer> iterator = team.getMembers().iterator();
            int currentSize = team.getMembers().size();
            while (currentSize > change && iterator.hasNext()) {
                team.quit(iterator.next());
                currentSize--;
            }
        }
    }
}
