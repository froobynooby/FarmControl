package com.froobworld.farmcontrol.controller.tracker;

import com.froobworld.farmcontrol.FarmControl;
import org.bukkit.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class CycleHistoryManager {
    private final Queue<CycleStats> cycleStats = new ArrayDeque<>();
    private final NotificationCentre notificationCentre;

    public CycleHistoryManager(FarmControl farmControl) {
        notificationCentre = new NotificationCentre(farmControl);
    }

    public NotificationCentre getNotificationCentre() {
        return notificationCentre;
    }

    public CycleTracker startCycleTracker(Collection<World> worlds) {
        return new CycleTracker(this, worlds);
    }

    synchronized void reportCompletedCycle(CycleStats cycleStats) {
        this.cycleStats.add(cycleStats);
        if (this.cycleStats.size() > 5) {
            this.cycleStats.remove();
        }
        notificationCentre.notify(cycleStats);
    }

    public List<CycleStats> getCycleHistory() {
        return new ArrayList<>(cycleStats);
    }

}
