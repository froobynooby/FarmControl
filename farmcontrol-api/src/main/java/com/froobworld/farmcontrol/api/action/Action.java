package com.froobworld.farmcontrol.api.action;

/**
 * Actions are applied to mobs as part of profiles.
 */
public interface Action {

    /**
     * Gives the name of the action.
     *
     * @return the name of the action
     */
    String getName();

    /**
     * Gives whether the action is persistent once applied (i.e. won't be automatically undone after a server restart).
     *
     * @return whether the action is persistent
     */
    boolean isPersistent();

    /**
     * Gives whether the action will result in the mob being removed from the game.
     *
     * @return whether the action removes
     */
    boolean removes();

}
