package fr.hysekai.tokyo.quinque.craft;

import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class QuinqueItem extends InteractiveItem {

    public QuinqueItem() {
        super(Material.BOOK, ChatColor.GREEN + "Craft de la Quinque");
    }

    @Override
    public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
        if (action == InteractiveAction.LEFT_CLICK) return;
        new QuinqueGui().open(player);
    }
}
