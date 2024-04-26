package com.froobworld.farmcontrol.controller.movement;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.utils.NmsUtils;
import com.google.common.collect.MapMaker;
import org.bukkit.entity.Mob;

import java.util.*;

import static org.joor.Reflect.*;

public class BehaviourSuppressor {
    private static final Set<Class<?>> randomMovementBehaviours = new HashSet<>();
    private static final List<String> randomMovementBehaviourStrings = List.of("BehaviorStrollRandom", "BehaviorStrollRandomUnconstrained");
    private static final Class<?> gateBehaviourClass;
    private static final Class<?> doNothingClass;
    private final Map<Mob, Map<Object, Set<Object>>> entityRemovedBehavioursMap = new MapMaker().weakKeys().makeMap();
    private final Map<Mob, Map<Object, Object>> entitySubstitutedGateBehavioursMap = new MapMaker().weakKeys().makeMap();

    static {
        Class<?> tempGateBehaviourClass = null;
        Class<?> tempDoNothingClass = null;
        try {
            randomMovementBehaviours.add(Class.forName(NmsUtils.getFullyQualifiedClassName("BehaviorStrollRandom", "world.entity.ai.behavior")));
            randomMovementBehaviours.add(Class.forName(NmsUtils.getFullyQualifiedClassName("LongJumpToPreferredBlock", "world.entity.ai.behavior")));
            tempGateBehaviourClass = Class.forName(NmsUtils.getFullyQualifiedClassName("BehaviorGateSingle", "world.entity.ai.behavior"));
            tempDoNothingClass = Class.forName(NmsUtils.getFullyQualifiedClassName("BehaviorNop", "world.entity.ai.behavior"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        gateBehaviourClass = tempGateBehaviourClass;
        doNothingClass = tempDoNothingClass;
    }

    public void cleanUp(FarmControl farmControl) {
        List<Mob> mobs = new ArrayList<>(entityRemovedBehavioursMap.keySet());
        for (Mob mob : mobs) {
            farmControl.getHookManager().getSchedulerHook().runEntityTaskAsap(() -> {
                if (!mob.isValid()) {
                    entityRemovedBehavioursMap.remove(mob);
                }
            }, () -> entityRemovedBehavioursMap.remove(mob), mob);
        }
    }

    public void suppress(Mob mob) {
        Object entityObject = on(mob).call("getHandle").get();
        Collection<?> behaviourMaps = on(entityObject)
                .call("dO") // get brain
                .field("f") // Map<priority, Map<Activity, Set<Behaviour>>>
                .as(Map.class)
                .values(); // Collection<Map<Activity, Set<Behaviour>>>

        for (Object map : behaviourMaps) {
            Map<?, ?> behaviourMap = (Map<?, ?>) map;
            for (Object activity : behaviourMap.keySet()) {
                Iterator<?> behaviourIterator = ((Set<?>) behaviourMap.get(activity)).iterator();
                while (behaviourIterator.hasNext()) {
                    Object behaviour = behaviourIterator.next();
                    boolean isRandomMovement = false;
                    try {
                        String debugString = on(behaviour).call("b").get().toString();
                        System.out.println(debugString);
                        for (String randomMovementBehaviourString : randomMovementBehaviourStrings) {
                            if (debugString.contains(randomMovementBehaviourString)) {
                                isRandomMovement = true;
                                break;
                            }
                        }
                    } catch (Throwable ignored) {}
                    isRandomMovement = isRandomMovement || randomMovementBehaviours.contains(behaviour.getClass());
                    if (isRandomMovement) {
                        behaviourIterator.remove();
                        entityRemovedBehavioursMap.compute(mob, (k, v) -> v == null ? new HashMap<>() : v)
                                .compute(activity, (k, v) -> v == null ? new HashSet<>() : v)
                                .add(behaviour);
                    }

                    // if it's a gate behaviour we'll need to substitute any random walk activities for do nothing activities
                    if (gateBehaviourClass != null && gateBehaviourClass.equals(behaviour.getClass())) {
                        List<Object> gateEntries = on(behaviour)
                                .field("e") // ShufflingList
                                .field("a").get(); // List<ShufflingList.WeightedEntry>
                        for (Object gateEntry : gateEntries) {
                            Object subBehaviour = on(gateEntry).field("a").get(); // behaviour
                            System.out.println(subBehaviour.getClass());
                            isRandomMovement = false;
                            try {
                                String debugString = on(subBehaviour).call("b").get().toString();
                                System.out.println(debugString);
                                for (String randomMovementBehaviourString : randomMovementBehaviourStrings) {
                                    if (debugString.contains(randomMovementBehaviourString)) {
                                        isRandomMovement = true;
                                        break;
                                    }
                                }
                            } catch (Throwable ignored) {}
                            isRandomMovement = isRandomMovement || randomMovementBehaviours.contains(subBehaviour.getClass());
                            if (isRandomMovement) {
                                // replace the behaviour with a "do nothing" behaviour and store it for later
                                on(gateEntry).set("a", onClass(doNothingClass).create(0, 0));
                                entitySubstitutedGateBehavioursMap.compute(mob, (k, v) -> v == null ? new HashMap<>() : v)
                                        .put(gateEntry, subBehaviour);
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void unsuppress(Mob mob) {
        Map<Object, Set<Object>> removedBehaviours = entityRemovedBehavioursMap.remove(mob);
        if (removedBehaviours != null) {
            Object entityObject = on(mob).call("getHandle").get();
            Collection<?> behaviourMaps = on(entityObject)
                    .call("dO") // get brain
                    .field("f") // Map<priority, Map<Activity, Set<Behaviour>>>
                    .as(Map.class)
                    .values(); // Collection<Map<Activity, Set<Behaviour>>>

            for (Object map : behaviourMaps) {
                Map<Object, Set<Object>> behaviourMap = (Map<Object, Set<Object>>) map;
                for (Object activity : behaviourMap.keySet()) {
                    if (removedBehaviours.containsKey(activity)) {
                        Set<Object> behaviourSet = behaviourMap.get(activity);
                        behaviourSet.addAll(removedBehaviours.get(activity));
                    }
                }
            }
        }
        Map<Object, Object> substitutedGateBehaviours = entitySubstitutedGateBehavioursMap.remove(mob);
        if (substitutedGateBehaviours != null) {
            for (Object gateBehaviour : substitutedGateBehaviours.keySet()) {
                on(gateBehaviour).set("a", substitutedGateBehaviours.get(gateBehaviour));
            }
        }
    }

}
