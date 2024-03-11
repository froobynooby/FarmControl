package com.froobworld.farmcontrol.hook.entitygetter;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BukkitEntityGetterHook implements EntityGetterHook {
    @Override
    public CompletableFuture<Collection<Entity>> getEntities(World world, Class<?>... classes) {
        return CompletableFuture.completedFuture(world.getEntitiesByClasses(classes));
    }

    @Override
    public CompletableFuture<Collection<Entity>> getEntities(World world) {
        return CompletableFuture.completedFuture(world.getEntities());
    }

    @Override
    public CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world, Class<?>... classes) {
        List<SnapshotEntity> snapshotEntities = new ArrayList<>();
        for (Entity entity : world.getEntitiesByClasses(classes)) {
            snapshotEntities.add(new SnapshotEntity(entity));
        }
        return CompletableFuture.completedFuture(snapshotEntities);
    }

    @Override
    public CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world) {
        List<SnapshotEntity> snapshotEntities = new ArrayList<>();
        for (Entity entity : world.getEntities()) {
            snapshotEntities.add(new SnapshotEntity(entity));
        }
        return CompletableFuture.completedFuture(snapshotEntities);
    }
}
