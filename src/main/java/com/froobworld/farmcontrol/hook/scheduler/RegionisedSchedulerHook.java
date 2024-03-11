package com.froobworld.farmcontrol.hook.scheduler;

import com.froobworld.farmcontrol.FarmControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class RegionisedSchedulerHook implements SchedulerHook {
    private final FarmControl farmControl;

    public RegionisedSchedulerHook(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    @Override
    public ScheduledTask runTask(Runnable runnable) {
        return new RegionisedScheduledTask(Bukkit.getGlobalRegionScheduler().run(farmControl, task -> runnable.run()));
    }

    @Override
    public ScheduledTask runTaskAsap(Runnable runnable) {
        if (Bukkit.isGlobalTickThread()) {
            runnable.run();
        }
        return runTask(runnable);
    }

    @Override
    public ScheduledTask runRepeatingTask(Runnable runnable, long initDelay, long period) {
        return new RegionisedScheduledTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(farmControl, task -> runnable.run(), initDelay, period));
    }

    @Override
    public ScheduledTask runEntityTask(Runnable runnable, Runnable retired, Entity entity) {
        io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask = entity.getScheduler().run(farmControl, task -> runnable.run(), retired);
        return scheduledTask == null ? null : new RegionisedScheduledTask(scheduledTask);
    }

    @Override
    public ScheduledTask runEntityTaskAsap(Runnable runnable, Runnable retired, Entity entity) {
        if (Bukkit.isOwnedByCurrentRegion(entity)) {
            runnable.run();
            return new ScheduledTask() {
                @Override
                public void cancel() {}

                @Override
                public boolean isCancelled() {
                    return false;
                }
            };
        }
        return runEntityTask(runnable, retired, entity);
    }

    public static boolean isCompatible() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private static class RegionisedScheduledTask implements ScheduledTask {
        private final io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask;

        private RegionisedScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override
        public void cancel() {
            scheduledTask.cancel();
        }

        @Override
        public boolean isCancelled() {
            return scheduledTask.isCancelled();
        }
    }

}
