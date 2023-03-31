package com.froobworld.farmcontrol.api;

import com.froobworld.farmcontrol.api.action.Action;
import com.froobworld.farmcontrol.api.action.ActionBuilder;
import org.bukkit.entity.Mob;

import java.util.function.Predicate;

/**
 * The core access to the FarmControl API.
 */
public interface FarmControlApi {

    /**
     * Creates an ActionBuilder instance for a new action of a given name.
     *
     * @param actionName the name of the action
     * @return the ActionBuilder instance
     */
    ActionBuilder createAction(String actionName);

    /**
     * Registers an action.
     *
     * @param action the action to be registered
     */
    void registerAction(Action action);

    /**
     * Unregisters an action.
     *
     * @param action the action to be unregistered.
     */
    void unregisterAction(Action action);

    /**
     * Registers an exclusion predicate which will exclude mobs from having actions performed on them.
     *
     * @param exclusionPredicate the exclusion predicate to register
     */
    void registerExclusion(Predicate<Mob> exclusionPredicate);

    /**
     * Unregisters an exclusion predicate.
     *
     * @param exclusionPredicate the exclusion predicate to unregister
     */
    void unregisterExclusion(Predicate<Mob> exclusionPredicate);

}
