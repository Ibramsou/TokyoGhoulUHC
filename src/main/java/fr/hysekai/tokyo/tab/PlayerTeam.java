package fr.hysekai.tokyo.tab;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.util.Alphabetical;
import fr.hysekai.tokyo.util.Reflection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;

public class PlayerTeam extends ScoreboardTeam {

    private static final Field cField = Reflection.accessField(ScoreboardTeam.class, "c");

    private final PlayerScore score;
    private final EntityPlayer entityplayer;

    private String lastOrder, orderedName;
    private String tabPrefix = "", tabSuffix = "";
    private boolean tabList, registered;

    protected PlayerTeam(Player player, ScoreboardObjective objective, String orderedName) {
        super(TokyoGhoulPlugin.getInstance().getBoardManager().getCachedBoard(), "TAG_" + player.getEntityId());
        this.orderedName = orderedName + "_" + player.getEntityId();
        this.score = new PlayerScore(objective, player);
        this.entityplayer = ((CraftPlayer) player).getHandle();
        Reflection.set(this, cField, Collections.singleton(player.getName()));
    }

    protected void register(EntityPlayer entityplayer) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 0));
        this.tabList = true;
        this.updateListName(entityplayer);
        this.tabList = false;
    }

    protected void unregister(EntityPlayer entityplayer) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 1));
        entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardScore(this.score.getPlayerName()));
    }

    protected void update(EntityPlayer entityplayer) {
        String orderedName = this.orderedName;
        this.orderedName = this.lastOrder;
        entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 1));
        this.orderedName = orderedName;
        entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 0));
        this.tabList = true;
        this.updateListName(entityplayer);
        this.tabList = false;
    }

    protected void updateListName(EntityPlayer entityplayer) {
        IChatBaseComponent oldName = this.entityplayer.listName;
        this.entityplayer.listName = CraftChatMessage.fromString(this.getPrefix() + this.entityplayer.getName() + this.getSuffix())[0];
        entityplayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, this.entityplayer));
        this.entityplayer.listName = oldName;
    }

    protected void updateHealth(EntityPlayer entityplayer) {
        if (this.isRegistered()) return;
        this.updateHealth(entityplayer, this.entityplayer.getHealth());
    }

    protected void updateHealth(EntityPlayer entityplayer, double health) {
        Packet<?> packet;
        this.score.setScore((int) health);
        if (this.score.getScore() < 0) {
            packet = new PacketPlayOutScoreboardScore(this.score.getPlayerName());
        } else {
            packet = new PacketPlayOutScoreboardScore(this.score);
        }

        entityplayer.playerConnection.sendPacket(packet);
    }

    protected void setOrder(int order) {
        this.lastOrder = this.orderedName;
        this.orderedName = Alphabetical.getStringOrder(order, true) + "_" + this.entityplayer.getId();
    }

    protected void setTabPrefix(String prefix) {
        this.tabPrefix = prefix;
    }

    protected void setTabSuffix(String suffix) {
        this.tabSuffix = suffix;
    }

    protected void setRegistered(boolean registered) {
        this.registered = registered;
    }

    protected boolean isRegistered() {
        if (this.registered) {
            this.registered = false;
            return true;
        }

        return false;
    }

    @Override
    public String getDisplayName() {
        return this.orderedName;
    }

    @Override
    public String getName() {
        return this.orderedName;
    }

    @Override
    public String getPrefix() {
        return this.tabList ? this.tabPrefix : super.getPrefix();
    }

    @Override
    public String getSuffix() {
        return this.tabList ? this.tabSuffix : super.getSuffix();
    }

    @Override
    public void setDisplayName(String s) {}

    @Override
    public void setCanSeeFriendlyInvisibles(boolean b) {}

    @Override
    public void b(EnumNameTagVisibility enumNameTagVisibility) {}

    @Override
    public void setAllowFriendlyFire(boolean b) {}

    @Override
    public void setNameTagVisibility(EnumNameTagVisibility enumNameTagVisibility) {}

    @Override
    public void a(EnumChatFormat enumChatFormat) {}
}