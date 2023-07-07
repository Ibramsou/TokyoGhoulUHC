package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.personality.Personality;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.util.Distances;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class Renji extends Antique {

    public Renji() {
        super(RoleType.RENJI);

        this.maxSkillUses = 2;
    }

    @Override
    public ItemStack[] givenItems() {
        return new ItemStack[] {
                rottenFlesh,
                new LightningItem()
        };
    }

    @Override
    public void switchEpisode(int episode) {}

    @Override
    public String[] information() {
        return null;
    }

    @Override
    public void onTick(TokyoGhoulPlugin plugin, Player player, long ticks) {
        super.onTick(plugin, player, ticks);
        double health = player.getHealth();
        if (ticks % 30 == 0) {
            if (health >= player.getMaxHealth()) return;
            health = Math.min(player.getMaxHealth(), health + 1.0D);
            player.setHealth(health);
        }

        boolean hasEffect = player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        if (health >= 8.0D && hasEffect) {
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        } else if (health < 8.0D && !hasEffect) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 0));
            Location location = player.getLocation();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int size = random.nextInt(3, 5);
            World world = location.getWorld();
            EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
            double x = entityplayer.locX;
            double y = entityplayer.locY;
            double z = entityplayer.locZ;
            for (int i = 0; i < size; i++) {
                plugin.getEffectManager()
                        .spawnLightning(world, x + (random.nextInt() % 4) * 0.75, y, z + (random.nextInt() % 4) * 0.75, 2.0D)
                        .setOwner(player)
                        .strike();
            }
        }
    }

    private class LightningItem extends InteractiveItem {

        public LightningItem() {
            super(Material.IRON_AXE, ChatColor.AQUA + "Sort d'éclair");
        }

        @Override
        public void onAttack(Role attackerRole, Role damagedRole, Player attacker, Player damaged, EntityDamageByEntityEvent event) {

        }

        @Override
        public void onClick(Role role, Player player, Player clicked, InteractiveAction action) {
            if (skillUses >= maxSkillUses) {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette compétence que " + skillUses + " fois dans la partie.");
                return;
            }
            Player target = clicked != null ? clicked : action == InteractiveAction.RIGHT_CLICK ? Distances.rayTrace(player, 30) : null;
            if (target == null) return;
            Role targetRole = TokyoGhoulPlugin.getInstance().getRoleManager().getRole(target);
            if (targetRole == null) return;
            boolean kaneki = targetRole.getType() != RoleType.KANEKI || ((Kaneki) targetRole).getPersonality() == Personality.KANEKI;
            if (targetRole instanceof Antique && kaneki) return;
            Location location = player.getLocation();
            Vector direction = location.getDirection();

            double x = location.getX();
            double y = location.getY() + 1.25;
            double z = location.getZ();

            double distance = Distances.distance(player, target);

            for (double i = 0.5; i < distance; i += 0.25) {
                this.plugin.getEffectManager().broadcastColored(player.getWorld(), x + direction.getX() * i, y + direction.getY() * i, z + direction.getZ() * i, 255, 255, 255);
            }

            Location targetLoc = target.getLocation();
            this.plugin.getEffectManager().spawnLightning(player.getWorld(), targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 0);
            target.damage(4);

            player.sendMessage(ChatColor.GREEN + "Vous avez fait apparaitre un éclair sur " + target.getName());
            target.sendMessage(ChatColor.RED + player.getName() + " a fait apparaitre un éclair sur vous");

            if (targetRole.getType() == RoleType.COLOMBE || targetRole.getType() == RoleType.KANEKI && ((Kaneki) targetRole).getPersonality() == Personality.SASAKI) {
                targetRole.disableSkill(900);
                target.sendMessage(ChatColor.RED + "Suite a cette attaque, votre Quinque a été désactivée pendant 5 minutes.");
            }

            addSkillUse();
        }
    }
}
