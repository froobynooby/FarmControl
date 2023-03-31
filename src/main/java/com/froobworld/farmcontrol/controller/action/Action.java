package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Mob;

public abstract class Action implements com.froobworld.farmcontrol.api.action.Action {
    private final String name;
    private final boolean removes;
    private final boolean persistent;

    public Action(String name, boolean removes, boolean persistent) {
        if (!name.matches("[a-z-]+")) {
            throw new IllegalArgumentException("Name must match [a-z-]+");
        }
        this.name = name;
        this.removes = removes;
        this.persistent = persistent;
    }

    public final String getName() {
        return name;
    }

    public final boolean removes() {
        return removes;
    }

    public final boolean isPersistent() {
        return persistent;
    }

    public abstract void doAction(Mob mob);

    public abstract void undoAction(Mob mob);

}
