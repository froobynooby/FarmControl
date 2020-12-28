package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.MixedEntitySet;
import org.bukkit.entity.EntityType;

public class Group {
    private final EntityType initialType;
    private final int initialChunkX, initialChunkZ;
    private final GroupDefinition definition;
    private final MixedEntitySet members = new MixedEntitySet();

    public Group(GroupDefinition definition, SnapshotEntity initialMember) {
        this.definition = definition;
        members.add(initialMember);
        initialType = initialMember.getEntityType();
        initialChunkX = initialMember.getLocation().getBlockX() >> 4;
        initialChunkZ = initialMember.getLocation().getBlockZ() >> 4;
    }

    public boolean shouldBeMember(SnapshotEntity entity) {
        if (definition.isPure() && entity.getEntityType() != initialType) {
            return false;
        }
        if (definition.isSameChunk()) {
            return entity.getLocation().getBlockX() >> 4 == initialChunkX && entity.getLocation().getBlockZ() >> 4 == initialChunkZ;
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
            } else if (distance > (remainingMembers + 1) * definition.getDistance()) {
                return false;
            }
            remainingMembers--;
        }
        return false;
    }

    public void merge(Group otherGroup) {
        members.addAll(otherGroup.members);
    }

    public MixedEntitySet getMembers() {
        return members;
    }

    public boolean meetsCondition() {
        return members.size() >= definition.getSize();
    }

}
