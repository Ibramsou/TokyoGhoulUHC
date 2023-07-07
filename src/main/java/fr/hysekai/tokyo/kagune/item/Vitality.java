package fr.hysekai.tokyo.kagune.item;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Vitality extends InteractiveItem {

    public Vitality() {
        super(Material.NETHER_STAR, ChatColor.RED + "Vol de vie");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (!action.equals(InteractiveAction.ENTITY_CLICK)) return;
        double health = clicked.getHealth();
        double steal = health <= 4 ? 1 : 4;
        clicked.damage(steal);
        player.setHealth(Math.min(player.getHealth() + steal, player.getMaxHealth()));
        steal /= 2;
        String msg = ChatColor.RED + (steal == 2 ?  + (int) steal + " coeurs" : "un demi coeur") + ChatColor.GRAY;
        player.sendMessage(ChatColor.GRAY + "Vous avez volé " + msg + " à " + ChatColor.RED + clicked.getName());
        clicked.sendMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " vous a volé " + msg);
        Location location = clicked.getLocation();
        this.plugin.getEffectManager().broadcastSound(location.getWorld(), Sound.PORTAL, location.getX(), location.getY(), location.getZ(), 1f, 1f);
    }
}
