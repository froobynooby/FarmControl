package com.froobworld.farmcontrol.hook.scheduler;

import org.bukkit.entity.Entity;

public interface SchedulerHook {

    ScheduledTask runTask(Runnable runnable);

    ScheduledTask runTaskAsap(Runnable runnable);

    ScheduledTask runRepeatingTask(Runnable runnable, long initDelay, long period);

    ScheduledTask runEntityTask(Runnable runnable, Runnable retired, Entity entity);

    ScheduledTask runEntityTaskAsap(Runnable runnable, Runnable retired, Entity entity);

}
