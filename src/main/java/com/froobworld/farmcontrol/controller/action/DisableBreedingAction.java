package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.controller.breeding.BreedingBlocker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class DisableBreedingAction extends Action {
    private final BreedingBlocker breedingBlocker;

    public DisableBreedingAction(BreedingBlocker breedingBlocker) {
        super("disable-breeding", Mob.class, false, true, false);
        this.breedingBlocker = breedingBlocker;
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob)) {
            return;
        }
        breedingBlocker.setBreedingDisabled(entity, true);
    }

    @Override
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        breedingBlocker.setBreedingDisabled(mob, false);
    }
}
