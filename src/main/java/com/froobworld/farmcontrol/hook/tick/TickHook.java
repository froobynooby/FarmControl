package com.froobworld.farmcontrol.hook.tick;

import com.froobworld.farmcontrol.FarmControl;

import java.util.function.Consumer;

public interface TickHook {

    void register(FarmControl farmControl);

    boolean addTickConsumer(Consumer<Long> consumer);

    boolean removeTickConsumer(Consumer<Long> consumer);

}
