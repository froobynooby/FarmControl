package com.froobworld.farmcontrol.metrics.charts;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class LegacyReactiveModeIndicatorChart extends SimplePie {

    public LegacyReactiveModeIndicatorChart(FarmControl farmControl) {
        super("reactive_mode_indicator", () -> {
            boolean usingReactiveMode = false;
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        usingReactiveMode = true;
                        break;
                    }
                }
            }
            if (usingReactiveMode && farmControl.getHookManager().getMsptTracker() != null) {
                return "mspt";
            }
            return null;
        });
    }

}
