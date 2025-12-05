package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.FarmController;
import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.controller.trigger.UntriggerStrategy;
import com.froobworld.farmcontrol.data.FcData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UntriggerAllocationTask implements Runnable {
    private final FarmControl farmControl;
    private final FarmController farmController;
    private final List<SnapshotEntity> snapshotEntities;
    private final Map<Trigger, UntriggerStrategy> untriggerStrategyMap;

    public UntriggerAllocationTask(FarmControl farmControl, FarmController farmController, List<SnapshotEntity> snapshotEntities, Map<Trigger, UntriggerStrategy> untriggerStrategyMap) {
        this.farmControl = farmControl;
        this.farmController = farmController;
        this.snapshotEntities = snapshotEntities;
        this.untriggerStrategyMap = untriggerStrategyMap;
    }

    public void run() {
        Map<SnapshotEntity, Set<TriggerActionPair>> actionsToUndo = new HashMap<>();
        for (Trigger trigger : untriggerStrategyMap.keySet()) {
            UntriggerStrategy untriggerStrategy = untriggerStrategyMap.get(trigger);
            int undos = 0;
            for (SnapshotEntity entity : snapshotEntities) {
                if (undos >= untriggerStrategy.getMaximumUndosPerCycle()) {
                    break;
                }
                FcData fcData = entity.getFcData();
                if (fcData == null) {
                    continue;
                }
                Set<String> actionStrings = fcData.getActions(trigger);
                if (actionStrings == null || actionStrings.isEmpty()) {
                    continue;
                }
                for (String actionName : actionStrings) {
                    Action action = farmControl.getActionManager().getAction(actionName);
                    actionsToUndo.computeIfAbsent(entity, k -> new HashSet<>()).add(new TriggerActionPair(trigger, action));
                }
                undos += untriggerStrategy.getEntityWeightFunction().apply(entity.getEntityType());
            }
        }
        farmController.submitUntriggerPerformTask(new UntriggerPerformTask(farmControl.getHookManager().getSchedulerHook(), actionsToUndo));
    }

}
