package com.froobworld.farmcontrol.metrics.charts;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class LegacyModesInUseChart extends AdvancedPie {

    public LegacyModesInUseChart(FarmControl farmControl) {
        super("modes_in_use", () -> {
            Map<String, Integer> modeCountMap = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.proactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        modeCountMap.put("proactive", 1);
                        break;
                    }
                }
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        modeCountMap.put("reactive", 1);
                        break;
                    }
                }
            }
            return modeCountMap;
        });
    }

}
