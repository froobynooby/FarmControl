package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class RemoveAiAction extends Action {

    public RemoveAiAction() {
        super("remove-ai", false, true, true);
    }

    @Override
    public void doAction(Mob mob) {
        mob.setAI(false);
    }

    @Override
    public void undoAction(Mob mob) {
        mob.setAI(true);
        mob.setVelocity(new Vector(0, 0, 0));
    }
}
