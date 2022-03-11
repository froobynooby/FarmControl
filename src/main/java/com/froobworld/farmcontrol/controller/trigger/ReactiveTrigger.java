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
        double mspt = farmControl.getHookManager().getMsptTracker().getMspt();
        if (mspt >= farmControl.getFcConfig().worldSettings.of(world).reactiveModeSettings.triggerMsptThreshold.get()) {
            return TriggerStatus.TRIGGERED;
        } else if (mspt <= farmControl.getFcConfig().worldSettings.of(world).reactiveModeSettings.untriggerMsptThreshold.get()) {
            return TriggerStatus.UNTRIGGERED;
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
