package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ProtoGroup {
    private final EntityType initialType;
    private final int initialChunkX, initialChunkZ;
    private final GroupDefinition definition;
    private final List<SnapshotEntity> members;

    public ProtoGroup(GroupDefinition definition, SnapshotEntity initialMember) {
        this.definition = definition;
        this.members = new ArrayList<>();
        this.members.add(initialMember);
        initialType = initialMember.getEntityType();
        initialChunkX = initialMember.getLocation().getBlockX() >> 4;
        initialChunkZ = initialMember.getLocation().getBlockZ() >> 4;
    }

    public List<SnapshotEntity> getMembers() {
        return members;
    }

    public Group toGroup() {
        return new Group(definition, members);
    }

    void merge(ProtoGroup otherProtoGroup) {
        members.addAll(otherProtoGroup.members);
    }

    void add(SnapshotEntity entity) {
        members.add(entity);
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

}
