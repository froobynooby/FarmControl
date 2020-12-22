package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private final EntityType initialType;
    private final GroupDefinition definition;
    private final Set<SnapshotEntity> members = new HashSet<>();

    public Group(GroupDefinition definition, SnapshotEntity initialMember) {
        this.definition = definition;
        members.add(initialMember);
        initialType = initialMember.getEntityType();
    }

    public boolean shouldBeMember(SnapshotEntity entity) {
        if (definition.isPure() && entity.getEntityType() != initialType) {
            return false;
        }
        int remainingMembers = members.size();
        for (SnapshotEntity member : members) {
            int distance = Math.max(
                    Math.abs(member.getLocation().getBlockX() - entity.getLocation().getBlockX()),
                    Math.max(
                            definition.ignoreVerticalDistance() ? 0 : (Math.abs(member.getLocation().getBlockY() - entity.getLocation().getBlockY())),
                            Math.abs(member.getLocation().getBlockZ() - entity.getLocation().getBlockZ())
                    )
            );
            if (distance <= definition.getDistance()) {
                return true;
            } else if (distance > remainingMembers * definition.getDistance()) {
                return false;
            }
            remainingMembers--;
        }
        return false;
    }

    public void merge(Group otherGroup) {
        members.addAll(otherGroup.members);
    }

    public Set<SnapshotEntity> getMembers() {
        return members;
    }

    public boolean meetsCondition() {
        return members.size() >= definition.getSize();
    }

}
