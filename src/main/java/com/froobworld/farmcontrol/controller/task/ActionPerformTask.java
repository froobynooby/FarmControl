package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.data.FcData;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.Mob;

import java.util.Map;
import java.util.Set;

public class ActionPerformTask implements Runnable {
    private final Map<SnapshotEntity, Set<TriggerActionPair>> triggerActionMap;
    private final Map<SnapshotEntity, Set<TriggerActionPair>> unTriggerActionMap;

    public ActionPerformTask(Map<SnapshotEntity, Set<TriggerActionPair>> triggerActionMap, Map<SnapshotEntity, Set<TriggerActionPair>> unTriggerActionMap) {
        this.triggerActionMap = triggerActionMap;
        this.unTriggerActionMap = unTriggerActionMap;
    }

    @Override
    public void run() {
        for (SnapshotEntity snapshotEntity : triggerActionMap.keySet()) {
            Mob entity = snapshotEntity.getEntity();
            if (!entity.isValid()) {
                continue;
            }
            FcData fcData = FcData.getOrCreate(entity);
            Set<TriggerActionPair> triggerActionPairs = triggerActionMap.get(snapshotEntity);
            for (TriggerActionPair triggerActionPair : triggerActionPairs) {
                if (fcData.add(triggerActionPair.trigger, triggerActionPair.action)) {
                    triggerActionPair.action.doAction(entity);
                }
            }
            fcData.save(entity);
        }
        for (SnapshotEntity snapshotEntity : unTriggerActionMap.keySet()) {
            Mob entity = snapshotEntity.getEntity();
            if (!entity.isValid()) {
                continue;
            }
            FcData fcData = snapshotEntity.getFcData();
            if (fcData == null) {
                continue;
            }
            Set<TriggerActionPair> triggerActionPairs = unTriggerActionMap.get(snapshotEntity);
            for (TriggerActionPair triggerActionPair : triggerActionPairs) {
                if (fcData.remove(triggerActionPair.trigger, triggerActionPair.action)) {
                    triggerActionPair.action.undoAction(entity);
                }
            }
            fcData.save(entity);
            FcData.removeIfEmpty(entity);
        }
    }

}
