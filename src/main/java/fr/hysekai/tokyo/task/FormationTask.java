package fr.hysekai.tokyo.task;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.quinque.craft.QuinqueItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.tokyo.util.Distances;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FormationTask extends BukkitRunnable {

    private final TokyoGhoulPlugin plugin;
    private final RoleType required;
    private final UUID formerUUID, targetUUID;
    private final int maxTicks, maxCheckTicks;
    private int ticks, checkTicks;
    private boolean failed;

    public FormationTask(TokyoGhoulPlugin plugin, RoleType required, Player former, Player target, int time, int maxTime) {
        this.required = required;
        this.formerUUID = former.getUniqueId();
        this.targetUUID = target.getUniqueId();
        this.maxTicks = (time * 1000) / 100;
        this.maxCheckTicks = (maxTime * 1000) / 100;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (this.failed) return;

        Role formerRole = this.plugin.getRoleManager().getRole(this.formerUUID);
        Role targetRole = this.plugin.getRoleManager().getRole(this.targetUUID);
        if (formerRole == null || targetRole == null) {
            this.cancel();
            return;
        }

        final Player target = this.getTarget();
        final Player former = this.getFormer();

        if (this.checkTicks++ >= this.maxCheckTicks) {
            this.failed(former, ChatColor.RED + "La formation a échoué car le joueur n'est pas resté assez longtemps a proximité de vous.");
            return;
        }
        if (former == null || target == null) return;

        int percent = this.ticks == 0 ? 0 : (int) ((double) this.ticks / (double) this.maxTicks * 100);

        if (Distances.isInRange(former, target, 15)) {
            this.sendActionBar(former, ChatColor.WHITE + "Formation: " + this.color(percent) + percent + "%");

            if (this.ticks++ == this.maxTicks) {
                final Role role = this.plugin.getRoleManager().getRole(target);
                if (role == null || role.getType() != this.required) {
                    former.sendMessage(ChatColor.RED + "La formation a échoué");
                } else {
                    role.setRallied(former, target, true);
                    TokyoGhoulPlugin.getInstance().getBoardManager().updateTags(role.getTab(), target);
                }

                this.cancel();
            }
        } else {
            this.sendActionBar(former, ChatColor.WHITE + "Formation: " + ChatColor.RED + "En Pause");
        }
    }

    public void failed(Player former, String message) {
        if (former != null) former.sendMessage(ChatColor.RED + message);
        this.failed = true;
        this.cancel();
    }

    private ChatColor color(int percent) {
        return percent >= 90 ? ChatColor.GREEN : percent >= 70 ? ChatColor.DARK_GREEN : percent >= 50 ? ChatColor.YELLOW : percent >= 30 ? ChatColor.GOLD : percent >= 10 ? ChatColor.RED : ChatColor.DARK_RED;
    }

    private void sendActionBar(Player former, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        ((CraftPlayer) former).getHandle().playerConnection.sendPacket(packet);
    }

    public Player getFormer() {
        return Bukkit.getPlayer(this.formerUUID);
    }

    public Player getTarget() {
        return Bukkit.getPlayer(this.targetUUID);
    }
}
