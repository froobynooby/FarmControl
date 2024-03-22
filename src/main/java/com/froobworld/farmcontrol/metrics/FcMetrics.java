package com.froobworld.farmcontrol.metrics;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.metrics.charts.*;

public class FcMetrics {
    private final FarmControl farmControl;
    private final Metrics metrics;

    public FcMetrics(FarmControl farmControl, int pluginId) {
        this.farmControl = farmControl;
        metrics = new Metrics(farmControl, pluginId);
        addCustomMetrics();
    }

    private void addCustomMetrics() {
        metrics.addCustomChart(new LegacyProactiveActionsChart(farmControl));
        metrics.addCustomChart(new LegacyReactiveActionsChart(farmControl));
        metrics.addCustomChart(new LegacyModesInUseChart(farmControl));
        metrics.addCustomChart(new LegacyReactiveModeIndicatorChart(farmControl));
        metrics.addCustomChart(new NumberOfWorldsChart());
        metrics.addCustomChart(new ProactiveActionsChart(farmControl));
        metrics.addCustomChart(new ReactiveActionsChart(farmControl));
        metrics.addCustomChart(new ModesInUseChart(farmControl));
    }

}
