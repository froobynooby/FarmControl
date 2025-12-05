package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MixedEntitySet implements Iterable<SnapshotEntity> {
    private final Comparator<SnapshotEntity> comparator;
    private final Set<SnapshotEntity> unclassifiedEntities;
    private final Map<Object, MixedEntitySet> classifiedEntityMap = new HashMap<>();

    public MixedEntitySet(Comparator<SnapshotEntity> comparator) {
        // if equal, resolve by comparing the entity ID
        this.comparator = comparator.thenComparingInt(SnapshotEntity::getEntityId);
        unclassifiedEntities = new TreeSet<>(this.comparator);
    }

    public boolean add(SnapshotEntity entity) {
        return add(entity, entity.getClassifications().iterator());
    }

    private boolean add(SnapshotEntity entity, Iterator<Object> classificationIterator) {
        if (!classificationIterator.hasNext()) {
            return unclassifiedEntities.add(entity);
        } else {
            return classifiedEntityMap.computeIfAbsent(classificationIterator.next(), c -> new MixedEntitySet(comparator)).add(entity, classificationIterator);
        }
    }

    public boolean remove(SnapshotEntity entity) {
        return remove(entity, entity.getClassifications().iterator());
    }

    private boolean remove(SnapshotEntity entity, Iterator<Object> classificationIterator) {
        if (!classificationIterator.hasNext()) {
            return unclassifiedEntities.remove(entity);
        } else {
            MixedEntitySet subSet = classifiedEntityMap.get(classificationIterator.next());
            return subSet != null && subSet.remove(entity, classificationIterator);
        }
    }

    public int size() {
        int size = unclassifiedEntities.size();
        for (MixedEntitySet subSet : classifiedEntityMap.values()) {
            size += subSet.size();
        }
        return size;
    }

    public MixedEntityIterator iterator() {
        return new MixedEntityIterator(this);
    }

    public static class MixedEntityIterator implements Iterator<SnapshotEntity> {
        private Object lastUsedClassification;
        private int effectiveRemaining;
        private final Iterator<SnapshotEntity> unclassifiedIterator;
        private final Map<Object, MixedEntityIterator> classifiedIteratorMap;

        public MixedEntityIterator(MixedEntitySet set) {
            effectiveRemaining = set.unclassifiedEntities.size();
            unclassifiedIterator = set.unclassifiedEntities.iterator();
            classifiedIteratorMap = Maps.toMap(set.classifiedEntityMap.keySet(), key -> new MixedEntityIterator(set.classifiedEntityMap.get(key)));
        }

        @Override
        public boolean hasNext() {
            if (unclassifiedIterator.hasNext()) {
                return true;
            }
            for (MixedEntityIterator subIterator : classifiedIteratorMap.values()) {
                if (subIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public SnapshotEntity next() {
            Object nextClassification = null;
            int highestRemaining = unclassifiedIterator.hasNext() ? effectiveRemaining : -1;
            for (Object object : classifiedIteratorMap.keySet()) {
                MixedEntityIterator nextIterator = classifiedIteratorMap.get(object);
                if (nextIterator.hasNext() && nextIterator.getEffectiveRemaining() > highestRemaining) {
                    nextClassification = object;
                    highestRemaining = nextIterator.getEffectiveRemaining();
                }
            }
            lastUsedClassification = nextClassification;
            if (nextClassification == null) {
                effectiveRemaining--;
                return unclassifiedIterator.next();
            } else {
                return classifiedIteratorMap.get(nextClassification).next();
            }
        }

        private int getEffectiveRemaining() {
            int effectiveRemaining = this.effectiveRemaining;
            for (MixedEntityIterator iterator : classifiedIteratorMap.values()) {
                effectiveRemaining += iterator.getEffectiveRemaining();
            }
            return effectiveRemaining;
        }

        @Override
        public void remove() {
            if (lastUsedClassification == null) {
                unclassifiedIterator.remove();
            } else {
                classifiedIteratorMap.get(lastUsedClassification).remove();
            }
        }

        public void skipLast() {
            if (lastUsedClassification == null) {
                effectiveRemaining++;
            } else {
                classifiedIteratorMap.get(lastUsedClassification).skipLast();
            }
        }

    }

}
