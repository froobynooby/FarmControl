package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Tameable;
import org.bukkit.util.Vector;

public class SnapshotEntity {
    private final Mob entity;
    private final Class<? extends Mob> entityClass;
    private final EntityType entityType;
    private final Vector location;
    private final FcData fcData;
    private final boolean leashed;
    private final boolean loveMode;
    private final String customName;
    private final boolean tamed;
    private final int ticksLived;

    public SnapshotEntity(Mob entity) {
        this.entity = entity;
        this.entityClass = entity.getClass();
        this.entityType = entity.getType();
        this.location = entity.getLocation().toVector();
        this.fcData = FcData.get(entity);
        this.leashed = entity.isLeashed();
        this.loveMode = entity instanceof Animals && ((Animals) entity).isLoveMode();
        this.customName = entity.getCustomName();
        this.tamed = entity instanceof Tameable && ((Tameable) entity).isTamed();
        this.ticksLived = entity.getTicksLived();
    }

    public Class<? extends Mob> getEntityClass() {
        return entityClass;
    }

    public EntityType getEntityType() {
        return entityType;
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

    public String getCustomName() {
        return customName;
    }

    public boolean isTamed() {
        return tamed;
    }

    public int getTicksLived() {
        return ticksLived;
    }
}
