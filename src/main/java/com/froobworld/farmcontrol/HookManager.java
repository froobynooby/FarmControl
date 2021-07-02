package com.froobworld.farmcontrol;

import com.froobworld.farmcontrol.hook.tick.PaperTickHook;
import com.froobworld.farmcontrol.hook.tick.SpigotTickHook;
import com.froobworld.farmcontrol.hook.tick.TickHook;
import com.froobworld.farmcontrol.utils.MsptTracker;
import com.froobworld.farmcontrol.utils.TpsTracker;

public class HookManager {
    private final FarmControl farmControl;
    private final TickHook tickHook;
    private TpsTracker tpsTracker;
    private MsptTracker msptTracker;

    public HookManager(FarmControl farmControl) {
        this.farmControl = farmControl;
        if (PaperTickHook.isCompatible()) {
            tickHook = new PaperTickHook();
        } else {
            tickHook = new SpigotTickHook();
        }
        tickHook.register(farmControl);
    }

    public void load() {
        tpsTracker = new TpsTracker(
                farmControl.getFcConfig().tpsTracker.collectionPeriod.get(),
                tickHook,
                farmControl.getFcConfig().tpsTracker.trimOutliersPercent.get()
        );
        tpsTracker.register();
        if (tickHook instanceof PaperTickHook) {
            msptTracker = new MsptTracker(
                    farmControl.getFcConfig().paperSettings.msptTracker.collectionPeriod.get(),
                    tickHook
            );
            msptTracker.register();
        }
    }

    public void reload() {
        tpsTracker.unregister();
        if (msptTracker != null) {
            msptTracker.unregister();
        }
        load();
    }

    public TpsTracker getTpsTracker() {
        return tpsTracker;
    }

    public MsptTracker getMsptTracker() {
        return msptTracker;
    }

}
