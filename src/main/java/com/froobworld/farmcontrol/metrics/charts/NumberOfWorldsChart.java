package com.froobworld.farmcontrol.metrics.charts;

import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;

public class NumberOfWorldsChart extends SimplePie {

    public NumberOfWorldsChart() {
        super("number_of_worlds", () -> Bukkit.getWorlds().size() + "");
    }

}
