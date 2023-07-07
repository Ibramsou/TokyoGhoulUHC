package fr.hysekai.tokyo.role;

import fr.hysekai.tokyo.TokyoGhoulPlugin;
import fr.hysekai.tokyo.role.type.Antique;
import fr.hysekai.tokyo.skin.Skin;
import fr.hysekai.tokyo.task.FormationTask;
import fr.hysekai.uhcapi.UltraHardcoreAPI;
import fr.hysekai.uhcapi.utils.VirtualItem;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Role {

    protected static final VirtualItem rottenFlesh = new VirtualItem(Material.ROTTEN_FLESH).setDisplayName(ChatColor.DARK_GREEN + "Chair Putréfié").amount(64);

    protected final RoleType type;

    protected boolean revealed, dead, disconnected, rallied;
    protected int skillUses, maxSkillUses = 1;

    private ChatColor teamColor;
    private FormationTask formation;
    private RoleTab tab;
    protected boolean setup;
    private boolean canForm;
    private boolean changedDimension;
    private long disableSkillTime, disableTimeStamp, ticks;

    public Role(RoleType type) {
        this.type = type;
    }

    public void setup(Player player) {
        final String prefix = ChatColor.GRAY.toString();
        player.sendMessage(ChatColor.GREEN + "Vous êtes " + this.roleName() + ChatColor.GREEN + ".");
        player.sendMessage(prefix + StringUtils.join(this.information(), "\n" + prefix));
        final ItemStack[] items = this.givenItems();
        if (items == null) return;
        player.getInventory().addItem(items);

        if (this.tab == null) this.tab = new RoleTab(this, player);
        this.setup = true;
    }

    public void connect(Player player) {
        if (!this.setup) this.setup(player);
        this.disconnected = false;
        TokyoGhoulPlugin.getInstance().getAntiqueManager().checkPlayer(player);
    }

    public Skin getSkin() {
        return this.type.getSkin();
    }

    public boolean eliminate(Player player, boolean disconnect) {
        if (this.tab != null) {
            if (disconnect) {
                this.disconnected = true;
                TokyoGhoulPlugin.getInstance().getBoardManager().removeTags(this.tab, player);
            } else {
                this.dead = true;
                TokyoGhoulPlugin.getInstance().getBoardManager().updateTags(this.tab, player);
                TokyoGhoulPlugin.getInstance().getRoleManager().getRoleMap().remove(player.getUniqueId());
            }

            this.tab = null;
        }

        return TokyoGhoulPlugin.getInstance().getRoleManager().detectWin(player);
    }

    public abstract void onTick(TokyoGhoulPlugin plugin, Player player, long ticks);

    public void reveal(Player player) {
        this.revealed = true;
        UltraHardcoreAPI.getInstance().broadcast(ChatColor.GREEN + player.getName() + " s'est révélé ! Il est " + this.roleName());
        if (this.formation != null) {
            this.formation.failed(this.formation.getFormer(), ChatColor.RED + "La formation a échoué car " + player.getName() + " s'est révélé.");
        }
    }

    public void updateSkin(Player player, boolean broadcast, boolean includeSelf) {
        Skin skin = this.getSkin();
        if (broadcast) {
            if (includeSelf) {
                this.getSkin().broadcastPacketsIncludeSelf(player);
            } else {
                this.getSkin().broadcastPackets(player);
            }
        } else {
            skin.apply(player);
            if (!this.revealed) {
                Skin.INNOCENT.broadcastPackets(player);
            }
        }

        TokyoGhoulPlugin.getInstance().getBoardManager().updateTags(this.tab, player);
    }

    public boolean canUseSkill(Player player, boolean add) {
        if (this.skillUses >= this.maxSkillUses) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez utiliser cette compétence " + (this.maxSkillUses == 1 ? "qu'une" : "que " + this.maxSkillUses) + " fois par épisode");
            return false;
        } else if (this.skillDisabled()) {
            player.sendMessage(ChatColor.RED + "Votre compétence est temporairement désactivée.");
            return false;
        }

        if (add) {
            this.skillUses++;
        }

        return true;
    }

    public abstract boolean canAffect(Player player, Player target);

    public void disableSkill(int period) {
        this.disableSkillTime = 1000L * period;
        this.disableTimeStamp = System.currentTimeMillis();
    }

    protected boolean skillDisabled() {
        return this.disableTimeStamp != 0 && System.currentTimeMillis() - this.disableTimeStamp <= this.disableSkillTime;
    }

    public void switchEpisode(int episode) {
        this.skillUses = 0;
    }

    public boolean isGhoul() {
        return this.type == RoleType.SAVAGE_GHOUL || this instanceof Antique;
    }

    public boolean isRallied() {
        return this.rallied;
    }

    public void setRallied(Player former, Player player, boolean rallied) {
        this.rallied = rallied;
    }

    public boolean isCanForm() {
        return canForm;
    }

    public boolean isChangedDimension() {
        return changedDimension;
    }

    public void setChangedDimension(boolean changedDimension) {
        this.changedDimension = changedDimension;
    }

    public void setCanForm(boolean canForm) {
        this.canForm = canForm;
    }

    public void setFormation(FormationTask formation) {
        this.formation = formation;
    }

    public void setTab(RoleTab tab) {
        this.tab = tab;
    }

    public RoleTab getTab() {
        return tab;
    }

    public RoleType getType() {
        return this.type;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChatColor teamColor) {
        this.teamColor = teamColor;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean isSetup() {
        return setup;
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    public void addSkillUse() {
        this.skillUses++;
    }

    public long ticks() {
        return this.ticks;
    }

    public void resetTicks() {
        this.ticks = 0;
    }

    public void addTicks() {
        this.ticks += 1;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public abstract String roleName();

    public abstract String[] information();

    public ItemStack[] givenItems() {
        return null;
    }
}
