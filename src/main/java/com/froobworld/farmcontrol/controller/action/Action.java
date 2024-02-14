package com.froobworld.farmcontrol.controller.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public abstract class Action {
    private final String name;
    private final boolean removes;
    private final boolean persistent;
    private final boolean undoOnUnload;

    public Action(String name, boolean removes, boolean persistent, boolean undoOnUnload) {
        if (!name.matches("[a-z-]+")) {
            throw new IllegalArgumentException("Name must match [a-z-]+");
        }
        this.name = name;
        this.removes = removes;
        this.persistent = persistent;
        this.undoOnUnload = undoOnUnload;
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

    public boolean undoOnUnload() {
        return undoOnUnload;
    }

    public abstract void doAction(Entity entity);

    public abstract void undoAction(Entity entity);
}
