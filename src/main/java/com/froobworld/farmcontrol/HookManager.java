package com.froobworld.farmcontrol;

import com.froobworld.farmcontrol.hook.scheduler.BukkitSchedulerHook;
import com.froobworld.farmcontrol.hook.scheduler.RegionisedSchedulerHook;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import com.froobworld.farmcontrol.hook.tick.PaperTickHook;
import com.froobworld.farmcontrol.hook.tick.SpigotTickHook;
import com.froobworld.farmcontrol.hook.tick.TickHook;
import com.froobworld.farmcontrol.utils.MsptTracker;

public class HookManager {
    private final FarmControl farmControl;
    private final TickHook tickHook;
    private final SchedulerHook schedulerHook;
    private MsptTracker msptTracker;

    public HookManager(FarmControl farmControl) {
        this.farmControl = farmControl;
        if (RegionisedSchedulerHook.isCompatible()) {
            schedulerHook = new RegionisedSchedulerHook(farmControl);
        } else {
            schedulerHook = new BukkitSchedulerHook(farmControl);
        }
        if (PaperTickHook.isCompatible()) {
            tickHook = new PaperTickHook();
        } else {
            tickHook = new SpigotTickHook(schedulerHook);
        }
        tickHook.register(farmControl);
    }

    public void load() {
        if (RegionisedSchedulerHook.isCompatible()) {
            return;
        }
        msptTracker = new MsptTracker(
                farmControl.getFcConfig().msptTracker.collectionPeriod.get(),
                tickHook
        );
        msptTracker.register();
    }

    public void reload() {
        if (msptTracker != null) {
            msptTracker.unregister();
        }
        load();
    }

    public MsptTracker getMsptTracker() {
        return msptTracker;
    }

    public SchedulerHook getSchedulerHook() {
        return schedulerHook;
    }

}
