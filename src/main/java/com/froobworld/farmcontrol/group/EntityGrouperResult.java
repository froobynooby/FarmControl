package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;

import java.util.*;

public class EntityGrouperResult {
    private final GroupDefinition groupDefinition;
    private final List<Group> groups = new ArrayList<>();

    public EntityGrouperResult(GroupDefinition groupDefinition) {
        this.groupDefinition = groupDefinition;
    }

    void addEntity(SnapshotEntity entity) {
        if (!groupDefinition.getTypePredicate().test(entity) || groupDefinition.getExcludeTypePredicate().test(entity)) {
            return;
        }
        ListIterator<Group> iterator = groups.listIterator();

        Group entityGroup = new Group(groupDefinition, entity);
        iterator.add(entityGroup);
        while (iterator.hasNext()) {
            Group nextGroup = iterator.next();
            if (nextGroup.shouldBeMember(entity)) {
                entityGroup.merge(nextGroup);
                iterator.remove();
            }
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

}
