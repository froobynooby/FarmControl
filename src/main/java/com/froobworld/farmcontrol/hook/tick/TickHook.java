package com.froobworld.farmcontrol.hook.tick;

import com.froobworld.farmcontrol.FarmControl;

import java.util.function.Consumer;

public interface TickHook {

    void register(FarmControl farmControl);

    boolean addTickStartCallback(Consumer<Integer> consumer);

    boolean removeTickStartCallback(Consumer<Integer> consumer);

    boolean addTickEndCallback(Consumer<Integer> consumer);

    boolean removeTickEndCallback(Consumer<Integer> consumer);

}
