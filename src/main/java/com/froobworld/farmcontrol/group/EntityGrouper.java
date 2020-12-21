package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;

import java.util.Collection;

public class EntityGrouper {

    public static EntityGrouperResult groupEntities(Collection<SnapshotEntity> entities, GroupDefinition groupDefinition) {
        EntityGrouperResult result = new EntityGrouperResult(groupDefinition);
        for (SnapshotEntity entity : entities) {
            result.addEntity(entity);
        }
        return result;
    }

}
