package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import com.froobworld.farmcontrol.controller.FarmController;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.controller.tracker.CycleTracker;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.controller.trigger.UntriggerStrategy;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TriggerCheckTask implements Runnable {
    private final FarmControl farmControl;
    private final FarmController farmController;
    private final ExecutorService executorService;
    private final Map<World, Map<Trigger, Set<ActionProfile>>> worldTriggerProfilesMap;
    private final Map<UUID, Map<Trigger, Integer>> worldLastTriggerCount = new HashMap<>();

    public TriggerCheckTask(FarmControl farmControl, FarmController farmController, Map<World, Map<Trigger, Set<ActionProfile>>> worldTriggerProfilesMap) {
        this.farmControl = farmControl;
        this.farmController = farmController;
        executorService = Executors.newFixedThreadPool(1,
                new ThreadFactoryBuilder()
                        .setThreadFactory(Executors.defaultThreadFactory())
                        .setNameFormat("farmcontrol-worker-%d")
                        .build()
        );
        this.worldTriggerProfilesMap = worldTriggerProfilesMap;
    }


    public void run() {
        CycleTracker cycleTracker = farmController.getCycleHistoryManager().startCycleTracker(worldTriggerProfilesMap.keySet());
        for (World world : worldTriggerProfilesMap.keySet()) {
            worldLastTriggerCount.putIfAbsent(world.getUID(), new HashMap<>());
            Map<Trigger, Set<ActionProfile>> triggerProfilesMap = worldTriggerProfilesMap.get(world);
            Map<Trigger, Set<ActionProfile>> profilesToRun = new HashMap<>();
            Map<Trigger, UntriggerStrategy> untriggerStrategyMap = new HashMap<>();
            Set<Trigger> triggeredTriggers = new HashSet<>();

            for (Trigger trigger : triggerProfilesMap.keySet()) {
                Trigger.TriggerStatus triggerStatus = trigger.getTriggerStatus(world);
                if (triggerStatus == Trigger.TriggerStatus.TRIGGERED) {
                    triggeredTriggers.add(trigger);
                    profilesToRun.computeIfAbsent(trigger, t -> new HashSet<>()).addAll(triggerProfilesMap.get(trigger));
                    worldLastTriggerCount.get(world.getUID()).put(trigger, 0);
                } else if (triggerStatus == Trigger.TriggerStatus.UNTRIGGERED) {
                    untriggerStrategyMap.put(trigger, trigger.getUntriggerStrategy(world));
                    worldLastTriggerCount.get(world.getUID()).compute(trigger, (t, v) -> v == null ? 1 : (v + 1));
                }
            }

            untriggerStrategyMap.entrySet().removeIf(entry -> worldLastTriggerCount.get(world.getUID()).getOrDefault(entry.getKey(), 0) <= entry.getValue().getMinimumCyclesBeforeUndo());
            CompletableFuture<List<SnapshotEntity>> completableFuture = CompletableFuture.completedFuture(null);
            if (!profilesToRun.isEmpty() || !untriggerStrategyMap.isEmpty()) {
                completableFuture = farmControl.getHookManager().getEntityGetterHook().getSnapshotEntities(world, FarmController.ENTITY_CLASSES);
            }
            completableFuture.thenAccept(snapshotEntities -> {
                if (snapshotEntities == null || snapshotEntities.isEmpty()) {
                    cycleTracker.signalCompletion(world);
                    return;
                }
                if (!profilesToRun.isEmpty()) {
                    executorService.submit(new ActionAllocationTask(farmController, world, farmControl.getHookManager().getSchedulerHook(), triggeredTriggers, snapshotEntities, profilesToRun, farmControl.getExclusionManager().getExclusionPredicate(world), farmControl.getActionManager().getActions(), cycleTracker));
                } else {
                    cycleTracker.signalCompletion(world);
                }
                if (!untriggerStrategyMap.isEmpty()) {
                    executorService.submit(new UntriggerAllocationTask(farmControl, farmController, snapshotEntities, untriggerStrategyMap));
                }
            });
        }
    }

    public void stop() {
        executorService.shutdown();
    }

}
