package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.config.FcConfig;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.World;

import java.util.List;
import java.util.function.Predicate;

public class ExclusionManager {
    private final FarmControl farmControl;

    public ExclusionManager(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    public Predicate<SnapshotEntity> getExclusionPredicate(World world) {
        FcConfig.WorldSettings.ExclusionSettings exclusionSettings = farmControl.getFcConfig().worldSettings.of(world).exclusionSettings;
        boolean excludeLeashed = exclusionSettings.leashed.get();
        boolean excludeLoveMode = exclusionSettings.loveMode.get();
        List<String> excludeMeta = exclusionSettings.metadata.get();
        boolean excludeNamed = exclusionSettings.named.get();
        boolean excludeTamed = exclusionSettings.tamed.get();
        boolean excludePatrolLeaders = exclusionSettings.patrolLeader.get();
        List<String> excludeType = exclusionSettings.type.get();
        long excludeTicksLived = exclusionSettings.youngerThan.get();
        boolean excludePickupable = exclusionSettings.pickupable.get();
        boolean excludeMounted = exclusionSettings.mounted.get();
        return snapshotEntity -> {
            if (excludeLeashed && snapshotEntity.isLeashed()) {
                return true;
            }
            if (excludeLoveMode && snapshotEntity.isLoveMode()) {
                return true;
            }
            if (excludeNamed && snapshotEntity.hasCustomName()) {
                return true;
            }
            if (excludeTamed && snapshotEntity.isTamed()) {
                return true;
            }
            if (excludePatrolLeaders && snapshotEntity.isPatrolLeader()) {
                return true;
            }
            if (excludePickupable && snapshotEntity.isPickupable()) {
                return true;
            }
            if (excludeMounted && snapshotEntity.isMounted()) {
                return true;
            }
            if (snapshotEntity.getTicksLived() < excludeTicksLived) {
                return true;
            }
            for (String meta : excludeMeta) {
                if (snapshotEntity.hasMetadata(meta)) {
                    return true;
                }
            }
            for (String type : excludeType) {
                if (snapshotEntity.getEntityType().toString().equalsIgnoreCase(type)) {
                    return true;
                }
            }
            return false;
        };
    }

}
