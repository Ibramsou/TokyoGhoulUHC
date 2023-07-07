package fr.hysekai.tokyo.util;

import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.team.MultiTeam;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

import java.util.Collections;

public class OrderedTeam extends MultiTeam {

    private final String displayName;
    private final int order;

    public OrderedTeam(UltraHardcoreAPI api, String displayName, String name, ChatColor chatColor, DyeColor dyeColor, int order) {
        super(api, name, chatColor, dyeColor, Collections.singletonList(PatternType.BASE));
        this.order = order;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }
}
