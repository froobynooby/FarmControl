package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.data.FcData;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.Map;
import java.util.Set;

public class UntriggerPerformTask implements Runnable {
    private final SchedulerHook schedulerHook;
    private final Map<SnapshotEntity, Set<TriggerActionPair>> actionsToUndo;

    public UntriggerPerformTask(SchedulerHook schedulerHook, Map<SnapshotEntity, Set<TriggerActionPair>> actionsToUndo) {
        this.schedulerHook = schedulerHook;
        this.actionsToUndo = actionsToUndo;
    }

    @Override
    public void run() {
        for (SnapshotEntity snapshotEntity : actionsToUndo.keySet()) {
            Entity entity = snapshotEntity.getEntity();
            schedulerHook.runEntityTaskAsap(() -> {
                if (!(entity instanceof Mob mob) || !entity.isValid()) {
                    return;
                }
                FcData fcData = FcData.get(entity);
                if (fcData == null) {
                    return;
                }
                for (TriggerActionPair triggerActionPair : actionsToUndo.get(snapshotEntity)) {
                    if (fcData.remove(triggerActionPair.trigger, triggerActionPair.action)) {
                        triggerActionPair.action.undoAction(mob);
                    }
                }
                fcData.save(entity);
                FcData.removeIfEmpty(entity);
            }, null, entity);
        }
    }

}
