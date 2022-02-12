package com.froobworld.farmcontrol.metrics.charts;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class ModesInUseChart extends SimplePie {

    public ModesInUseChart(FarmControl farmControl) {
        super("modes_used", () -> {
            boolean usesProactive = false;
            boolean usesReactive = false;
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.proactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        usesProactive = true;
                        break;
                    }
                }
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        usesReactive = true;
                        break;
                    }
                }
            }
            if (usesProactive && usesReactive) {
                return "Both proactive and reactive";
            } else if (usesProactive) {
                return "Proactive";
            } else if (usesReactive) {
                return "Reactive";
            }
            return "None";
        });
    }

}
