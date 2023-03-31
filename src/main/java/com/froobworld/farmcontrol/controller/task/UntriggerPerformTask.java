package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.api.event.PostEntityActionUndoEvent;
import com.froobworld.farmcontrol.api.event.PreEntityActionUndoEvent;
import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.data.FcData;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import org.bukkit.Bukkit;
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
            Mob entity = snapshotEntity.getEntity();
            schedulerHook.runEntityTaskAsap(() -> {
                if (!entity.isValid()) {
                    return;
                }
                FcData fcData = FcData.get(entity);
                if (fcData == null) {
                    return;
                }
                for (TriggerActionPair triggerActionPair : actionsToUndo.get(snapshotEntity)) {
                    if (fcData.remove(triggerActionPair.trigger, triggerActionPair.action)) {
                        Bukkit.getPluginManager().callEvent(new PreEntityActionUndoEvent(entity, triggerActionPair.action));
                        triggerActionPair.action.undoAction(entity);
                        Bukkit.getPluginManager().callEvent(new PostEntityActionUndoEvent(entity, triggerActionPair.action));
                    }
                }
                fcData.save(entity);
                FcData.removeIfEmpty(entity);
            }, null, entity);
        }
    }

}
