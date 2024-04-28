package com.froobworld.farmcontrol.hook.nms.mobgoal;

import org.bukkit.entity.Mob;
import org.joor.Reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.joor.Reflect.*;

public abstract class BaseMobGoalNmsHook implements MobGoalNmsHook {
    private final String wrappedGoalsFieldName; // Set<WrappedGoal> in GoalSelector
    private final String unwrappedGoalFieldName; // Goal in WrappedGoal
    private final String mobClassName; // NMS class for mobs
    private final String goalSelectorClassName; // NMS class for goal selector
    private final String wrappedGoalClassName; // NMS class for wrapped goals
    private final String unwrappedGoalClassName; // NMS class for unwrapped goals (i.e. just "goals")
    private final List<String> randomMovementClassNames; // NMS classes for random movement goals
    private final List<String> goalSelectorFieldNames = new ArrayList<>(); // mobs have two goal selectors (one for goals, one for targets)
    private final Set<Class<?>> randomMovementGoalClasses = new HashSet<>();
    private boolean compatible = true;

    public BaseMobGoalNmsHook(String wrappedGoalsFieldName, String unwrappedGoalFieldName, String mobClassName, String goalSelectorClassName, String wrappedGoalClassName, String unwrappedGoalClassName, List<String> randomMovementClassNames) {
        this.wrappedGoalsFieldName = wrappedGoalsFieldName;
        this.unwrappedGoalFieldName = unwrappedGoalFieldName;
        this.mobClassName = mobClassName;
        this.goalSelectorClassName = goalSelectorClassName;
        this.wrappedGoalClassName = wrappedGoalClassName;
        this.unwrappedGoalClassName = unwrappedGoalClassName;
        this.randomMovementClassNames = randomMovementClassNames;
        initWrappedGoalsGetter();
        initGoalUnwrapper();
        initWrappedGoalsGetter();
        initRandomMovementGoalClasses();
    }

    @Override
    public Object[] getGoalSelectors(Mob mob) {
        Reflect mobHandle = on(mob).call("getHandle");
        Object[] goalSelectors = new Object[goalSelectorFieldNames.size()];
        for (int i = 0; i < goalSelectorFieldNames.size(); i++) {
            goalSelectors[i] = mobHandle.get(goalSelectorFieldNames.get(i));
        }
        return goalSelectors;
    }

    @Override
    public Set<Object> getWrappedGoals(Object goalSelector) {
        return on(goalSelector).get(wrappedGoalsFieldName);
    }

    @Override
    public Object unwrapGoal(Object wrappedGoal) {
        return on(wrappedGoal).get(unwrappedGoalFieldName);
    }

    @Override
    public Set<Class<?>> getRandomMovementGoalClasses() {
        return randomMovementGoalClasses;
    }

    @Override
    public boolean isCompatible() {
        return compatible;
    }

    private void initWrappedGoalsGetter() {
        try {
            Class<?> entityInsentientClass = Class.forName(mobClassName);
            Class<?> goalSelectorClass = Class.forName(goalSelectorClassName);
            for (Field field : entityInsentientClass.getDeclaredFields()) {
                if (field.getType().equals(goalSelectorClass)) {
                    goalSelectorFieldNames.add(field.getName());
                }
            }
            if (goalSelectorFieldNames.isEmpty()) {
                compatible = false; // found no goal selectors
                return;
            }
            boolean foundField = false;
            for (Field field : goalSelectorClass.getDeclaredFields()) {
                if (field.getName().equals(wrappedGoalsFieldName)) {
                    if (!Set.class.isAssignableFrom(field.getType())) {
                        compatible = false; // found the field, but it's the wrong type
                        break;
                    }
                    foundField = true;
                    break;
                }
            }
            if (!foundField) {
                compatible = false; // couldn't find a matching field
            }
        } catch (ClassNotFoundException ignored) {}
    }

    private void initGoalUnwrapper() {
        try {
            Class<?> wrappedGoalClass = Class.forName(wrappedGoalClassName);
            Class<?> unwrappedGoalClass = Class.forName(unwrappedGoalClassName);
            boolean foundField = false;
            for (Field field : wrappedGoalClass.getDeclaredFields()) {
                if (field.getName().equals(unwrappedGoalFieldName)) {
                    if (!unwrappedGoalClass.isAssignableFrom(field.getType())) {
                        compatible = false; // found the field, but it's the wrong type
                        break;
                    }
                    foundField = true;
                    break;
                }
            }
            if (!foundField) {
                compatible = false; // couldn't find a matching field
            }
        } catch (ClassNotFoundException ignored) {}
    }

    private void initRandomMovementGoalClasses() {
        try {
            for (String className : randomMovementClassNames) {
                randomMovementGoalClasses.add(Class.forName(className));
            }

        } catch (Exception ignored) {}
        if (randomMovementGoalClasses.isEmpty()) {
            compatible = false; // none of our random movement classes exist
        }
    }

}
