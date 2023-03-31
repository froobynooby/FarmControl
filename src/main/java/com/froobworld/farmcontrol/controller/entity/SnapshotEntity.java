package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.*;
import org.bukkit.material.Colorable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SnapshotEntity {
    private final Mob entity;
    private final boolean excluded;
    private final Vector location;
    private final FcData fcData;
    private final List<Object> classifications = new ArrayList<>();

    public SnapshotEntity(Mob entity, boolean excluded) {
        this.entity = entity;
        this.excluded = excluded;
        this.location = entity.getLocation().toVector();
        this.fcData = FcData.get(entity);
        classifications.add(entity.getType());
        if (entity instanceof Colorable) {
            classifications.add(((Colorable) entity).getColor());
        }
        if (entity instanceof Villager) {
            classifications.add(((Villager) entity).getProfession());
        }
    }

    public EntityType getEntityType() {
        return entity.getType();
    }

    public Vector getLocation() {
        return location;
    }

    public FcData getFcData() {
        return fcData;
    }

    public Mob getEntity() {
        return entity;
    }

    public List<Object> getClassifications() {
        return classifications;
    }

    public boolean isExcluded() {
        return excluded;
    }
}
