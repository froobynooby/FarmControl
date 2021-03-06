package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import com.froobworld.farmcontrol.controller.FarmController;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.controller.trigger.UntriggerStrategy;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.World;
import org.bukkit.entity.Mob;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TriggerCheckTask implements Runnable {
    private final FarmControl farmControl;
    private final FarmController farmController;
    private final ExecutorService executorService;
    private final Map<World, Map<Trigger, Set<ActionProfile>>> worldTriggerProfilesMap;
    private final Map<World, Map<Trigger, Integer>> worldLastTriggerCount = new HashMap<>();

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
        for (World world : worldTriggerProfilesMap.keySet()) {
            worldLastTriggerCount.putIfAbsent(world, new HashMap<>());
            Map<Trigger, Set<ActionProfile>> triggerProfilesMap = worldTriggerProfilesMap.get(world);
            Map<Trigger, Set<ActionProfile>> profilesToRun = new HashMap<>();
            Map<Trigger, UntriggerStrategy> untriggerStrategyMap = new HashMap<>();
            Set<Trigger> triggeredTriggers = new HashSet<>();

            for (Trigger trigger : triggerProfilesMap.keySet()) {
                Trigger.TriggerStatus triggerStatus = trigger.getTriggerStatus(world);
                if (triggerStatus == Trigger.TriggerStatus.TRIGGERED) {
                    triggeredTriggers.add(trigger);
                    profilesToRun.computeIfAbsent(trigger, t -> new HashSet<>()).addAll(triggerProfilesMap.get(trigger));
                    worldLastTriggerCount.get(world).put(trigger, 0);
                } else if (triggerStatus == Trigger.TriggerStatus.UNTRIGGERED) {
                    untriggerStrategyMap.put(trigger, trigger.getUntriggerStrategy(world));
                    worldLastTriggerCount.get(world).compute(trigger, (t, v) -> v == null ? 1 : (v + 1));
                }
            }

            untriggerStrategyMap.entrySet().removeIf(entry -> worldLastTriggerCount.get(world).getOrDefault(entry.getKey(), 0) <= entry.getValue().getMinimumCyclesBeforeUndo());
            List<SnapshotEntity> snapshotEntities = new ArrayList<>();
            if (!profilesToRun.isEmpty() || !untriggerStrategyMap.isEmpty()) {
                for (Mob entity : world.getEntitiesByClass(Mob.class)) {
                    snapshotEntities.add(new SnapshotEntity(entity));
                }
            }
            if (!profilesToRun.isEmpty()) {
                executorService.submit(new ActionAllocationTask(farmController, triggeredTriggers, snapshotEntities, profilesToRun, farmControl.getExclusionManager().getExclusionPredicate(world), farmControl.getActionManager().getActions()));
            }
            if (!untriggerStrategyMap.isEmpty()) {
                executorService.submit(new UntriggerAllocationTask(farmControl, farmController, snapshotEntities, untriggerStrategyMap));
            }
        }
    }

    public void stop() {
        executorService.shutdown();
    }

}
