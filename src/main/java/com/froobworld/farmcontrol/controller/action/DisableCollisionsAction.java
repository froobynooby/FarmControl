package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class DisableCollisionsAction extends Action {

    public DisableCollisionsAction() {
        super("disable-collisions", Mob.class, false, false, true);
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }

        mob.setCollidable(false);
    }

    @Override
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        mob.setCollidable(true);
    }
}
