package com.froobworld.farmcontrol.api.event;

import com.froobworld.farmcontrol.api.action.Action;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * An event called immediately after an action is applied to an entity.
 */
public class PostEntityActionEvent extends EntityEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Action action;

    public PostEntityActionEvent(Entity entity, Action action) {
        super(entity);
        this.action = action;
    }

    /**
     * Gives the action that has been applied to the entity.
     *
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
