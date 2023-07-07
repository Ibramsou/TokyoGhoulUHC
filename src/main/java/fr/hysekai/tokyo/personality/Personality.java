package fr.hysekai.tokyo.personality;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Personality {
    SASAKI(ChatColor.AQUA + "Sasaki Haise", Material.NETHER_STAR,
            "",
            "Dans cet état, vous avez perdu la mémoire.",
            "Vous devez gagner avec votre équipe ",
            "d'origine.",
            "",
            "Vous recevrez également une Quinque.",
            "Et vous connaitrez la Colombe de",
            "votre équipe."
    ),
    KANEKI(ChatColor.DARK_GREEN + "Kaneki Ken", Material.ANVIL,
            "",
            "Dans cet état, êtes dans votre état normal,",
            "vous devez donc gagner avec",
            "les membres de l'Antique",
            "",
            "Sous cette forme, si vous vous trouvez",
            "à moins de 15 blocs de Toka,",
            "vous recevrez l'effet Resistance 1."
    ),
    KING(ChatColor.RED + "Roi Borgne", Material.NETHER_STALK,
            "",
            "Sous cette forme, vous êtes craint",
            "part toutes les Ghouls et vous devez",
            "gagner seul.",
            "",
            "Si vous vous trouvez à moins de",
            "5 blocs d'une Ghoul, cette dernière",
            "recevra l'effet Faiblesse 1."
    );

    private final String name;
    private final Material material;
    private final String[] description;

    Personality(String name, Material material, String... description) {
        this.name = name;
        this.material = material;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String[] getDescription() {
        String[] desc = new String[this.description.length];
        for (int i = 0; i < this.description.length; i++) desc[i] = ChatColor.GRAY + this.description[i];
        return desc;
    }
}
