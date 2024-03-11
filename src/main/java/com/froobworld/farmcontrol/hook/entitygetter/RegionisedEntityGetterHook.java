package com.froobworld.farmcontrol.hook.entitygetter;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.hook.scheduler.RegionisedSchedulerHook;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class RegionisedEntityGetterHook implements EntityGetterHook {
    private final FarmControl farmControl;

    public RegionisedEntityGetterHook(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    private <T> CompletableFuture<Collection<T>> getMappedEntities(World world, Function<Entity, T> mapper, Class<?>... entityClasses) {
        MappedEntityCollector<T> entityCollector = new MappedEntityCollector<>();
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (Chunk chunk : world.getLoadedChunks()) {
            CompletableFuture<Void> chunkFuture = new CompletableFuture<>();
            Bukkit.getRegionScheduler().execute(farmControl, world, chunk.getX(), chunk.getZ(), () -> {
                for (Entity entity : chunk.getEntities()) {
                    boolean eligible = entityClasses.length == 0;
                    for (Class<?> entityClass : entityClasses) {
                        if (entityClass.isAssignableFrom(entity.getClass())) {
                            eligible = true;
                            break;
                        }
                    }
                    if (eligible) {
                        entityCollector.addEntity(entity, mapper.apply(entity));
                    }
                }
                chunkFuture.complete(null);
            });
            future = future.thenCompose(v -> chunkFuture);
        }
        return future.thenApply(v -> entityCollector.getEntities());
    }

    @Override
    public CompletableFuture<Collection<Entity>> getEntities(World world, Class<?>... entityClasses) {
        return getMappedEntities(world, Function.identity(), entityClasses);
    }

    @Override
    public CompletableFuture<Collection<Entity>> getEntities(World world) {
        return getMappedEntities(world, Function.identity());
    }

    @Override
    public CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world, Class<?>... classes) {
        return getMappedEntities(world, SnapshotEntity::new, classes).thenApply(ArrayList::new);
    }

    @Override
    public CompletableFuture<List<SnapshotEntity>> getSnapshotEntities(World world) {
        return getMappedEntities(world, SnapshotEntity::new).thenApply(ArrayList::new);
    }

    public static boolean isCompatible() {
        return RegionisedSchedulerHook.isCompatible();
    }

    private static class MappedEntityCollector<T> {
        private final Set<UUID> seenEntities = Sets.newConcurrentHashSet();
        private final Collection<T> mappedEntities = new ConcurrentLinkedQueue<>();

        public void addEntity(Entity entity, T mappedEntity) {
            if (seenEntities.add(entity.getUniqueId())) {
                mappedEntities.add(mappedEntity);
            }
        }

        public Collection<T> getEntities() {
            return mappedEntities;
        }
    }

}
