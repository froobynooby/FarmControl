package com.froobworld.farmcontrol.group;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class EntityGrouperResult {
    private final List<Group> groups;

    public EntityGrouperResult(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static class Builder {
        private final GroupDefinition groupDefinition;
        private final List<ProtoGroup> protoGroups = new ArrayList<>();

        Builder(GroupDefinition groupDefinition) {
            this.groupDefinition = groupDefinition;
        }

        public void addEntity(SnapshotEntity entity) {
            if (!groupDefinition.getTypePredicate().test(entity) || groupDefinition.getExcludeTypePredicate().test(entity)) {
                return;
            }
            ListIterator<ProtoGroup> iterator = protoGroups.listIterator();

            ProtoGroup entityProtoGroup = null;
            while (iterator.hasNext()) {
                ProtoGroup nextProtoGroup = iterator.next();
                if (nextProtoGroup.shouldBeMember(entity)) {
                    if (entityProtoGroup == null) {
                        entityProtoGroup = nextProtoGroup;
                        entityProtoGroup.add(entity);
                    } else {
                        entityProtoGroup.merge(nextProtoGroup);
                        iterator.remove();
                    }
                }
            }
            if (entityProtoGroup == null) {
                protoGroups.add(new ProtoGroup(groupDefinition, entity));
            }
        }

        public EntityGrouperResult build() {
            return new EntityGrouperResult(
                    protoGroups.stream()
                            .map(ProtoGroup::toGroup)
                            .collect(Collectors.toList())
            );
        }

    }

}
