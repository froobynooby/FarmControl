package com.froobworld.farmcontrol.hook.nms.mobgoal;

import org.bukkit.entity.Mob;

import java.util.Set;

public interface MobGoalNmsHook {

    Object[] getGoalSelectors(Mob mob);

    Set<Object> getWrappedGoals(Object goalSelector);

    Object unwrapGoal(Object wrappedGoal);

    Set<Class<?>> getRandomMovementGoalClasses();

    boolean isCompatible();

}
