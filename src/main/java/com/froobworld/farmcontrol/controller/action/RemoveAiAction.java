package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class RemoveAiAction extends Action {

    public RemoveAiAction() {
        super("remove-ai", Mob.class, false, true, true);
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        mob.setAI(false);
    }

    @Override
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        mob.setAI(true);
        mob.setVelocity(new Vector(0, 0, 0));
    }
}
