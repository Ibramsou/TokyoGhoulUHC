package fr.hysekai.tokyo.kagune.item;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.type.Ghoul;
import fr.hysekai.tokyo.task.ResistanceTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Armor extends InteractiveItem {

    public Armor() {
        super(Material.DIAMOND, ChatColor.DARK_AQUA + "Armure");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action.equals(InteractiveAction.LEFT_CLICK)) return;
        Ghoul ghoul = (Ghoul) role;
        if (ghoul.isDamaged()) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser votre armure pendand 10 secondes car vous avez été frappé par une Quinque Aus.");
            return;
        }
        if (!role.canUseSkill(player, true)) return;
        player.sendMessage(ChatColor.DARK_AQUA + "Vous avez reçu une protection durant 30 secondes");
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1));
        new ResistanceTask(TokyoGhoulPlugin.getInstance(), player.getUniqueId(), ghoul).runTaskTimerAsynchronously(this.plugin, 0L, 1L);
    }
}
