package fr.hysekai.tokyo.kagune.item;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.task.DashTask;
import fr.hysekai.tokyo.util.Velocity;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Dash extends InteractiveItem {

    public Dash() {
        super(Material.SUGAR, ChatColor.GREEN + "Dash");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action.equals(InteractiveAction.LEFT_CLICK) || !role.canUseSkill(player, true)) return;
        boolean toka = role.getType() == RoleType.TOKA;
        Location location = player.getLocation();
        if (!toka) this.plugin.getEffectManager().broadcastParticle(location.getWorld(), EnumParticle.EXPLOSION_LARGE, location.getX(), location.getY(), location.getZ());
        this.plugin.getEffectManager().broadcastSound(location.getWorld(), Sound.ENDERDRAGON_WINGS, location.getX(), location.getY(), location.getZ(), 1f, 1f);
        Velocity.dash(player, 5.5F, 0.5F);
        if (toka) new DashTask(TokyoGhoulPlugin.getInstance(), player).runTaskTimerAsynchronously(this.plugin, 0L, 1L);

    }
}
