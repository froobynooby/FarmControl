package com.froobworld.farmcontrol.hook.nms.tick;

public interface TickTimesNmsHook {

    long[] getTickTimes();

    boolean isCompatible();

}
