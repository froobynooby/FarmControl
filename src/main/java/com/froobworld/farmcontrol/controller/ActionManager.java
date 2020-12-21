package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionManager {
    private final Map<String, Action> actions = new HashMap<>();

    public void addDefaults(FarmControl farmControl) {
        addAction(new DisableCollisionsAction());
        addAction(new KillAction());
        addAction(new RemoveAction());
        addAction(new RemoveAiAction());
        addAction(new RemoveAwarenessAction());
        addAction(new RemoveRandomMovementAction());
    }

    public void addAction(Action action) {
        actions.put(action.getName(), action);
    }

    public Action getAction(String actionName) {
        return actions.get(actionName);
    }

    public Set<Action> getActions() {
        return new HashSet<>(actions.values());
    }

}
