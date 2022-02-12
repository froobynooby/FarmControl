package com.froobworld.farmcontrol.metrics.charts;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import com.froobworld.farmcontrol.controller.action.Action;
import org.bstats.charts.SimpleBarChart;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class ReactiveActionsChart extends SimpleBarChart {

    public ReactiveActionsChart(FarmControl farmControl) {
        super("reactive_actions", () -> {
            Map<String, Integer> actionMap = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        for (Action action : actionProfile.getActions()) {
                            actionMap.put(action.getName(), 1);
                        }
                    }
                }
            }
            return actionMap;
        });
    }

}
