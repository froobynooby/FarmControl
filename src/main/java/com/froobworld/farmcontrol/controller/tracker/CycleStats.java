package com.froobworld.farmcontrol.controller.tracker;

import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CycleStats {
    private final long startTime;
    private final long endTime;
    private final int entitiesAffected;
    private final int entitiesRemoved;
    private final Map<Action, Map<EntityType, Integer>> actionCounts;

    private CycleStats(long startTime, long endTime, int entitiesAffected, int entitiesRemoved, Map<Action, Map<EntityType, Integer>> actionCounts) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.entitiesAffected = entitiesAffected;
        this.entitiesRemoved = entitiesRemoved;
        this.actionCounts = actionCounts;
    }

    public long getStartTimeMillis() {
        return startTime;
    }

    public long getDurationMillis() {
        return startTime - endTime;
    }

    public int getAffectedEntityCount() {
        return entitiesAffected;
    }

    public int getEntitiesRemoved() {
        return entitiesRemoved;
    }

    public BaseComponent[] getBreakdown() {
        List<BaseComponent> components = new ArrayList<>();
        boolean firstAction = true;

        for (Action action : actionCounts.keySet()) {
            int totalCount = actionCounts.get(action).values().stream()
                    .mapToInt(value -> value)
                    .sum();
            String entityBreakdown = actionCounts.get(action).entrySet().stream()
                    .sorted(Map.Entry.<EntityType, Integer>comparingByValue().reversed())
                    .map(entry -> ChatColor.RED + "" + entry.getValue() + " " + ChatColor.GRAY + entry.getKey().name().toLowerCase())
                    .collect(Collectors.joining(", "));
            BaseComponent[] baseComponents = new ComponentBuilder()
                    .append(firstAction ? "" : "\n\n")
                    .append("- " + action.getName() + ": ").color(ChatColor.GOLD)
                    .append(totalCount + "\n").color(ChatColor.RED)
                    .append(TextComponent.fromLegacyText(entityBreakdown))
                    .create();
            components.addAll(Arrays.asList(baseComponents));
            firstAction = false;
        }
        return components.toArray(new BaseComponent[0]);
    }

    public static class Builder {
        private long startTime;
        private final Set<SnapshotEntity> affectedEntities = new HashSet<>();
        private final Set<SnapshotEntity> removedEntities = new HashSet<>();
        private final Map<Action, Map<EntityType, Integer>> actionCounts = new HashMap<>();

        private Builder() {}

        public static Builder start(long startTime) {
            Builder builder = new Builder();
            builder.startTime = startTime;
            return builder;
        }

        public synchronized void action(Action action, SnapshotEntity entity) {
            affectedEntities.add(entity);
            if (action.removes()) {
                removedEntities.add(entity);
            }
            actionCounts.computeIfAbsent(action, a -> new HashMap<>())
                    .compute(entity.getEntityType(), (e, count) -> count != null ? count + 1 : 1);
        }

        public CycleStats end(long endTime) {
            return new CycleStats(startTime, endTime, affectedEntities.size(), removedEntities.size(), actionCounts);
        }

    }

}
