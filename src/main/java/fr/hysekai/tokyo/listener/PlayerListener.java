package fr.hysekai.tokyo.listener;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.item.InteractiveAction;
import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.option.items.OpenGuiVirtualItem;
import fr.hysekai.tokyo.packet.PacketInjector;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.tokyo.role.type.antique.Enji;
import fr.hysekai.tokyo.role.type.antique.Nishiki;
import fr.hysekai.tokyo.task.JumpTask;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.utils.gui.AbstractGui;
import io.netty.channel.ChannelHandler;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final TokyoGhoulPlugin plugin;

    public PlayerListener(TokyoGhoulPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();
        entityplayer.playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", "tg-handler", new PacketInjector(player));

        Role role = this.plugin.getRoleManager().getRole(player);

        if (role == null || role.isDead()) {
            player.setMaxHealth(20);
            return;
        }

        boolean match = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams().stream().anyMatch(multiTeam -> multiTeam.getChatColor() == role.getTeamColor());

        if (match) {
            role.connect(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();

        ChannelHandler handler = entityplayer.playerConnection.networkManager.channel.pipeline().get("tg-handler");

        if (handler != null) {
            entityplayer.playerConnection.networkManager.channel.pipeline().remove(handler);
        }

        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null || role.isDead()) return;
        role.eliminate(player, true);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        final Player player = event.getPlayer();
        final Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null || !role.isGhoul()) return;

        ItemStack item = event.getItem();
        final int amount = item.getAmount();
        if (item.getType() == Material.ROTTEN_FLESH) {
            player.setFoodLevel(player.getFoodLevel() + 8);
        } else if (item.getType() != Material.GOLDEN_APPLE) {
            @SuppressWarnings("deprecation")
            final ItemFood food = (ItemFood) Item.getById(item.getTypeId());
            int nutrition = food.getNutrition(null) / 2;
            player.setFoodLevel(player.getFoodLevel() + nutrition);
            if (Math.random() * 100 <= 60) {
                this.plugin.getEffectManager().selfDamage(player, 1.0D);
            }
        } else {
            return;
        }

        event.setCancelled(true);

        if (amount > 1) item.setAmount(amount - 1);
        else item = new ItemStack(Material.AIR);
        player.setItemInHand(item);
        player.updateInventory();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        ItemStack itemStack = event.getItemInHand();
        InteractiveItem item = InteractiveItem.getItem(itemStack);
        if (item != null) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        if (event.getAction() == Action.PHYSICAL) return;
        ItemStack itemStack = event.getItem();
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role != null && clicked != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (role.getType() == RoleType.COLOMBE) {
                Human human = (Human) role;
                if (human.containsBlock(clicked)) {
                    clicked.setType(Material.AIR);
                    return;
                }
            }
        }
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
        InteractiveItem item = InteractiveItem.getItem(itemStack);
        if (item == null) return;
        if (System.currentTimeMillis() - item.lastInteract < 250) return;
        item.lastInteract = System.currentTimeMillis();
        item.onClick(role, player, null, event.getAction().name().contains("LEFT") ? InteractiveAction.LEFT_CLICK : InteractiveAction.RIGHT_CLICK);
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractAt(PlayerInteractAtEntityEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        org.bukkit.entity.Entity entity = event.getRightClicked();
        if (!(entity instanceof Player)) return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
        InteractiveItem item = InteractiveItem.getItem(itemStack);
        if (item == null) return;
        if (System.currentTimeMillis() - item.lastInteract < 250) return;
        item.lastInteract = System.currentTimeMillis();
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null) return;
        item.onClick(role, player, (Player) entity, InteractiveAction.ENTITY_CLICK);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        Player player = event.getPlayer();
        org.bukkit.entity.Item item = event.getItemDrop();
        if (item == null || item.getItemStack().getType() == Material.AIR) return;
        ItemStack itemStack = item.getItemStack();
        InteractiveItem interactiveItem = InteractiveItem.getItem(itemStack);
        if (interactiveItem == null) return;
        event.setCancelled(true);
        player.setItemOnCursor(null);
        player.updateInventory();
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        org.bukkit.entity.Entity entityAttacker = event.getDamager();
        org.bukkit.entity.Entity entityDamaged = event.getEntity();

        if (entityAttacker.getType() == EntityType.PLAYER && entityDamaged.getType() == EntityType.PLAYER) {
            Player attacker = (Player) entityAttacker;
            Player victim = (Player) entityDamaged;

            ItemStack itemStack = attacker.getItemInHand();
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
            InteractiveItem item = InteractiveItem.getItem(itemStack);
            if (item == null) return;
            Role roleA = this.plugin.getRoleManager().getRole(attacker);
            if (roleA == null) return;
            Role roleB = this.plugin.getRoleManager().getRole(victim);
            if (roleB == null) return;
            item.onAttack(roleA, roleB, attacker, victim, event);
        }
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        Player player = event.getPlayer();
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null || role.getType() != RoleType.ENJI) return;
        Enji enji = (Enji) role;
        enji.setCanJump(false);
        player.setFlying(false);
        player.setAllowFlight(false);
        new JumpTask(this.plugin, enji, player).runTaskTimerAsynchronously(this.plugin, 0L, 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && (TokyoGhoulPlugin.testMode || role.getType() == RoleType.NISHIKI)) {
            event.setCancelled(true);
        } else {
            this.plugin.getBoardManager().updateHealths(role.getTab(), player, player.getHealth() - event.getFinalDamage());
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!this.plugin.getRoleManager().isReady()) return;
        Player player = (Player) event.getEntity();
        Role role = this.plugin.getRoleManager().getRole(player);
        if (role == null || role.getType() != RoleType.NISHIKI) return;
        int food = event.getFoodLevel();
        if (food >= player.getFoodLevel()) return;
        Nishiki nishiki = (Nishiki) role;
        if (nishiki.foodTicks() % 3 != 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        String name = event.getInventory().getName();
        if (!name.equals("§aConfiguration")) return;
        AbstractGui gui = AbstractGui.guiMap.get(name);
        Inventory inventory = event.getInventory();
        if (gui != null) {
            OpenGuiVirtualItem item = new OpenGuiVirtualItem(gui);
            gui.addItems(31, item);
            inventory.setItem(31, item);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalEnter(EntityPortalEnterEvent event) {
        org.bukkit.entity.Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }

        if (this.plugin.getAntiqueManager().isLocked()) return;
        Location locBack = this.plugin.getAntiqueManager().getLocationBack(player);
        if (locBack == null) return;
        player.teleport(locBack);
        player.sendMessage(ChatColor.GREEN + "Vous êtes retourné dans votre dimension d'origine");
    }
}
