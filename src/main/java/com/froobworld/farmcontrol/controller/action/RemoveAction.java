package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;

public class RemoveAction extends Action {

    public RemoveAction() {
        super("remove", true, false, false);
    }

    @Override
    public void doAction(Mob mob) {
        mob.remove();
    }

    @Override
    public void undoAction(Mob mob) {}
}
