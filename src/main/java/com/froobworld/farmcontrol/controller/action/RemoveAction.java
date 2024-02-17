package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Entity;

public class RemoveAction extends Action {

    public RemoveAction() {
        super("remove", Entity.class, true, false, false);
    }

    @Override
    public void doAction(Entity entity) {
        entity.remove();
    }

    @Override
    public void undoAction(Entity entity) {}
}
