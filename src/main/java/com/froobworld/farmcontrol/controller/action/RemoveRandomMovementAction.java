package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.hook.nms.mobgoal.MobGoalNmsHook;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.*;

public class RemoveRandomMovementAction extends Action {
    private final static Map<Mob, Map<Object, Set<Object>>> entityRemovedGoalsMap = new MapMaker().weakKeys().makeMap();
    private final MobGoalNmsHook nmsHook;

    public static void cleanUp(FarmControl farmControl) {
        List<Mob> mobs = new ArrayList<>(entityRemovedGoalsMap.keySet());
        for (Mob mob : mobs) {
            farmControl.getHookManager().getSchedulerHook().runEntityTaskAsap(() -> {
                if (!mob.isValid()) {
                    entityRemovedGoalsMap.remove(mob);
                }
            }, () -> entityRemovedGoalsMap.remove(mob), mob);
        }
    }

    public RemoveRandomMovementAction(MobGoalNmsHook nmsHook) {
        super("remove-random-movement", Mob.class, false, false, true);
        this.nmsHook = nmsHook;
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }

        for (Object goalSelector : nmsHook.getGoalSelectors(mob)) {
            Set<Object> wrappedGoals = nmsHook.getWrappedGoals(goalSelector);
            Set<Object> removedGoals = new HashSet<>();

            Iterator<Object> goalIterator = wrappedGoals.iterator();
            while (goalIterator.hasNext()) {
                Object nextGoal = goalIterator.next();
                Class<?> nextClass = nmsHook.unwrapGoal(nextGoal).getClass();

                if (nmsHook.getRandomMovementGoalClasses().contains(nextClass)) {
                    goalIterator.remove();
                    removedGoals.add(nextGoal);
                }
            }
            entityRemovedGoalsMap
                    .compute(mob, (k, v) -> v == null ? new HashMap<>() : v)
                    .compute(goalSelector, (k, v) -> v == null ? removedGoals : Sets.union(removedGoals, v));
        }
    }

    @Override
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        Map<Object, Set<Object>> removedGoalsMap = entityRemovedGoalsMap.remove(mob);
        if (removedGoalsMap == null) {
            return;
        }
        Iterator<Object> goalSelectorIterator = removedGoalsMap.keySet().iterator();
        while (goalSelectorIterator.hasNext()) {
            Object goalSelector = goalSelectorIterator.next();
            Set<Object> wrappedGoals = nmsHook.getWrappedGoals(goalSelector);
            Set<Object> removedGoals = removedGoalsMap.get(goalSelector);
            if (removedGoals != null) {
                wrappedGoals.addAll(removedGoals);
            }
            goalSelectorIterator.remove();
        }
    }

}
