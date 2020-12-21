package com.froobworld.farmcontrol.controller.trigger;

import org.bukkit.World;

public abstract class Trigger {
    private final String name;

    public Trigger(String name) {
        if (!name.matches("[a-z-]+")) {
            throw new IllegalArgumentException("Name must match [a-z-]+");
        }
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public abstract TriggerStatus getTriggerStatus(World world);

    public abstract UntriggerStrategy getUntriggerStrategy(World world);

    public enum TriggerStatus {
        TRIGGERED,
        IDLE,
        UNTRIGGERED
    }

}
