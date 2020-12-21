package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.Mob;

import java.util.Map;
import java.util.Set;

public class UntriggerPerformTask implements Runnable {
    private final Map<SnapshotEntity, Set<TriggerActionPair>> actionsToUndo;

    public UntriggerPerformTask(Map<SnapshotEntity, Set<TriggerActionPair>> actionsToUndo) {
        this.actionsToUndo = actionsToUndo;
    }

    @Override
    public void run() {
        for (SnapshotEntity snapshotEntity : actionsToUndo.keySet()) {
            Mob entity = snapshotEntity.getEntity();
            if (!entity.isValid()) {
                continue;
            }
            FcData fcData = FcData.get(entity);
            if (fcData == null) {
                continue;
            }
            for (TriggerActionPair triggerActionPair : actionsToUndo.get(snapshotEntity)) {
                if (fcData.remove(triggerActionPair.trigger, triggerActionPair.action)) {
                    triggerActionPair.action.undoAction(entity);
                }
            }
            fcData.save(entity);
            FcData.removeIfEmpty(entity);
        }
    }

}
