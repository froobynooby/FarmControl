package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.EntityTypeUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Predicate;

public class GroupDefinition {
    private final Predicate<SnapshotEntity> typePredicate;
    private final Predicate<SnapshotEntity> excludeTypePredicate;
    private final int size;
    private final double distance;
    private final double distanceSquared;
    private final boolean sameChunk;
    private final boolean ignoreVerticalDistance;
    private final boolean pure;

    public GroupDefinition(Predicate<SnapshotEntity> typePredicate, Predicate<SnapshotEntity> excludeTypePredicate, int size, double distance, boolean sameChunk, boolean ignoreVerticalDistance, boolean pure) {
        this.typePredicate = typePredicate;
        this.excludeTypePredicate = excludeTypePredicate;
        this.size = size;
        this.distance = distance;
        this.distanceSquared = distance * distance;
        this.sameChunk = sameChunk;
        this.ignoreVerticalDistance = ignoreVerticalDistance;
        this.pure = pure;
    }

    public Predicate<SnapshotEntity> getTypePredicate() {
        return typePredicate;
    }

    public Predicate<SnapshotEntity> getExcludeTypePredicate() {
        return excludeTypePredicate;
    }

    public int getSize() {
        return size;
    }

    public boolean isSameChunk() {
        return sameChunk;
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
                .orElse(snapshotEntity -> false); // require explicit specification of types by defaulting to false
        Predicate<SnapshotEntity> excludeTypePredicate = section.getStringList("exclude-types").stream()
                .map(EntityTypeUtils::fromString)
                .reduce(Predicate::or)
                .orElse(snapshotEntity -> false);
        int size = section.getInt("count");
        boolean sameChunk = section.isString("distance") && "same-chunk".equalsIgnoreCase(section.getString("distance"));
        double distance = sameChunk ? 0 : section.getDouble("distance");
        boolean ignoreVerticalDistance = section.getBoolean("ignore-vertical-distance");
        boolean pure = section.getBoolean("pure");

        return new GroupDefinition(typePredicate, excludeTypePredicate, size, distance, sameChunk, ignoreVerticalDistance, pure);
    }

}
