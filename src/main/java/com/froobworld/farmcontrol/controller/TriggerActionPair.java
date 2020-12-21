package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.trigger.Trigger;

import java.util.Objects;

public class TriggerActionPair {
    public final Trigger trigger;
    public final Action action;

    public TriggerActionPair(Trigger trigger, Action action) {
        this.trigger = trigger;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerActionPair that = (TriggerActionPair) o;
        return Objects.equals(trigger, that.trigger) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trigger, action);
    }
}
