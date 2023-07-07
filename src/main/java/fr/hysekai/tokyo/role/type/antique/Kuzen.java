package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.skin.Skin;
import fr.hysekai.tokyo.task.OwlTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kuzen extends Antique {

    private boolean owl;

    public Kuzen() {
        super(RoleType.KUZEN);
    }

    @Override
    public String[] information() {
        return null;
    }

    @Override
    public Skin getSkin() {
        return this.owl && this.revealed ? Skin.OWL : Skin.KUZEN;
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new OwlItem()
        };
    }

    @Override
    public void setup(Player player) {
        super.setup(player);
        if (!TokyoGhoulPlugin.getInstance().getRoleManager().isReady()) return;
        player.sendMessage(TokyoGhoulPlugin.getInstance().getRoleManager().antiqueList(true));
    }

    @Override
    protected void onReveal(Player player) {
        this.updateSkin(player, true, true);
    }

    @Override
    public void reveal(Player player) {
        this.owl = true;
        this.transform(player);
        super.reveal(player);
    }

    public void setOwl(boolean owl) {
        this.owl = owl;
    }

    private void transform(Player player) {
        if (!this.canUseSkill(player, true)) return;

        player.sendMessage(ChatColor.GRAY + "Vous êtes désormais sous votre forme de Chouette pour une durée de 3 minutes.");
        if (this.revealed) {
            this.onReveal(player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0));
        new OwlTask(this, player.getUniqueId()).runTaskTimerAsynchronously(TokyoGhoulPlugin.getInstance(), 300L, 300L);

    }

    private class OwlItem extends InteractiveItem {

        public OwlItem() {
            super(Material.SKULL_ITEM, ChatColor.DARK_GREEN + "Transformation en Chouette");
        }

        @Override
        public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

        }

        @Override
        public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
            if (action != InteractiveAction.RIGHT_CLICK) return;
            if (!Kuzen.this.revealed) {
                Kuzen.this.reveal(player);
                return;
            }

            Kuzen.this.transform(player);
        }
    }

}
