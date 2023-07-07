package fr.hysekai.tokyo.quinque;

import fr.hysekai.tokyo.item.InteractiveItem;
import fr.hysekai.tokyo.quinque.item.*;

public enum Quinque {
    AUS("Aus", new Aus()),
    ZEBIZU("Zebizu", new Zebizu()),
    NARUKAMI("Narukami", new Narukami()),
    TETORO("Tetoro", new Tetoro()),
    SILVER_CRANE("Crâne d'Argent", new SilverCrane());

    private final String name;
    private final InteractiveItem item;

    Quinque(String name, InteractiveItem item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public InteractiveItem getItem() {
        return item;
    }
}
