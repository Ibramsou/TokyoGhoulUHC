package fr.hysekai.tokyo.role.type;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.quinque.craft.QuinqueItem;
import fr.hysekai.tokyo.role.Role;
import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.quinque.Quinque;
import fr.hysekai.tokyo.util.Distances;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Human extends Role {

    private boolean canForm;
    private Quinque quinque;

    private double startX, startY, startZ, endX, endY, endZ;

    private final List<int[]> toBreak = new ArrayList<>(405);
    private final List<int[]> toReplace = new ArrayList<>(405);

    public Human(boolean colombe) {
        super(colombe ? RoleType.COLOMBE : RoleType.INNOCENT);

        this.canForm = colombe;

        if (colombe) {
            Quinque[] quinques = Quinque.values();
            this.quinque = quinques[ThreadLocalRandom.current().nextInt(quinques.length)];
        }
    }

    @Override
    public void setup(Player player) {
        super.setup(player);
        this.updateSkin(player, false, false);
    }

    @Override
    public void connect(Player player) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveHumans().add(player);
        super.connect(player);
    }

    @Override
    public boolean eliminate(Player player, boolean disconnect) {
        TokyoGhoulPlugin.getInstance().getRoleManager().getAliveHumans().remove(player);
        return super.eliminate(player, disconnect);
    }

    @Override
    public ItemStack[] givenItems() {
        return isColombe() ? new ItemStack[] {this.quinque.getItem()} : new ItemStack[] {new ItemStack(Material.GOLDEN_APPLE, 3)};
    }

    @Override
    public String roleName() {
        return isColombe() ? ChatColor.DARK_GREEN + "Une Colombe" : ChatColor.GREEN + "Un Innocent";
    }

    @Override
    public String[] information() {
        return isColombe() ?
                new String[] {
                        " » Vous devez gagner avec votre équipe d'origine",
                        " » En tant que Colombe, vous devez traquer les Ghouls",
                        " ",
                        " » Avec la commande " + ChatColor.BOLD + "/tg former <pseudo>" + ChatColor.GRAY + ", vous pouvez former un innocent afin qu'il devienne un apprenti colombe,",
                        " » Il faudra par la suite que joueur reste à coté de vous pendant une durée de 15 minutes,",
                        " » La formation échouera si la personne que vous avez essayé de former n'est pas un innocent.",
                        " ",
                        " » Vous disposez de la Quinque: " + ChatColor.BOLD + this.quinque.getName()
                } :
                new String[] {
                        " » Vous devez gagner avec votre équipe d'origine",
                        " » Votre rôle ne possède aucune compétance en particulier,",
                        " » Cependant, une colombe peut vous former pour devenir un apprenti colombe",
                        " ",
                        " » Vous avez reçu " + ChatColor.BOLD + "3 pommes d'or"
                };
    }

    @Override
    public void onTick(TokyoGhoulPlugin plugin, Player player, long ticks) {
        if (this.startX == 0 && this.startY == 0 && this.startZ == 0 && this.endX == 0 && this.endY == 0 && this.endZ == 0) return;
        Location location = player.getLocation();
        boolean inArea = Distances.inArea(location, this.startX, this.startY, this.startZ, this.endX, this.endY, this.endZ);
        if (!inArea && !this.toReplace.isEmpty()) {
            World world = player.getWorld();
            this.toReplace.forEach(array -> world.getBlockAt(array[0], array[1], array[2]).setType(Material.WEB));
            this.setArea(0, 0, 0, 0, 0, 0);
            this.toReplace.clear();
            this.toBreak.clear();
        }
    }

    @Override
    public boolean canAffect(Player player, Player target) {
        return true;
    }

    @Override
    public void setRallied(Player former, Player player, boolean rallied) {
        super.setRallied(former, player, rallied);

        Quinque[] quinques = Quinque.values();
        this.quinque = quinques[ThreadLocalRandom.current().nextInt(quinques.length)];

        former.sendMessage(ChatColor.GREEN + "La formation s'est terminée avec succès !");
        player.sendMessage(ChatColor.GREEN + "Vous êtes désormais un apprenti Colombe, suite à la formation que vous avez suivi avec " + player.getName());
        player.sendMessage(ChatColor.GREEN + "Vous pourrez crafter la Quinque" + this.quinque.getName());
        player.sendMessage(ChatColor.GREEN + "Pour voir le craft, cliquez sur le livre qui vous a été donné");
        player.getInventory().addItem(new QuinqueItem());
        player.updateInventory();
    }

    public boolean containsBlock(Block block) {
        for (int[] array : this.toBreak) {
            if (block.getX() == array[0] && block.getY() == array[1] && block.getZ() == array[2]) {
                toReplace.add(array);
                return block.getType() == Material.WEB;
            }
        }

        return false;
    }

    public void setArea(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void addBlocks(List<int[]> blocks) {
        this.toBreak.addAll(blocks);
    }

    public boolean isColombe() {
        return this.type == RoleType.COLOMBE;
    }

    public Quinque getQuinque() {
        return quinque;
    }

    public boolean isCanForm() {
        return this.canForm;
    }

    public void setCanForm(boolean canForm) {
        this.canForm = canForm;
    }
}
