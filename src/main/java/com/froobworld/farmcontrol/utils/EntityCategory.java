package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class EntityCategory {
    private static final Map<String, EntityCategory> entityCategoryMap;
    private final String name;
    private final boolean allowInheritance;
    private final Class<?>[] memberClasses;

    static {
        List<EntityCategory> entityCategories = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            Class<?> entityTypeClass = entityType.getEntityClass();
            if (entityTypeClass != null) {
                entityCategories.add(new EntityCategory(entityType.toString().toLowerCase(), false, entityTypeClass));
            }
        }
        entityCategories.addAll(
                List.of(
                        new EntityCategory("category:animal", true, Animals.class),
                        new EntityCategory("category:monster", true, Monster.class),
                        new EntityCategory("category:golem", true, Golem.class),
                        new EntityCategory("category:ambient", true, Ambient.class),
                        new EntityCategory("category:fish", true, Fish.class),
                        new EntityCategory("category:tameable", true, Tameable.class),
                        new EntityCategory("category:raider", true, Raider.class),
                        new EntityCategory("category:mob", true, Mob.class),
                        new EntityCategory("category:vehicle", true, Boat.class, Minecart.class),
                        new EntityCategory("category:projectile", true, Projectile.class)
                )
        );

        entityCategoryMap = new HashMap<>();
        entityCategories.forEach(entityCategory -> entityCategoryMap.put(entityCategory.name, entityCategory));
    }

    private EntityCategory(String name, boolean allowInheritance, Class<?>... memberClasses) {
        this.name = name;
        this.allowInheritance = allowInheritance;
        this.memberClasses = memberClasses;
    }

    public String getName() {
        return name;
    }

    public boolean isMember(Class<?> entityClass) {
        for (Class<?> memberClass : memberClasses) {
            if (allowInheritance ? memberClass.isAssignableFrom(entityClass) : memberClass.equals(entityClass)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMember(SnapshotEntity entity) {
        return isMember(entity.getEntityType());
    }

    public boolean isMember(EntityType entityType) {
        if (entityType.getEntityClass() == null) {
            return false;
        }
        return isMember(entityType.getEntityClass());
    }

    public Predicate<SnapshotEntity> asSnapshotEntityPredicate() {
        return this::isMember;
    }

    public boolean isCompatibleWith(Action action) {
        for (EntityType entityType : EntityType.values()) {
            if (isMember(entityType)) {
                Class<?> entityTypeClass = entityType.getEntityClass();
                if (entityTypeClass != null && action.getEntityClass().isAssignableFrom(entityTypeClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static EntityCategory ofName(String categoryName) {
        return EntityCategory.entityCategoryMap.get(categoryName.toLowerCase());
    }

}
