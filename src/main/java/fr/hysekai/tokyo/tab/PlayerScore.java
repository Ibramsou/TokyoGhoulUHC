package fr.hysekai.tokyo.tab;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerScore extends ScoreboardScore {

    public PlayerScore(ScoreboardObjective scoreboardObjective, Player player) {
        super(TokyoGhoulPlugin.getInstance().getBoardManager().getCachedBoard(), scoreboardObjective, player.getName());
    }

    @Override
    public void addScore(int i) {}

    @Override
    public void removeScore(int i) {}

    @Override
    public void incrementScore() {}

    @Override
    public void a(boolean b) {}

    @Override
    public void updateForList(List<EntityHuman> list) {}
}
