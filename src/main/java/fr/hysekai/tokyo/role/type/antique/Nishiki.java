package fr.hysekai.tokyo.role.type.antique;

import fr.hysekai.tokyo.role.RoleType;
import fr.hysekai.tokyo.role.type.Antique;

public class Nishiki extends Antique {

    private int foodTick;

    public Nishiki() {
        super(RoleType.NISHIKI);
    }

    @Override
    public String[] information() {
        return null;
    }

    public int foodTicks() {
        this.foodTick += 1;
        return this.foodTick;
    }
}
