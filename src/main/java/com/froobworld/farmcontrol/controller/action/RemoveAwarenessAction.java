package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;

public class RemoveAwarenessAction extends Action {

    public RemoveAwarenessAction() {
        super("remove-awareness", false, true, true);
    }

    @Override
    public void doAction(Mob mob) {
        mob.setAware(false);
    }

    @Override
    public void undoAction(Mob mob) {
        mob.setAware(true);
    }
}
