package com.froobworld.farmcontrol.hook.tick;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.hook.nms.tick.TickTimesNmsHook;
import com.froobworld.farmcontrol.hook.scheduler.ScheduledTask;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.joor.Reflect.on;

public class BukkitTickHook implements TickHook {
    private final long[] tickTimes;
    private final Set<Consumer<Long>> tickConsumers = new HashSet<>();
    private final SchedulerHook schedulerHook;
    private ScheduledTask scheduledTask;

    public BukkitTickHook(SchedulerHook schedulerHook, TickTimesNmsHook nmsHook) {
        this.schedulerHook = schedulerHook;
        this.tickTimes = nmsHook.getTickTimes();
    }

    @Override
    public void register(FarmControl farmControl) {
        if (scheduledTask == null) {
            scheduledTask = schedulerHook.runRepeatingTask(
                    () -> tickConsumers.forEach(consumer -> consumer.accept(getLastTickTime())),
                    0, 1
            );
        }
    }

    @Override
    public boolean addTickConsumer(Consumer<Long> consumer) {
        return tickConsumers.add(consumer);
    }

    @Override
    public boolean removeTickConsumer(Consumer<Long> consumer) {
        return tickConsumers.remove(consumer);
    }

    private long getLastTickTime() {
        int currentTick = getCurrentTick();
        if (currentTick < 1) {
            return 0;
        }
        return tickTimes == null ? 0 : tickTimes[(currentTick - 1) % tickTimes.length];
    }

    private static int getCurrentTick() {
        return on(Bukkit.getScheduler())
                .get("currentTick");
    }

}
