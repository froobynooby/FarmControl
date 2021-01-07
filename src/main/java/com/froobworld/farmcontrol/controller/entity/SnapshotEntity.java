package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.*;
import org.bukkit.material.Colorable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SnapshotEntity {
    private final Mob entity;
    private final Vector location;
    private final FcData fcData;
    private final boolean leashed;
    private final boolean loveMode;
    private final boolean customName;
    private final boolean tamed;
    private final int ticksLived;
    private final List<Object> classifications = new ArrayList<>();

    public SnapshotEntity(Mob entity) {
        this.entity = entity;
        this.location = entity.getLocation().toVector();
        this.fcData = FcData.get(entity);
        this.leashed = entity.isLeashed();
        this.loveMode = entity instanceof Animals && ((Animals) entity).isLoveMode();
        this.customName = entity.getCustomName() != null;
        this.tamed = entity instanceof Tameable && ((Tameable) entity).isTamed();
        this.ticksLived = entity.getTicksLived();
        classifications.add(entity.getType());
        if (entity instanceof Colorable) {
            classifications.add(((Colorable) entity).getColor());
        }
        if (entity instanceof Villager) {
            classifications.add(((Villager) entity).getProfession());
        }
    }

    public Class<? extends Mob> getEntityClass() {
        return entity.getClass();
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

    public boolean hasMetadata(String key) {
        return entity.hasMetadata(key);
    }

    public boolean isLeashed() {
        return leashed;
    }

    public boolean isLoveMode() {
        return loveMode;
    }

    public boolean hasCustomName() {
        return customName;
    }

    public boolean isTamed() {
        return tamed;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public List<Object> getClassifications() {
        return classifications;
    }
}
