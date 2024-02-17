package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.*;

import java.util.function.Predicate;

public final class EntityTypeUtils {

    private EntityTypeUtils(){}

    public static Predicate<SnapshotEntity> fromString(String string) {
        if (string.toLowerCase().startsWith("category:")) {
            String category = string.split(":")[1];
            switch (category.toLowerCase()) {
                case "animal" -> {return entity -> Animals.class.isAssignableFrom(entity.getEntityClass());}
                case "monster" -> {return entity -> Monster.class.isAssignableFrom(entity.getEntityClass());}
                case "golem" -> {return entity -> Golem.class.isAssignableFrom(entity.getEntityClass());}
                case "ambient" -> {return entity -> Ambient.class.isAssignableFrom(entity.getEntityClass());}
                case "fish" -> {return entity -> Fish.class.isAssignableFrom(entity.getEntityClass());}
                case "tameable" -> {return entity -> Tameable.class.isAssignableFrom(entity.getEntityClass());}
                case "raider" -> {return entity -> Raider.class.isAssignableFrom(entity.getEntityClass());}
                case "mob" -> {return entity -> Mob.class.isAssignableFrom(entity.getEntityClass());}
                case "vehicle" -> {return entity -> Vehicle.class.isAssignableFrom(entity.getEntityClass());}
                case "projectile" -> {return entity -> Projectile.class.isAssignableFrom(entity.getEntityClass());}
            }
        }
        return entity -> entity.getEntityType().toString().equalsIgnoreCase(string);
    }

}
