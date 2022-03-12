package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.utils.NmsUtils;
import com.google.common.collect.Sets;
import org.bukkit.entity.Mob;

import java.util.*;

import static org.joor.Reflect.on;

public class RemoveRandomMovementAction extends Action {
    private final static Map<Mob, Set<Object>> entityRemovedGoalsMap = new WeakHashMap<>();
    private final static Set<Class<?>> randomMovementGoals = new HashSet<>();

    static {
        try {
            randomMovementGoals.add(Class.forName(NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomFly", "world.entity.ai.goal")));
            randomMovementGoals.add(Class.forName(NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomStroll", "world.entity.ai.goal")));
            randomMovementGoals.add(Class.forName(NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomStrollLand", "world.entity.ai.goal")));
            randomMovementGoals.add(Class.forName(NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomSwim", "world.entity.ai.goal")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void cleanUp() {
        entityRemovedGoalsMap.entrySet().removeIf(entry -> !entry.getKey().isValid());
    }

    public RemoveRandomMovementAction() {
        super("remove-random-movement", false, false);
    }

    @Override
    public void doAction(Mob mob) {
        Object entityObject = on(mob).call("getHandle").get();
        Set<?> wrappedGoals = on(entityObject)
                .field(NmsUtils.GoalSelectorHelper.getGoalSelectorFieldName())
                .field("d")
                .as(Set.class);
        Iterator<?> goalIterator = wrappedGoals.iterator();
        Set<Object> removedGoals = new HashSet<>();
        while (goalIterator.hasNext()) {
            Object next = goalIterator.next();
            Class<?> nextClass = on(next)
                    .field("a")
                    .get().getClass();
            if (randomMovementGoals.contains(nextClass)) {
                goalIterator.remove();
                removedGoals.add(next);
            }
        }
        entityRemovedGoalsMap.compute(mob, (k, v) -> v == null ? removedGoals : Sets.union(removedGoals, v));
    }

    @Override
    public void undoAction(Mob mob) {
        Object entityObject = on(mob).call("getHandle").get();
        Set wrappedGoals = on(entityObject)
                .field(NmsUtils.GoalSelectorHelper.getGoalSelectorFieldName())
                .field("d")
                .as(Set.class);
        Set<Object> removedGoals = entityRemovedGoalsMap.remove(mob);
        if (removedGoals != null) {
            wrappedGoals.addAll(removedGoals);
        }
    }

}
