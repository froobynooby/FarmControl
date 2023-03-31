package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.config.FcConfig;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ExclusionManager {
    private final FarmControl farmControl;
    private final Set<Predicate<Mob>> customPredicates = new HashSet<>();

    public ExclusionManager(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    public Predicate<Mob> getExclusionPredicate(World world) {
        FcConfig.WorldSettings.ExclusionSettings exclusionSettings = farmControl.getFcConfig().worldSettings.of(world).exclusionSettings;
        boolean excludeLeashed = exclusionSettings.leashed.get();
        boolean excludeLoveMode = exclusionSettings.loveMode.get();
        List<String> excludeMeta = exclusionSettings.metadata.get();
        boolean excludeNamed = exclusionSettings.named.get();
        boolean excludeTamed = exclusionSettings.tamed.get();
        boolean excludePatrolLeaders = exclusionSettings.patrolLeader.get();
        List<String> excludeType = exclusionSettings.type.get();
        long excludeTicksLived = exclusionSettings.youngerThan.get();
        return entity -> {
            if (excludeLeashed && entity.isLeashed()) {
                return true;
            }
            if (excludeLoveMode && entity instanceof Animals && ((Animals) entity).isLoveMode()) {
                return true;
            }
            if (excludeNamed && entity.getCustomName() != null) {
                return true;
            }
            if (excludeTamed && entity instanceof Tameable && ((Tameable) entity).isTamed()) {
                return true;
            }
            if (excludePatrolLeaders && entity instanceof Raider && ((Raider) entity).isPatrolLeader()) {
                return true;
            }
            if (entity.getTicksLived() < excludeTicksLived) {
                return true;
            }
            for (String meta : excludeMeta) {
                if (entity.hasMetadata(meta)) {
                    return true;
                }
            }
            for (String type : excludeType) {
                if (entity.getType().toString().equalsIgnoreCase(type)) {
                    return true;
                }
            }
            for (Predicate<Mob> customPredicate : customPredicates) {
                if (customPredicate.test(entity)) {
                    return true;
                }
            }
            return false;
        };
    }

    public void addCustomPredicate(Predicate<Mob> predicate) {
        this.customPredicates.add(predicate);
    }

    public void removeCustomPredicate(Predicate<Mob> predicate) {
        this.customPredicates.remove(predicate);
    }

}
