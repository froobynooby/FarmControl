package com.froobworld.farmcontrol.metrics;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.ActionProfile;
import com.froobworld.farmcontrol.controller.action.Action;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class FcMetrics {
    private final FarmControl farmControl;
    private final Metrics metrics;

    public FcMetrics(FarmControl farmControl, int pluginId) {
        this.farmControl = farmControl;
        metrics = new Metrics(farmControl, pluginId);
        addCustomMetrics();
    }

    private void addCustomMetrics() {
        metrics.addCustomChart(new Metrics.AdvancedPie("actions_in_use", () -> {
            Map<String, Integer> actionCountMap = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.proactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        for (Action action : actionProfile.getActions()) {
                            actionCountMap.put(action.getName(), 1);
                        }
                    }
                }
            }
            return actionCountMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("reactive_actions_in_use", () -> {
            Map<String, Integer> actionCountMap = new HashMap<>();
            for (World world : Bukkit.getWorlds()) {
                for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                    ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName);
                    if (actionProfile != null) {
                        for (Action action : actionProfile.getActions()) {
                            actionCountMap.put(action.getName(), 1);
                        }
                    }
                }
            }
            return actionCountMap;
        }));
        metrics.addCustomChart(new Metrics.AdvancedPie("modes_in_use", () -> {
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
        }));
        metrics.addCustomChart(new Metrics.SimplePie("reactive_mode_indicator", () -> {
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
                for (World world : Bukkit.getWorlds()) {
                    if (farmControl.getFcConfig().paperSettings.worldSettings.of(world).alternativeReactiveModeSettings.useAlternativeSettings.get()) {
                        return "mspt";
                    }
                }
                return "tps";
            }
            return null;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("number_of_worlds", () -> Bukkit.getWorlds().size() + ""));
    }

}
