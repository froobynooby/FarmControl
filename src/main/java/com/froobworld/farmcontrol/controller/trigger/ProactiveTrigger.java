package com.froobworld.farmcontrol.controller.trigger;

import org.bukkit.World;

public class ProactiveTrigger extends Trigger {

    public ProactiveTrigger() {
        super("proactive");
    }

    @Override
    public TriggerStatus getTriggerStatus(World world) {
        return TriggerStatus.TRIGGERED;
    }

    @Override
    public UntriggerStrategy getUntriggerStrategy(World world) {
        return null;
    }
}
