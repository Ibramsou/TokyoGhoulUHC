package fr.hysekai.tokyo.role.type;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.kagune.Kagune;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Ghoul extends Role {

    private Kagune kagune;
    private UUID damager;
    private BukkitRunnable activeTask;

    public Ghoul() {
        super(RoleType.SAVAGE_GHOUL);
    }

    @Override
    public void connect(Player player) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveGhouls().add(player);
        super.connect(player);
        this.updateSkin(player, this.revealed, true);
    }

    @Override
    public boolean eliminate(Player player, boolean disconnect) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveGhouls().remove(player);
        return super.eliminate(player, disconnect);
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

    @Override
    public void setup(Player player) {
        Kagune[] kagunes = Kagune.values();
        this.kagune = kagunes[ThreadLocalRandom.current().nextInt(kagunes.length)];
        super.setup(player);
        this.updateSkin(player, false, false);
    }

    @Override
    public String roleName() {
        return ChatColor.RED + "Une Ghoul Sauvage";
    }

    @Override
    public String[] information() {
        return new String[] {
                " » Vous êtes l'enemie de l'humanité",
                " » Vous devez donc gagner seul",
                " » en elimimant tout les autres joueurs de la partie",
                " ",
                " » Pour vous nourrir, il vous faut de la chair putréfié",
                " » Si vous mangez de la nourriture classique, votre barre",
                " » de nourriture se régénèrera moins bien et vous aurez 60% de chance",
                " » de perdre un demi-coeur (l'animation de dégat ne sera pas visible par les autres joueurs)",
                " ",
                " » Pour survivre, vous devez vous nourrir de chair humaine",
                " » Si vous ne tuez pas un joueur toutes les 20 minutres, vous perdrez un coeur",
                " » Et si vous tuez un humain, vous gagnerez un coeur",
                " ",
                " » Vous disposez d'un " + ChatColor.RED + this.kagune.getName(),
                " » Pour l'activer, vous devrez utiliser la commande /tg reveal",
                " ",
                " » Vous avez reçu " + ChatColor.BOLD + "un stack de Chair Putréfié"
        };
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh
        };
    }

    @Override
    public void reveal(Player player) {
        final PotionEffect effect = this.kagune.getEffect();
        if (effect != null) {
            player.addPotionEffect(effect);
        }

        if (this.kagune.getItem() != null) {
            player.getInventory().addItem(this.kagune.getItem());
            player.updateInventory();
        }

        if (this.kagune == Kagune.VITALITY) {
            player.setMaxHealth(30);
            player.setHealth(player.getMaxHealth());
        }

        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));

        this.updateSkin(player, true, false);
        super.reveal(player);
    }

    @Override
    public boolean canAffect(Player player, Player target) {
        return true;
    }

    @Override
    public void setRallied(Player former, Player player, boolean rallied) {
        super.setRallied(former, player, rallied);
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveGhouls().remove(player);
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveAntiques().add(player);

        former.sendMessage(ChatColor.GREEN + "Vous vous êtes rallié à la ghoul " + player.getName());
        player.sendMessage(ChatColor.GREEN + "Vous êtes désormais un allié des membres de l'antique suite à votre formation avec " + former.getName());
    }

    public Kagune getKagune() {
        return this.kagune;
    }

    public void setDamager(Player damager) {
        this.damager = damager == null ? null : damager.getUniqueId();
    }

    public boolean haveActiveTask() {
        return this.activeTask != null;
    }

    public void setActiveTask(BukkitRunnable activeTask) {
        this.activeTask = activeTask;
    }

    public boolean isDamaged() {
        return this.damager != null;
    }
}
