package com.froobworld.farmcontrol.controller.tracker;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.World;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CycleTracker {
    private boolean empty = true;
    private final CycleHistoryManager cycleHistoryManager;
    private final CycleStats.Builder statsBuilder;
    private final Set<UUID> worldsWaitingOn;

    CycleTracker(CycleHistoryManager cycleHistoryManager, Collection<World> worldsRequired) {
        this.cycleHistoryManager = cycleHistoryManager;
        statsBuilder = CycleStats.Builder.start(System.currentTimeMillis());
        worldsWaitingOn = worldsRequired.stream()
                .map(World::getUID)
                .collect(Collectors.toSet());
    }

    public void reportAction(Action action, SnapshotEntity entity) {
        statsBuilder.action(action, entity);
        empty = false;
    }

    public synchronized void signalCompletion(World world) {
        worldsWaitingOn.remove(world.getUID());
        if (worldsWaitingOn.isEmpty() && !empty) {
            cycleHistoryManager.reportCompletedCycle(statsBuilder.end(System.currentTimeMillis()));
        }
    }

}
