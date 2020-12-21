package com.froobworld.farmcontrol.controller.trigger;

import org.bukkit.entity.EntityType;

import java.util.function.Function;

public class UntriggerStrategy {
    private final int maximumUndosPerCycle;

    private final Function<EntityType, Double> entityWeight;
    private final int minimumCyclesBeforeUndo;

    public UntriggerStrategy(int maximumUndosPerCycle, Function<EntityType, Double> entityWeight, int minimumCyclesBeforeUndo) {
        this.maximumUndosPerCycle = maximumUndosPerCycle;
        this.entityWeight = entityWeight;
        this.minimumCyclesBeforeUndo = minimumCyclesBeforeUndo;
    }

    public int getMaximumUndosPerCycle() {
        return maximumUndosPerCycle;
    }

    public Function<EntityType, Double> getEntityWeightFunction() {
        return entityWeight;
    }

    public int getMinimumCyclesBeforeUndo() {
        return minimumCyclesBeforeUndo;
    }

}
