package fr.hysekai.tokyo.kagune.item;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.task.FrenzyTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Frenzy extends InteractiveItem {

    public Frenzy() {
        super(Material.BLAZE_ROD, ChatColor.RED + "Sort de Frénésie");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action.equals(InteractiveAction.LEFT_CLICK) || !role.canUseSkill(player, true)) return;
        Location location = player.getLocation();
        this.plugin.getEffectManager().broadcastSound(location.getWorld(), Sound.ENDERDRAGON_WINGS, location.getX(), location.getY(), location.getZ(), 1f, 1f);
        new FrenzyTask(TokyoGhoulPlugin.getInstance(), player).runTaskTimerAsynchronously(this.plugin, 0L, 1L);
    }
}
