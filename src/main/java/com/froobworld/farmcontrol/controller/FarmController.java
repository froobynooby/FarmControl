package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.task.ActionPerformTask;
import com.froobworld.farmcontrol.controller.task.TriggerCheckTask;
import com.froobworld.farmcontrol.controller.task.UntriggerPerformTask;
import com.froobworld.farmcontrol.controller.tracker.CycleHistoryManager;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.hook.scheduler.ScheduledTask;
import com.froobworld.farmcontrol.utils.Actioner;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.*;

public class FarmController {
    public static final Class<?>[] ENTITY_CLASSES = List.of(Mob.class, Vehicle.class, Projectile.class, Item.class).toArray(new Class[0]);
    private final FarmControl farmControl;
    private final CycleHistoryManager cycleHistoryManager;
    private final Map<World, Map<Trigger, Set<ActionProfile>>> worldTriggerProfilesMap = new HashMap<>();
    private final CompatibilityListener compatibilityListener;
    private ScheduledTask scheduledTask;
    private TriggerCheckTask triggerCheckTask = null;
    private boolean registered;

    public FarmController(FarmControl farmControl) {
        this.farmControl = farmControl;
        cycleHistoryManager = new CycleHistoryManager(farmControl);
        compatibilityListener = new CompatibilityListener(farmControl, this);
    }

    public void load() {
        for (World world : Bukkit.getWorlds()) {
            addWorld(world);
        }
    }

    public void unload() {
        Set<World> worlds = new HashSet<>(worldTriggerProfilesMap.keySet());
        for (World world : worlds) {
            removeWorld(world);
        }
        worldTriggerProfilesMap.clear();
    }

    public void reload() {
        unload();
        load();
    }

    public CycleHistoryManager getCycleHistoryManager() {
        return cycleHistoryManager;
    }

    public void addWorld(World world) {
        Map<Trigger, Set<ActionProfile>> triggerProfileMap = worldTriggerProfilesMap.computeIfAbsent(world, w -> new HashMap<>());
        Trigger proactiveTrigger = farmControl.getTriggerManager().getTrigger("proactive");
        for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.proactive.get()) {
            ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName.toLowerCase());
            if (actionProfile == null) {
                farmControl.getLogger().warning("Unknown profile for world '" + world.getName() + "': '" + profileName.toLowerCase() + "'");
                continue;
            }
            triggerProfileMap.computeIfAbsent(proactiveTrigger, trigger -> new HashSet<>()).add(actionProfile);
        }
        if (farmControl.getHookManager().getMsptTracker() != null) {
            Trigger reactiveTrigger = farmControl.getTriggerManager().getTrigger("reactive");
            for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
                ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName.toLowerCase());
                if (actionProfile == null) {
                    farmControl.getLogger().warning("Unknown profile for world '" + world.getName() + "': '" + profileName.toLowerCase() + "'");
                    continue;
                }
                triggerProfileMap.computeIfAbsent(reactiveTrigger, trigger -> new HashSet<>()).add(actionProfile);
            }
        } else {
            if (!farmControl.getFcConfig().worldSettings.of(world).profiles.proactive.get().isEmpty()) {
                farmControl.getLogger().warning("Reactive mode is not supported on your version - ignoring reactive profiles for world '" + world.getName() + "'");
            }
        }
    }

    public void removeWorld(World world) {
        worldTriggerProfilesMap.remove(world);
        farmControl.getHookManager().getEntityGetterHook().getEntities(world).thenAccept(entities -> {
            for (Entity entity : entities) {
                farmControl.getHookManager().getSchedulerHook().runEntityTaskAsap(
                        () -> Actioner.undoAllActions(entity, farmControl),
                        null, entity);
            }
        });
    }

    public void register() {
        if (scheduledTask != null) {
            throw new IllegalStateException("Already registered");
        }
        triggerCheckTask = new TriggerCheckTask(farmControl, this, worldTriggerProfilesMap);
        long cyclePeriod = farmControl.getFcConfig().cyclePeriod.get();
        long startUpDelay = farmControl.getFcConfig().startUpDelay.get();
        registered = true;
        scheduledTask = farmControl.getHookManager().getSchedulerHook().runRepeatingTask(triggerCheckTask, startUpDelay + cyclePeriod, cyclePeriod);
    }

    public void unRegister() {
        registered = false;
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
        triggerCheckTask.stop();
        triggerCheckTask = null;
    }

    public void submitActionPerformTask(ActionPerformTask actionPerformTask) {
        if (registered) {
            farmControl.getHookManager().getSchedulerHook().runTask(actionPerformTask);
        }
    }

    public void submitUntriggerPerformTask(UntriggerPerformTask untriggerPerformTask) {
        if (registered) {
            farmControl.getHookManager().getSchedulerHook().runTask(untriggerPerformTask);
        }
    }

}
