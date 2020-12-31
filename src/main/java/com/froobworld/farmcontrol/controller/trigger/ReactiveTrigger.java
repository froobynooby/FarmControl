package com.froobworld.farmcontrol.controller.trigger;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.config.FcConfig;
import org.bukkit.World;

public class ReactiveTrigger extends Trigger {
    private final FarmControl farmControl;

    public ReactiveTrigger(FarmControl farmControl) {
        super("reactive");
        this.farmControl = farmControl;
    }

    @Override
    public TriggerStatus getTriggerStatus(World world) {
        boolean useMspt = farmControl.getFcConfig().paperSettings.worldSettings.of(world).alternativeReactiveModeSettings.useAlternativeSettings.get();
        useMspt &= farmControl.getHookManager().getMsptTracker() != null;

        if (useMspt) {
            double mspt = farmControl.getHookManager().getMsptTracker().getMspt();
            if (mspt >= farmControl.getFcConfig().paperSettings.worldSettings.of(world).alternativeReactiveModeSettings.triggerMsptThreshold.get()) {
                return TriggerStatus.TRIGGERED;
            } else if (mspt <= farmControl.getFcConfig().paperSettings.worldSettings.of(world).alternativeReactiveModeSettings.untriggerMsptThreshold.get()) {
                return TriggerStatus.UNTRIGGERED;
            }
        } else {
            double tps = farmControl.getHookManager().getTpsTracker().getTps();
            if (tps <= farmControl.getFcConfig().worldSettings.of(world).reactiveModeSettings.triggerTpsThreshold.get()) {
                return TriggerStatus.TRIGGERED;
            } else if (tps >= farmControl.getFcConfig().worldSettings.of(world).reactiveModeSettings.untriggerTpsThreshold.get()) {
                return TriggerStatus.UNTRIGGERED;
            }
        }
        return TriggerStatus.IDLE;
    }

    @Override
    public UntriggerStrategy getUntriggerStrategy(World world) {
        FcConfig.WorldSettings.ReactiveModeSettings.UntriggerSettings untriggerSettings = farmControl.getFcConfig().worldSettings.of(world).reactiveModeSettings.untriggerSettings;
        return new UntriggerStrategy(
                untriggerSettings.maximumUndosPerCycle.get(),
                entityType -> untriggerSettings.entityUndoWeight.of(entityType).get(),
                untriggerSettings.minimumCyclesBeforeUndo.get()
        );
    }
}
