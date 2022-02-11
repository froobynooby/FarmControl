package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;

import java.util.function.Predicate;

public final class EntityNameUtils {

    public static Predicate<SnapshotEntity> fromString(String string) {
        if (string.startsWith("*")) {
            String name = string.split("\\*")[1];
            return entity -> entity.getEntity().getCustomName() != null && entity.getEntity().getCustomName().toLowerCase().endsWith(name.toLowerCase());
        } else if (string.endsWith("*")) {
            String name = string.split("\\*")[0];
            return entity -> entity.getEntity().getCustomName() != null && entity.getEntity().getCustomName().toLowerCase().startsWith(name.toLowerCase());
        }
        return entity -> entity.getEntity().getCustomName() != null && entity.getEntity().getCustomName().equalsIgnoreCase(string);
    }

}
