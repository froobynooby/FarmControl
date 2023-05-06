package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.controller.breeding.BreedingBlocker;
import org.bukkit.entity.Mob;

public class DisableBreedingAction extends Action {
    private final BreedingBlocker breedingBlocker;

    public DisableBreedingAction(BreedingBlocker breedingBlocker) {
        super("disable-breeding", false, true, false);
        this.breedingBlocker = breedingBlocker;
    }

    @Override
    public void doAction(Mob mob) {
        breedingBlocker.setBreedingDisabled(mob, true);
    }

    @Override
    public void undoAction(Mob mob) {
        breedingBlocker.setBreedingDisabled(mob, false);
    }
}
