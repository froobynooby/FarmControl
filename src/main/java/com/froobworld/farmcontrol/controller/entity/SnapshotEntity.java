package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.material.Colorable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SnapshotEntity {
    private static final AtomicBoolean useWoodType = new AtomicBoolean(false);
    private final Entity entity;
    private final int entityId;
    private final Vector location;
    private final FcData fcData;
    private final boolean leashed;
    private final boolean loveMode;
    private final boolean customName;
    private final boolean tamed;
    private final boolean isPatrolLeader;
    private final int ticksLived;
    private final boolean pickupable;
    private final boolean mounted;
    private final List<Object> classifications = new ArrayList<>();

    public SnapshotEntity(Entity entity) {
        this.entity = entity;
        this.entityId = entity.getEntityId();
        this.location = entity.getLocation().toVector();
        this.fcData = FcData.get(entity);
        this.leashed = entity instanceof Mob && ((Mob) entity).isLeashed();
        this.loveMode = entity instanceof Animals && ((Animals) entity).isLoveMode();
        this.customName = entity.getCustomName() != null;
        this.tamed = entity instanceof Tameable && ((Tameable) entity).isTamed();
        this.isPatrolLeader = entity instanceof Raider && ((Raider) entity).isPatrolLeader();
        this.pickupable = entity instanceof AbstractArrow && ((AbstractArrow) entity).getPickupStatus() == AbstractArrow.PickupStatus.ALLOWED;
        this.ticksLived = entity.getTicksLived();
        this.mounted = !entity.getPassengers().isEmpty();
        classifications.add(entity.getType());
        if (entity instanceof Colorable) {
            DyeColor colour = ((Colorable) entity).getColor();
            if (colour != null) {
                classifications.add(colour);
            }
        }
        if (entity instanceof Villager) {
            classifications.add(((Villager) entity).getProfession());
        }
        if (entity instanceof Item) {
            classifications.add(((Item) entity).getItemStack().getType());
        }
        if (entity instanceof Boat) {
            // try to add boat type, falling back to wood type for version < 1.19
            if (!SnapshotEntity.useWoodType.get()) {
                try {
                    classifications.add(((Boat) entity).getBoatType());
                } catch (Throwable throwable) {
                    useWoodType.set(true);
                }
            }
            if (SnapshotEntity.useWoodType.get()) {
                //noinspection deprecation
                classifications.add(((Boat) entity).getWoodType());
            }
        }
    }

    public Class<? extends Entity> getEntityClass() {
        return entity.getClass();
    }

    public int getEntityId() {
        return entityId;
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

    public Entity getEntity() {
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

    public boolean isPatrolLeader() {
        return isPatrolLeader;
    }

    public boolean isPickupable() {
        return pickupable;
    }

    public boolean isMounted() {
        return mounted;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public List<Object> getClassifications() {
        return classifications;
    }

}
