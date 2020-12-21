package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;

public class KillAction extends Action {

    public KillAction() {
        super("kill", true, false);
    }

    @Override
    public void doAction(Mob mob) {
        mob.setHealth(0);
    }

    @Override
    public void undoAction(Mob mob) {}
}
