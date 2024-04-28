package com.froobworld.farmcontrol;

import com.froobworld.farmcontrol.hook.entitygetter.BukkitEntityGetterHook;
import com.froobworld.farmcontrol.hook.entitygetter.EntityGetterHook;
import com.froobworld.farmcontrol.hook.entitygetter.RegionisedEntityGetterHook;
import com.froobworld.farmcontrol.hook.nms.NmsHooks;
import com.froobworld.farmcontrol.hook.scheduler.BukkitSchedulerHook;
import com.froobworld.farmcontrol.hook.scheduler.RegionisedSchedulerHook;
import com.froobworld.farmcontrol.hook.scheduler.SchedulerHook;
import com.froobworld.farmcontrol.hook.tick.PaperTickHook;
import com.froobworld.farmcontrol.hook.tick.BukkitTickHook;
import com.froobworld.farmcontrol.hook.tick.TickHook;
import com.froobworld.farmcontrol.utils.MsptTracker;

public class HookManager {
    private final FarmControl farmControl;
    private final NmsHooks nmsHooks;
    private final TickHook tickHook;
    private final SchedulerHook schedulerHook;
    private final EntityGetterHook entityGetterHook;
    private MsptTracker msptTracker;

    public HookManager(FarmControl farmControl) {
        this.farmControl = farmControl;
        nmsHooks = new NmsHooks();
        if (RegionisedSchedulerHook.isCompatible()) {
            schedulerHook = new RegionisedSchedulerHook(farmControl);
        } else {
            schedulerHook = new BukkitSchedulerHook(farmControl);
        }
        if (RegionisedSchedulerHook.isCompatible()) {
            tickHook = null;
        } else if (PaperTickHook.isCompatible()) {
            tickHook = new PaperTickHook();
        } else if (nmsHooks.getTickTimeNmsHook() != null) {
            tickHook = new BukkitTickHook(schedulerHook, nmsHooks.getTickTimeNmsHook());
        } else {
            tickHook = null;
        }
        if (RegionisedEntityGetterHook.isCompatible()) {
            entityGetterHook = new RegionisedEntityGetterHook(farmControl);
        } else {
            entityGetterHook = new BukkitEntityGetterHook();
        }
        if (tickHook != null) {
            tickHook.register(farmControl);
        }
    }

    public void load() {
        if (tickHook != null) {
            msptTracker = new MsptTracker(
                    farmControl.getFcConfig().msptTracker.collectionPeriod.get(),
                    tickHook
            );
            msptTracker.register();
        }
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

    public TickHook getTickHook() {
        return tickHook;
    }

    public SchedulerHook getSchedulerHook() {
        return schedulerHook;
    }

    public EntityGetterHook getEntityGetterHook() {
        return entityGetterHook;
    }

    public NmsHooks getNmsHooks() {
        return nmsHooks;
    }
}
