package fr.hysekai.tokyo;

import fr.hysekai.tokyo.command.type.TgCommand;
import fr.hysekai.tokyo.manager.*;
import fr.hysekai.tokyo.option.Options;
import fr.hysekai.tokyo.listener.GameListener;
import fr.hysekai.tokyo.listener.PlayerListener;
import fr.hysekai.tokyo.quinque.craft.QuinqueRecipe;
import fr.hysekai.tokyo.role.type.Ghoul;
import fr.hysekai.tokyo.role.type.Human;
import fr.hysekai.tokyo.role.type.antique.*;
import fr.hysekai.tokyo.task.GameTask;
import fr.hysekai.tokyo.util.CommandUtils;
import fr.hysekai.tokyo.util.OrderedTeam;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.game.team.MultiTeam;
import fr.hysekai.uhcapi.game.team.TeamType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class TokyoGhoulPlugin extends JavaPlugin {

    private Options options;

    private RoleManager roleManager;
    private EffectManager effectManager;
    private BoardManager boardManager;
    private AntiqueManager antiqueManager;

    public static boolean testMode = true;

    private static TokyoGhoulPlugin instance;

    private void addTeam(UltraHardcoreAPI instance, String name, DyeColor dye, ChatColor color, int order) {
        OrderedTeam team = new OrderedTeam(instance, name, TeamType.TEAM_4.getCode(), color, dye, order);
        team.setMaxSize(6);
        instance.getGameManager().getTeamManager().getTeams().add(team);
    }

    @Override
    public void onEnable() {
        instance = this;

        // Setup Module
        UltraHardcoreAPI.getInstance().getGameManager().setModule(new TokyoGhoolModule());

        // Options
        this.options = new Options();

        // Register new teams
        List<MultiTeam> teams = UltraHardcoreAPI.getInstance().getGameManager().getTeamManager().getTeams();
        teams.clear();
        UltraHardcoreAPI api = UltraHardcoreAPI.getInstance();
        this.addTeam(api, "Cyan", DyeColor.CYAN, ChatColor.AQUA, 1);
        this.addTeam(api, "Bleu", DyeColor.BLUE, ChatColor.BLUE, 2);
        this.addTeam(api, "Vert", DyeColor.GREEN, ChatColor.GREEN, 3);
        this.addTeam(api, "Orange", DyeColor.ORANGE, ChatColor.GOLD, 4);
        this.addTeam(api, "Rouge", DyeColor.RED, ChatColor.RED, 5);
        this.addTeam(api, "Rose", DyeColor.PINK, ChatColor.LIGHT_PURPLE, 6);
        this.addTeam(api, "Jaune", DyeColor.YELLOW, ChatColor.YELLOW, 7);

        // Setup Managers
        this.roleManager = new RoleManager();
        this.effectManager = new EffectManager();
        this.boardManager = new BoardManager(this);
        this.antiqueManager = new AntiqueManager(this);
        CraftManager craftManager = new CraftManager();

        // Register Recipes
        craftManager.addRecipes(new QuinqueRecipe());
        craftManager.enableCraftEvents(false); // Useless event

        // Register Events
        Arrays.asList(
                new GameListener(this),
                new PlayerListener(this))
                .forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        // Register Commands
        CommandUtils.injectCommand(this,
                new TgCommand(this)
        );

        this.registerTasks(); // Test
        UltraHardcoreAPI.getInstance().getGameManager().getGameConfiguration().setInvinsible(false); // test
        UltraHardcoreAPI.getInstance().getGameManager().getGameConfiguration().setPvp(true); // test

    }

    @Override
    public void onDisable() {
    }

    public void registerTasks() {
        new GameTask(this).runTaskTimer(this, 20L, 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("test")) {
            final Player player = (Player) sender;
            if (args.length < 1) {
                testMode = !testMode;
                sender.sendMessage(ChatColor.GREEN + "Test Mode: " + testMode);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "setup":
                    this.roleManager.assignRoles();
                    this.roleManager.setupRoles();
                    break;
                case "colombe":
                    this.roleManager.setRole(player, new Human(true));
                    break;
                case "innocent":
                    this.roleManager.setRole(player, new Human(false));
                    break;
                case "ghoul":
                    this.roleManager.setRole(player, new Ghoul());
                    break;
                case "kuzen":
                    this.roleManager.setRole(player, new Kuzen());
                    break;
                case "kaneki":
                    this.roleManager.setRole(player, new Kaneki());
                    break;
                case "toka":
                    this.roleManager.setRole(player, new Toka());
                    break;
                case "renji":
                    this.roleManager.setRole(player, new Renji());
                    break;
                case "nishiki":
                    this.roleManager.setRole(player, new Nishiki());
                    break;
                case "kaya":
                    this.roleManager.setRole(player, new Kaya());
                    break;
                case "enji":
                    this.roleManager.setRole(player, new Enji());
                    break;
                case "food":
                    player.setFoodLevel(2);
                    player.sendMessage(ChatColor.GREEN + "T'as plus de bouffe cheh");
                    break;
                case "damage":
                    player.damage(args.length < 2 ? 2 : Double.parseDouble(args[1]));
                    break;
                case "switch":
                    UltraHardcoreAPI.getInstance().getGameManager().getEpisodeManager().switchEpisode();
                    break;
                case "rallied":
                    this.roleManager.getRole(player).setRallied(player, player, true);
                    break;
                case "quinque":
                    player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 2), new ItemStack(Material.IRON_BLOCK, 3), new ItemStack(Material.REDSTONE_BLOCK, 1), new ItemStack(Material.GOLD_SWORD, 1), new ItemStack(Material.DIAMOND, 2));
                    break;
                case "tpback":
                    player.teleport(new Location(Bukkit.getWorld("world"), 0, 4, 0));
                    break;
            }
        }

        return true;
    }

    public RoleManager getRoleManager() {
        return this.roleManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public AntiqueManager getAntiqueManager() {
        return antiqueManager;
    }

    public Options getOptions() {
        return options;
    }

    public static TokyoGhoulPlugin getInstance() {
        return instance;
    }
}
