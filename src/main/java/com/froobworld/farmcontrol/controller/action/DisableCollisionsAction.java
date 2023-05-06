package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;

public class DisableCollisionsAction extends Action {

    public DisableCollisionsAction() {
        super("disable-collisions", false, false, true);
    }

    @Override
    public void doAction(Mob mob) {
        mob.setCollidable(false);
    }

    @Override
    public void undoAction(Mob mob) {
        mob.setCollidable(true);
    }
}
