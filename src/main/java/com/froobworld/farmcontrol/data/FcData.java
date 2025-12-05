package com.froobworld.farmcontrol.data;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.google.common.collect.MapMaker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FcData {
    private static final Map<Entity, FcData> dataCache = new MapMaker().weakKeys().makeMap();
    private static final NamespacedKey KEY = new NamespacedKey(FarmControl.getPlugin(FarmControl.class), "data");
    private static final PersistentDataType<String, FcData> TYPE = new PersistentDataType<String, FcData>() {
        private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<FcData> getComplexType() {
            return FcData.class;
        }

        @NotNull
        @Override
        public String toPrimitive(@NotNull FcData fcData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.toJson(fcData);
        }

        @NotNull
        @Override
        public FcData fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            FcData fcData = gson.fromJson(s, FcData.class);
            fcData.postLoad();
            return fcData;
        }
    };

    @Expose
    private final ConcurrentHashMap<String, Set<String>> persistentActionTriggerMap = new ConcurrentHashMap<>();
    @Expose
    private final ConcurrentHashMap<String, Set<String>> persistentTriggerActionMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Set<String>> actionTriggerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> triggerActionMap = new ConcurrentHashMap<>();
    private boolean dirty = false;

    private FcData() {}

    public Set<String> getActions(Trigger trigger) {
        return triggerActionMap.get(trigger.getName());
    }

    public Set<String> getTriggers(Action action) {
        return actionTriggerMap.get(action.getName());
    }

    public Set<String> getActions() {
        return actionTriggerMap.keySet();
    }

    public boolean add(Trigger trigger, Action action) {
        boolean previouslyActioned = actionTriggerMap.containsKey(action.getName());
        actionTriggerMap.computeIfAbsent(action.getName(), a -> new HashSet<>()).add(trigger.getName());
        triggerActionMap.computeIfAbsent(trigger.getName(), t -> new HashSet<>()).add(action.getName());
        if (action.isPersistent()) {
            if (persistentActionTriggerMap.computeIfAbsent(action.getName(), a -> new HashSet<>()).add(trigger.getName())) {
                dirty = true;
            }
            persistentTriggerActionMap.computeIfAbsent(trigger.getName(), t -> new HashSet<>()).add(action.getName());
        }
        return !previouslyActioned;
    }

    public boolean remove(Trigger trigger, Action action) {
        if (actionTriggerMap.containsKey(action.getName())) {
            Set<String> triggers = actionTriggerMap.get(action.getName());
            triggers.remove(trigger.getName());
            if (triggers.isEmpty()) {
                actionTriggerMap.remove(action.getName());
            }
        }
        if (persistentActionTriggerMap.containsKey(action.getName())) {
            Set<String> triggers = persistentActionTriggerMap.get(action.getName());
            if (triggers.remove(trigger.getName())) {
                dirty = true;
            }
            if (triggers.isEmpty()) {
                persistentActionTriggerMap.remove(action.getName());
            }
        }
        if (triggerActionMap.containsKey(trigger.getName())) {
            Set<String> actions = triggerActionMap.get(trigger.getName());
            actions.remove(action.getName());
            if (actions.isEmpty()) {
                triggerActionMap.remove(trigger.getName());
            }
        }
        if (persistentTriggerActionMap.containsKey(trigger.getName())) {
            Set<String> actions = persistentTriggerActionMap.get(trigger.getName());
            actions.remove(action.getName());
            if (actions.isEmpty()) {
                persistentTriggerActionMap.remove(trigger.getName());
            }
        }
        return !actionTriggerMap.containsKey(action.getName());
    }

    public boolean removeAction(Action action) {
        if (!actionTriggerMap.containsKey(action.getName())) {
            return false;
        }
        actionTriggerMap.remove(action.getName());
        triggerActionMap.values().forEach(actions -> actions.remove(action.getName()));
        if (persistentActionTriggerMap.containsKey(action.getName())) {
            persistentActionTriggerMap.remove(action.getName());
            persistentTriggerActionMap.values().forEach(actions -> actions.remove(action.getName()));
            dirty = true;
        }
        return true;
    }

    public void save(Entity entity) {
        if (dirty) {
            entity.getPersistentDataContainer().set(KEY, TYPE, this);
            dirty = false;
        }
    }

    private void postLoad() {
        actionTriggerMap.putAll(persistentActionTriggerMap);
        triggerActionMap.putAll(persistentTriggerActionMap);
    }

    public static FcData get(Entity entity) {
        FcData data = dataCache.get(entity);
        if (data == null) {
            data = entity.getPersistentDataContainer().get(KEY, TYPE);
            if (data != null) {
                dataCache.put(entity, data);
            }
        }
        return data;
    }

    public static FcData getOrCreate(Entity entity) {
        FcData data = get(entity);
        if (data == null) {
            data = new FcData();
            dataCache.put(entity, data);
        }
        return data;
    }

    public static void removeIfEmpty(Entity entity) {
        FcData data = get(entity);
        if (data != null && data.actionTriggerMap.isEmpty()) {
            entity.getPersistentDataContainer().remove(KEY);
            dataCache.remove(entity);
        }
    }

}
