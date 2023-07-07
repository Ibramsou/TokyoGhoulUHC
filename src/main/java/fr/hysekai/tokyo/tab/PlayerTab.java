package fr.hysekai.tokyo.tab;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.util.Alphabetical;
import fr.hysekai.tokyo.util.Reflection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class PlayerTab {

    private static final Field bField = Reflection.accessField(PacketPlayOutPlayerListHeaderFooter.class, "b");

    private final EntityPlayer entityplayer;
    private final Map<Player, PlayerTeam> playerTeams = new HashMap<>(80, 1);
    private final String defaultOrder;

    private boolean removed = true;
    private int nextOrder;
    private String nextPrefix = "", nextSuffix = "", nextTabPrefix = "", nextTabSuffix = "";

    public PlayerTab(Player player) {
        this.entityplayer = ((CraftPlayer) player).getHandle();
        this.defaultOrder = Alphabetical.getStringOrder(this.defaultOrder(), true);
    }

    public void createBoard() {
        TokyoGhoulPlugin.getInstance().getBoardManager().createObjective(this.entityplayer);

        Packet<?> packet;
        if (this.displayHeaderFooter()) {
            IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + this.header() + "\"}");
            IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + this.footer() + "\"}");
            packet = new PacketPlayOutPlayerListHeaderFooter(header);
            Reflection.set(packet, bField, footer);
            this.entityplayer.playerConnection.sendPacket(packet);
        }

        if (this.displayHealth()) {
            TokyoGhoulPlugin.getInstance().getBoardManager().createDisplay(this.entityplayer);
        }

        this.removed = false;
    }

    public void removeBoard() {
        if (this.removed) return;
        this.playerTeams.values().forEach(playerTeam -> playerTeam.unregister(this.entityplayer));
        TokyoGhoulPlugin.getInstance().getBoardManager().removeObjective(this.entityplayer);
        this.playerTeams.clear();
        this.removed = true;
    }

    public void updateHealth(Player target, double health) {
        if (!this.displayHealth()) return;
        PlayerTeam team = this.playerTeams.get(target);
        if (team == null) return;
        team.updateHealth(this.entityplayer, health);
    }

    public void refreshTeams() {
        for (Map.Entry<Player, PlayerTeam> entry : this.playerTeams.entrySet()) {
            PlayerTeam team = entry.getValue();
            team.update(this.entityplayer);
            team.updateHealth(this.entityplayer);
        }
    }

    public void updatePlayer(Player target) {
        PlayerTeam team = this.playerTeams.get(target);
        if (team == null || team.isRegistered()) return;
        this.onUpdate(target);
        this.sendPackets(team);
    }

    public void addPlayer(Player target) {
        if (playerTeams.size() == 80) return;
        this.checkBoard();
        if (this.playerTeams.containsKey(target)) return;
        this.onUpdate(target);
        PlayerTeam team = new PlayerTeam(target, TokyoGhoulPlugin.getInstance().getBoardManager().getCachedObjective(), this.defaultOrder);
        team.setPrefix(this.nextPrefix);
        team.setSuffix(this.nextSuffix);
        team.setTabPrefix(this.nextTabPrefix);
        team.setTabSuffix(this.nextTabSuffix);
        team.setOrder(this.nextOrder);
        team.register(this.entityplayer);
        team.updateHealth(entityplayer);
        team.setRegistered(true);
        this.playerTeams.put(target, team);
    }

    public void removePlayer(Player target) {
        this.checkBoard();
        PlayerTeam team = this.playerTeams.remove(target);
        if (team == null) return;
        Packet<?> packet = new PacketPlayOutScoreboardTeam(team, 1);
        this.entityplayer.playerConnection.sendPacket(packet);
        team.updateListName(this.entityplayer);
    }

    public void checkBoard() {
        if (this.removed) this.createBoard();
    }

    public Player getPlayer() {
        return entityplayer.getBukkitEntity();
    }

    private void sendPackets(PlayerTeam team) {
        team.setPrefix(this.nextPrefix);
        team.setSuffix(this.nextSuffix);
        team.setTabPrefix(this.nextTabPrefix);
        team.setTabSuffix(this.nextTabSuffix);
        team.setOrder(this.nextOrder);
        team.update(this.entityplayer);
        if (this.displayHealth()) team.updateHealth(entityplayer);
    }

    protected final void setPrefix(String prefix) {
        this.nextPrefix = prefix == null ? "" : prefix;
    }

    protected final void setSuffix(String suffix) {
        this.nextSuffix = suffix == null ? "" : suffix;
    }

    protected final void setTabPrefix(String prefix) {
        this.nextTabPrefix = prefix == null ? "" : prefix;
    }

    protected final void setTabSuffix(String suffix) {
        this.nextTabSuffix = suffix == null ? "" : suffix;
    }

    protected final void setOrder(int order) {
        this.nextOrder = order;
    }

    public abstract void onUpdate(Player target);

    public abstract int defaultOrder();

    public abstract boolean displayHealth();

    public abstract boolean displayHeaderFooter();

    public abstract String header();

    public abstract String footer();
}