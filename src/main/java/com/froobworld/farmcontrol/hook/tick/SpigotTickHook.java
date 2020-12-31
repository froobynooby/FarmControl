package com.froobworld.farmcontrol.hook.tick;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.utils.NmsUtils;
import org.bukkit.Bukkit;
import org.joor.Reflect;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.joor.Reflect.*;

public class SpigotTickHook implements TickHook {
    private static final Reflect currentTick = onClass(NmsUtils.getFullyQualifiedClassName("MinecraftServer")).field("currentTick");

    private final Set<Consumer<Integer>> tickStartCallbacks = new HashSet<>();
    private final Set<Consumer<Integer>> tickEndCallbacks = new HashSet<>();
    private Integer taskId;

    @Override
    public void register(FarmControl farmControl) {
        if (taskId == null) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    farmControl,
                    () -> {
                        int currentTick = getCurrentTick();
                        tickEndCallback(currentTick - 1);
                        tickStartCallback(currentTick);
                    },
                    0, 1
            );
        }
    }

    @Override
    public boolean addTickStartCallback(Consumer<Integer> consumer) {
        return tickStartCallbacks.add(consumer);
    }

    @Override
    public boolean removeTickStartCallback(Consumer<Integer> consumer) {
        return tickStartCallbacks.remove(consumer);
    }

    @Override
    public boolean addTickEndCallback(Consumer<Integer> consumer) {
        return tickEndCallbacks.add(consumer);
    }

    @Override
    public boolean removeTickEndCallback(Consumer<Integer> consumer) {
        return tickEndCallbacks.add(consumer);
    }

    private void tickStartCallback(int tickNumber) {
        tickStartCallbacks.forEach(consumer -> consumer.accept(tickNumber));
    }

    private void tickEndCallback(int tickNumber) {
        tickEndCallbacks.forEach(consumer -> consumer.accept(tickNumber));
    }

    private static int getCurrentTick() {
        return currentTick.get();
    }

}
