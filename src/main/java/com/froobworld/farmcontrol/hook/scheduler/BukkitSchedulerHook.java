package com.froobworld.farmcontrol.hook.scheduler;

import com.froobworld.farmcontrol.FarmControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class BukkitSchedulerHook implements SchedulerHook {
    private final FarmControl farmControl;

    public BukkitSchedulerHook(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    @Override
    public ScheduledTask runTask(Runnable runnable) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(farmControl, runnable).getTaskId());
    }

    @Override
    public ScheduledTask runTaskAsap(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        }
        return runTask(runnable);
    }

    @Override
    public ScheduledTask runRepeatingTask(Runnable runnable, long initDelay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(farmControl, runnable, initDelay, period));
    }

    @Override
    public ScheduledTask runEntityTask(Runnable runnable, Runnable retired, Entity entity) {
        return runTask(runnable);
    }

    @Override
    public ScheduledTask runEntityTaskAsap(Runnable runnable, Runnable retired, Entity entity) {
        if (Bukkit.isPrimaryThread()) {
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
        return runTask(runnable);
    }

    private static class BukkitScheduledTask implements ScheduledTask {
        private final int taskId;

        private BukkitScheduledTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void cancel() {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        @Override
        public boolean isCancelled() {
            return !Bukkit.getScheduler().isQueued(taskId) && !Bukkit.getScheduler().isCurrentlyRunning(taskId);
        }
    }

}
