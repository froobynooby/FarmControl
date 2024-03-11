package com.froobworld.farmcontrol.hook.entitygetter;

import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntityGetterHook {

    CompletableFuture<Collection<Entity>> getEntities(World world, Class<?>... classes);

    CompletableFuture<Collection<Entity>> getEntities(World world);

    CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world, Class<?>... classes);

    CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world);

}
