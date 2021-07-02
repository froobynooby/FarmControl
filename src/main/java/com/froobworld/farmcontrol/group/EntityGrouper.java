package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;

import java.util.Collection;

public class EntityGrouper {

    public static EntityGrouperResult groupEntities(Collection<SnapshotEntity> entities, GroupDefinition groupDefinition) {
        EntityGrouperResult.Builder resultBuilder = new EntityGrouperResult.Builder(groupDefinition);
        for (SnapshotEntity entity : entities) {
            resultBuilder.addEntity(entity);
        }
        return resultBuilder.build();
    }

}
