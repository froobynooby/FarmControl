package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.EntityTypeUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Predicate;

public class GroupDefinition {
    private final Predicate<SnapshotEntity> typePredicate;
    private final int size;
    private final double distance;
    private final double distanceSquared;
    private final boolean ignoreVerticalDistance;
    private final boolean pure;

    public GroupDefinition(Predicate<SnapshotEntity> typePredicate, int size, double distance, boolean ignoreVerticalDistance, boolean pure) {
        this.typePredicate = typePredicate;
        this.size = size;
        this.distance = distance;
        this.distanceSquared = distance * distance;
        this.ignoreVerticalDistance = ignoreVerticalDistance;
        this.pure = pure;
    }

    public Predicate<SnapshotEntity> getTypePredicate() {
        return typePredicate;
    }

    public int getSize() {
        return size;
    }

    public double getDistance() {
        return distance;
    }

    public double getDistanceSquared() {
        return distanceSquared;
    }

    public boolean ignoreVerticalDistance() {
        return ignoreVerticalDistance;
    }

    public boolean isPure() {
        return pure;
    }

    public static GroupDefinition fromConfigurationSection(ConfigurationSection section) {
        Predicate<SnapshotEntity> typePredicate = section.getStringList("types").stream()
                .map(EntityTypeUtils::fromString)
                .reduce(Predicate::or)
                .orElse(snapshotEntity -> true);
        int size = section.getInt("count");
        double distance = section.getDouble("distance");
        boolean ignoreVerticalDistance = section.getBoolean("ignore-vertical-distance");
        boolean pure = section.getBoolean("pure");

        return new GroupDefinition(typePredicate, size, distance, ignoreVerticalDistance, pure);
    }

}
