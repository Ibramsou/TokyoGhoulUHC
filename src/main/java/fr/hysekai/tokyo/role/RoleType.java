package fr.hysekai.tokyo.role;

import fr.hysekai.tokyo.skin.Skin;

public enum RoleType {
    /* Humans */
    INNOCENT("Innocent", Skin.INNOCENT),
    COLOMBE("Colombe", Skin.COLOMBE),

    /* Ghoul */
    SAVAGE_GHOUL( "Ghoul Sauvage", Skin.SAVAGE_GHOUL),

    /* Antique Members */
    KUZEN("Kuzen Yoshimura", Skin.KUZEN),
    TOKA("Toka Kirishima", Skin.TOKA),
    KANEKI("Kaneki Ken", Skin.KANEKI),
    NISHIKI("Nishiki Nishio", Skin.NISHIKI),
    ENJI("Enji Koma", Skin.ENJI),
    KAYA("Kaya Irimi", Skin.KAYA),
    RENJI("Renji Yomo", Skin.RENJI),

    UNKNOWN;

    private final String name;
    private final Skin skin;

    RoleType() {
        this.name = null;
        this.skin = null;
    }

    RoleType(String name, Skin skin) {
        this.name = name;
        this.skin = skin;
    }

    public String getName() {
        return this.name;
    }

    public Skin getSkin() {
        return skin;
    }
}
