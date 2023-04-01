package com.froobworld.farmcontrol.hook.scheduler;

public interface ScheduledTask {

    void cancel();

    boolean isCancelled();

}
