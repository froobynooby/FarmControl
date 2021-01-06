package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.group.GroupDefinition;

import java.util.Set;

public class ActionProfile {
    private final GroupDefinition groupDefinition;
    private final Set<Action> actions;
    private final boolean removes;

    public ActionProfile(GroupDefinition groupDefinition, Set<Action> actions) {
        this.groupDefinition = groupDefinition;
        this.actions = actions;
        removes = actions.stream().anyMatch(Action::removes);
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
