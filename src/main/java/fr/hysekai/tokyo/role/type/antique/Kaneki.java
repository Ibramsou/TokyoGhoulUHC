package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.personality.Personality;
import fr.hysekai.tokyo.personality.PersonalityGui;
import fr.hysekai.tokyo.quinque.Quinque;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.skin.Skin;
import fr.hysekai.tokyo.util.Distances;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Kaneki extends Antique {

    private final List<PotionEffectType> toRemove = new ArrayList<>(4);
    private final List<PotionEffect> toAdd = new ArrayList<>(4);

    private Personality personality;
    private Quinque quinque;
    private boolean skillActive;


    public Kaneki() {
        super(RoleType.KANEKI);
        this.maxSkillUses = 2;
    }

    @Override
    public Skin getSkin() {
        if (this.personality == Personality.KING) {
            return Skin.KING;
        } else if (this.personality == Personality.SASAKI) {
            return Skin.SASAKI;
        }

        return super.getSkin();
    }

    @Override
    public void onConnect(Player player) {
        if (!this.toRemove.isEmpty()) {
            this.toRemove.forEach(player::removePotionEffect);
            this.toRemove.clear();
        }

        if (!this.toAdd.isEmpty()) {
            this.toAdd.forEach(player::addPotionEffect);
            this.toAdd.clear();
        }

        if (this.personality == Personality.KING) {
            TokyoGhoulPlugin.getInstance().getRoleManager().setAliveKing(player);
        } else if (this.personality == Personality.SASAKI) {
            TokyoGhoulPlugin.getInstance().getRoleManager().getAliveHumans().add(player);
        } else {
            super.onConnect(player);
        }
    }

    @Override
    protected void onEliminate(Player player) {
        if (this.personality == Personality.KING) {
            TokyoGhoulPlugin.getInstance().getRoleManager().setAliveKing(null);
        } else if (this.personality == Personality.SASAKI) {
            TokyoGhoulPlugin.getInstance().getRoleManager().getAliveHumans().remove(player);
        } else {
            super.onEliminate(player);
        }
    }

    @Override
    public void setup(Player player) {
        super.setup(player);
        Skin.INNOCENT.broadcastPackets(player);
        Bukkit.getScheduler().runTaskLater(TokyoGhoulPlugin.getInstance(), () -> new PersonalityGui(player, this).open(player), 1L);
    }

    @Override
    protected void onSetup(Player player) {}

    @Override
    public void onTick(TokyoGhoulPlugin plugin, Player player, long ticks) {
        if (this.personality == Personality.SASAKI) return;


        super.onTick(plugin, player, ticks);

        if (this.personality == Personality.KING) {
            for (Player target : player.getWorld().getPlayers()) {
                if (target == player) continue;
                Role targetRole = plugin.getRoleManager().getRole(target);
                if (targetRole == null || !targetRole.isGhoul()) continue;
                boolean enoughRange = Distances.distance(target, player) <= 5;
                boolean hasEffect = target.hasPotionEffect(PotionEffectType.WEAKNESS);
                if (hasEffect && !enoughRange) {
                    target.removePotionEffect(PotionEffectType.WEAKNESS);
                } else if (!hasEffect && enoughRange) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 99999, 0));
                }
            }
        } else if (this.personality == Personality.KANEKI) {
            Player target = plugin.getRoleManager().getByType(RoleType.TOKA);
            if (target == null) return;
            boolean hasEffect = player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            boolean enoughRange = Distances.distance(player, target) <= 5;
            if (hasEffect && !enoughRange) {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            } else if (!hasEffect && enoughRange) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 0));
            }
        }
    }

    @Override
    public boolean canAffect(Player player, Player target) {
        return false;
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new KaguneItem()
        };
    }

    @Override
    public void switchEpisode(int episode) {}

    @Override
    public String[] information() {
        return null;
    }

    public void setPersonality(Personality personality) {
        this.personality = personality;
    }

    public Personality getPersonality() {
        return this.personality;
    }

    public void setQuinque(Quinque quinque) {
        this.quinque = quinque;
    }

    public Quinque getQuinque() {
        return quinque;
    }

    private class KaguneItem extends InteractiveItem {

        public KaguneItem() {
            super(Material.NETHER_STALK, ChatColor.RED + "Activer le Kagune");
        }

        @Override
        public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

        }

        @Override
        public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
            if (action != InteractiveAction.RIGHT_CLICK) return;
            if (skillUses >= maxSkillUses) {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette compétence que " + skillUses + " fois par partie.");
                return;
            }
            if (skillActive) {
                player.sendMessage(ChatColor.RED + "Votre Kagune est déjà activé.");
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0));
            if (player.hasPotionEffect(PotionEffectType.SLOW)) player.removePotionEffect(PotionEffectType.SLOW);
            Bukkit.getScheduler().runTaskLater(TokyoGhoulPlugin.getInstance(), () -> {
                final PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 500, 0);
                if (!player.isOnline()) {
                    toRemove.add(PotionEffectType.SPEED);
                    toRemove.add(PotionEffectType.INCREASE_DAMAGE);
                    toAdd.add(effect);
                    return;
                }

                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(effect);
                player.sendMessage(ChatColor.RED + "Votre Kagune vient de se désactiver, vous aurez un effet de lenteur pendant 25 secondes");
                if (personality == Personality.KANEKI) {
                    player.damage(4);
                    player.sendMessage(ChatColor.RED + "Vous avez perdu deux coeur.");
                }
                Location location = player.getLocation();
                TokyoGhoulPlugin.getInstance().getEffectManager().sendSound(player, Sound.ANVIL_LAND, location.getX(), location.getY(), location.getZ(), 1f, 1f);
                skillActive = false;
            }, 600L);
            player.sendMessage(ChatColor.GREEN + "Vous venez d'activer votre Kagune");
            player.sendMessage(ChatColor.GREEN + "Vous aurez les effets Vitesse 1 et et Force 1 pendant 30 secondes.");
            Location location = player.getLocation();
            TokyoGhoulPlugin.getInstance().getEffectManager().sendSound(player, Sound.NOTE_PLING, location.getX(), location.getY(), location.getZ(), 1f, 1f);
            skillActive = true;
            addSkillUse();
        }
    }
}
