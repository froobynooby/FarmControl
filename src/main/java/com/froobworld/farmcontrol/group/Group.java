package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.MixedEntitySet;

import java.util.Collection;

public class Group {
    private final GroupDefinition definition;
    private final MixedEntitySet members = new MixedEntitySet();

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
