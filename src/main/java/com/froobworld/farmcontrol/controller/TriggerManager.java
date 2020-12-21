package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.trigger.ProactiveTrigger;
import com.froobworld.farmcontrol.controller.trigger.Trigger;

import java.util.HashMap;
import java.util.Map;

public class TriggerManager {
    private Map<String, Trigger> triggers = new HashMap<>();

    public void addDefaults(FarmControl farmControl) {
        addTrigger(new ProactiveTrigger());
    }

    public void addTrigger(Trigger trigger) {
        triggers.put(trigger.getName(), trigger);
    }

    public Trigger getTrigger(String triggerName) {
        return triggers.get(triggerName);
    }

}
