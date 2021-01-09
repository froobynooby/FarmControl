package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.*;

import java.util.function.Predicate;

public final class EntityTypeUtils {

    private EntityTypeUtils(){}

    public static Predicate<SnapshotEntity> fromString(String string) {
        if (string.toLowerCase().startsWith("category:")) {
            String category = string.split(":")[1];
            if (category.equalsIgnoreCase("animal")) {
                return entity -> Animals.class.isAssignableFrom(entity.getEntityClass());
            } else if (category.equalsIgnoreCase("monster")) {
                return entity -> Monster.class.isAssignableFrom(entity.getEntityClass());
            } else if (category.equalsIgnoreCase("ambient")) {
                return entity -> Ambient.class.isAssignableFrom(entity.getEntityClass());
            } else if (category.equalsIgnoreCase("fish")) {
                return entity -> Fish.class.isAssignableFrom(entity.getEntityClass());
            } else if (category.equalsIgnoreCase("tameable")) {
                return entity -> Tameable.class.isAssignableFrom(entity.getEntityClass());
            }
        }
        return entity -> entity.getEntityType().toString().equalsIgnoreCase(string);
    }

}
