package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.MixedEntitySet;

import java.util.Collection;
import java.util.Comparator;

public class Group {
    private final GroupDefinition definition;
    private final MixedEntitySet members = new MixedEntitySet(Comparator.comparingLong(SnapshotEntity::getTicksLived)); // youngest removed first

    public Group(GroupDefinition definition, Collection<SnapshotEntity> members) {
        this.definition = definition;
        members.forEach(this.members::add);
    }

    public MixedEntitySet getMembers() {
        return members;
    }

    public boolean meetsCondition() {
        return members.size() >= definition.getSize();
    }

}
