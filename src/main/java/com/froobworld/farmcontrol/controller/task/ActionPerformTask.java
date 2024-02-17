package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.tracker.CycleTracker;
import com.froobworld.farmcontrol.data.FcData;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.hook.scheduler.ScheduledTask;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ActionPerformTask implements Runnable {
    private final World world;
    private final SchedulerHook schedulerHook;
    private final Map<SnapshotEntity, Set<TriggerActionPair>> triggerActionMap;
    private final Map<SnapshotEntity, Set<TriggerActionPair>> unTriggerActionMap;
    private final CycleTracker cycleTracker;

    public ActionPerformTask(World world, SchedulerHook schedulerHook, Map<SnapshotEntity, Set<TriggerActionPair>> triggerActionMap, Map<SnapshotEntity, Set<TriggerActionPair>> unTriggerActionMap, CycleTracker cycleTracker) {
        this.world = world;
        this.schedulerHook = schedulerHook;
        this.triggerActionMap = triggerActionMap;
        this.unTriggerActionMap = unTriggerActionMap;
        this.cycleTracker = cycleTracker;
    }

    @Override
    public void run() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (SnapshotEntity snapshotEntity : triggerActionMap.keySet()) {
            Entity entity = snapshotEntity.getEntity();
            CompletableFuture<Void> entityFuture = new CompletableFuture<>();
            ScheduledTask scheduledTask = schedulerHook.runEntityTaskAsap(() -> {
                try {
                    if (!entity.isValid()) {
                        return;
                    }
                    FcData fcData = FcData.getOrCreate(entity);
                    Set<TriggerActionPair> triggerActionPairs = triggerActionMap.get(snapshotEntity);
                    for (TriggerActionPair triggerActionPair : triggerActionPairs) {
                        if (fcData.add(triggerActionPair.trigger, triggerActionPair.action)) {
                            triggerActionPair.action.doAction(entity);
                            cycleTracker.reportAction(triggerActionPair.action, snapshotEntity);
                        }
                    }
                    fcData.save(entity);
                } finally {
                    entityFuture.complete(null);
                }
            }, () -> entityFuture.complete(null), entity);
            if (scheduledTask != null) {
                future = future.thenCompose(v -> entityFuture);
            }
        }
        for (SnapshotEntity snapshotEntity : unTriggerActionMap.keySet()) {
            Entity entity = snapshotEntity.getEntity();
            schedulerHook.runEntityTaskAsap(() -> {
                if (!entity.isValid()) {
                    return;
                }
                FcData fcData = snapshotEntity.getFcData();
                if (fcData == null) {
                    return;
                }
                Set<TriggerActionPair> triggerActionPairs = unTriggerActionMap.get(snapshotEntity);
                for (TriggerActionPair triggerActionPair : triggerActionPairs) {
                    if (fcData.remove(triggerActionPair.trigger, triggerActionPair.action)) {
                        triggerActionPair.action.undoAction(entity);
                    }
                }
                fcData.save(entity);
                FcData.removeIfEmpty(entity);
            }, null, entity);
        }
        future.thenRunAsync(() -> schedulerHook.runTask(() -> cycleTracker.signalCompletion(world)));
    }

}
