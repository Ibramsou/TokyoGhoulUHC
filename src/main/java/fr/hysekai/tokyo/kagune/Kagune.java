package fr.hysekai.tokyo.kagune;

import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.kagune.item.Armor;
import fr.hysekai.tokyo.kagune.item.Dash;
import fr.hysekai.tokyo.kagune.item.Frenzy;
import fr.hysekai.tokyo.kagune.item.Vitality;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Kagune {
    SPEED("Kagune de Vitesse", PotionEffectType.SPEED, new Dash()),
    STRENGTH("Kagune de Puissance", PotionEffectType.INCREASE_DAMAGE, new Frenzy()),
    RESISTANCE("Kagune de Resistance", PotionEffectType.DAMAGE_RESISTANCE, new Armor()),
    VITALITY("Kagune de Vitalité", null, new Vitality());

    private final String name;
    private final PotionEffectType type;
    private final InteractiveItem item;

    Kagune(String name, PotionEffectType type, InteractiveItem item) {
        this.name = name;
        this.type = type;
        this.item = item;
    }

    public PotionEffect getEffect() {
        return type == null ? null : new PotionEffect(type, 99999, 0);
    }

    public String getName() {
        return this.name;
    }

    public InteractiveItem getItem() {
        return this.item;
    }
}
