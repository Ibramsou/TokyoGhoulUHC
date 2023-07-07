package fr.hysekai.tokyo.role.type;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Antique extends Role {

    public Antique(RoleType type) {
        super(type);
    }

    @Override
    public void connect(Player player) {
        this.onConnect(player);
        super.connect(player);
        this.updateSkin(player, this.revealed, true);
    }

    @Override
    public boolean eliminate(Player player, boolean disconnect) {
        this.onEliminate(player);
        return super.eliminate(player, disconnect);
    }

    @Override
    public void setup(Player player) {
        super.setup(player);
        this.onSetup(player);
    }

    @Override
    public void reveal(Player player) {
        super.reveal(player);
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
        this.onReveal(player);
    }

    protected void onConnect(Player player) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveAntiques().add(player);
    }

    protected void onEliminate(Player player) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveAntiques().remove(player);
    }

    protected void onSetup(Player player) {
        this.updateSkin(player, false, false);
    }

    protected void onReveal(Player player) {
        this.updateSkin(player, true, false);
    }

    @Override
    public boolean canAffect(Player player, Player target) {
        return true;
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
        };
    }

    @Override
    public String roleName() {
        return ChatColor.GOLD + this.getType().getName() + ChatColor.WHITE + " (Membre de l'Antique)";
    }

    @Override
    public void onTick(TokyoGhoulPlugin plugin, Player player, long ticks) {
        long seconds = 1200 - ticks;

        if (seconds <= 0) this.resetTicks();

        if (seconds <= 5) {
            if (seconds <= 0) {
                plugin.getEffectManager().selfDamage(player, 2.0D);
                player.sendMessage(ChatColor.RED + "Vous avez perdu un receptacle de coeur car vous n'avez pas tué d'humain lors de ces 20 dernières minutes.");
            } else {
                player.sendMessage(ChatColor.RED + "Il ne vous reste plus que " + seconds + ".");
            }
        } else if (seconds <= 600) {
            int minute = seconds == 600 ? 10 : seconds == 300 ? 5 : seconds == 60 ? 1 : -1;
            if (minute != -1) player.sendMessage(ChatColor.RED + "Il ne vous reste plus que " + minute + (minute == 1 ? " minutes" : " minute") + "pour tuer un humain.");
        }
    }
}
