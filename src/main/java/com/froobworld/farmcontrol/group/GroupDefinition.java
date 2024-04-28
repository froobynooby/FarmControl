package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.EntityCategory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class GroupDefinition {
    private final Set<EntityCategory> memberCategories;
    private final Set<EntityCategory> excludedCategories;
    private final Predicate<SnapshotEntity> typePredicate;
    private final Predicate<SnapshotEntity> excludeTypePredicate;
    private final int size;
    private final double distance;
    private final double distanceSquared;
    private final boolean sameChunk;
    private final boolean ignoreVerticalDistance;
    private final boolean pure;

    public GroupDefinition(Set<EntityCategory> memberCategories, Set<EntityCategory> excludedCategories, int size, double distance, boolean sameChunk, boolean ignoreVerticalDistance, boolean pure) {
        this.memberCategories = memberCategories;
        this.excludedCategories = excludedCategories;
        this.size = size;
        this.distance = distance;
        this.distanceSquared = distance * distance;
        this.sameChunk = sameChunk;
        this.ignoreVerticalDistance = ignoreVerticalDistance;
        this.pure = pure;
        this.typePredicate = memberCategories.stream()
                .map(EntityCategory::asSnapshotEntityPredicate)
                .reduce(Predicate::or)
                .orElse(snapshotEntity -> false); // require explicit specification of types by defaulting to false
        this.excludeTypePredicate = excludedCategories.stream()
                .map(EntityCategory::asSnapshotEntityPredicate)
                .reduce(Predicate::or)
                .orElse(snapshotEntity -> false);
    }

    public Set<EntityCategory> getMemberCategories() {
        return memberCategories;
    }

    public Set<EntityCategory> getExcludedCategories() {
        return excludedCategories;
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

    public static GroupDefinition fromConfigurationSection(FarmControl farmControl, String profileName, ConfigurationSection section) {
        Set<EntityCategory> memberCategories = new HashSet<>();
        for (String entityType : section.getStringList("types")) {
            EntityCategory entityCategory = EntityCategory.ofName(entityType);
            if (entityCategory == null) {
                farmControl.getLogger().warning("Unknown entity type '" + entityType + "' for profile '" + profileName + "'.");
            } else {
                memberCategories.add(entityCategory);
            }
        }
        Set<EntityCategory> excludedCategories = new HashSet<>();
        for (String entityType : section.getStringList("exclude-types")) {
            EntityCategory entityCategory = EntityCategory.ofName(entityType);
            if (entityCategory == null) {
                farmControl.getLogger().warning("Unknown excluded entity type '" + entityType + "' for profile '" + profileName + "'.");
            } else {
                excludedCategories.add(entityCategory);
            }
        }
        int size = section.getInt("count");
        boolean sameChunk = section.isString("distance") && "same-chunk".equalsIgnoreCase(section.getString("distance"));
        double distance = sameChunk ? 0 : section.getDouble("distance");
        boolean ignoreVerticalDistance = section.getBoolean("ignore-vertical-distance");
        boolean pure = section.getBoolean("pure");

        return new GroupDefinition(memberCategories, excludedCategories, size, distance, sameChunk, ignoreVerticalDistance, pure);
    }

}
