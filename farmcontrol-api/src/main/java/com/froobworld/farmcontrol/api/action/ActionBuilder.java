package com.froobworld.farmcontrol.api.action;

import org.bukkit.entity.Mob;

import java.util.function.Consumer;

public interface ActionBuilder {

    /**
     * Set whether the action is persistent (i.e. the action is not automatically undone after a server restart).
     *
     * @param persistent whether the action is persistent
     * @return the ActionBuilder instance
     */
    ActionBuilder setPersistent(boolean persistent);

    /**
     * Set whether the action will result in the mob being removed from the game.
     *
     * @param removes whether the action removes
     * @return the ActionBuilder instance
     */
    ActionBuilder setRemoves(boolean removes);

    /**
     * Set the 'do' action which is applied to the mob.
     *
     * @param consumer the 'do' action
     * @return the ActionBuilder instance
     */
    ActionBuilder onDoAction(Consumer<Mob> consumer);

    /**
     * Set the 'undo' action which undoes the 'do' action.
     *
     * @param consumer the 'undo' action
     * @return the ActionBuilder instance
     */
    ActionBuilder onUndoAction(Consumer<Mob> consumer);

    /**
     * Build the {@link Action}.
     *
     * @return the action
     */
    Action build();

}
