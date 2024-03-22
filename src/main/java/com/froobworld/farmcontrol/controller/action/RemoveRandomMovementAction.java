package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.utils.NmsUtils;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.*;

import static org.joor.Reflect.*;

public class RemoveRandomMovementAction extends Action {
    private final static Map<Mob, Set<Object>> entityRemovedGoalsMap = new MapMaker().weakKeys().makeMap();
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

    public RemoveRandomMovementAction() {
        super("remove-random-movement", Mob.class, false, false, true);
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }

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
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        Object entityObject = on(mob).call("getHandle").get();
        Set<Object> wrappedGoals = on(entityObject)
                .field(NmsUtils.GoalSelectorHelper.getGoalSelectorFieldName())
                .field("d")
                .as(Set.class);
        Set<Object> removedGoals = entityRemovedGoalsMap.remove(mob);
        if (removedGoals != null) {
            wrappedGoals.addAll(removedGoals);
        }
    }

}
