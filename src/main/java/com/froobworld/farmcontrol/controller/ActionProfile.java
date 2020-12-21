package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.group.GroupDefinition;
import com.froobworld.farmcontrol.controller.trigger.Trigger;

import java.util.Set;

public class ActionProfile {
    private final Trigger trigger;
    private final GroupDefinition groupDefinition;
    private final Set<Action> actions;
    private final boolean removes;

    public ActionProfile(Trigger trigger, GroupDefinition groupDefinition, Set<Action> actions) {
        this.trigger = trigger;
        this.groupDefinition = groupDefinition;
        this.actions = actions;
        removes = actions.stream().anyMatch(Action::removes);
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public GroupDefinition getGroupDefinition() {
        return groupDefinition;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public boolean removes() {
        return removes;
    }

}
