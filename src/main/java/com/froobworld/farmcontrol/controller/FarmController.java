package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.task.ActionPerformTask;
import com.froobworld.farmcontrol.controller.task.TriggerCheckTask;
import com.froobworld.farmcontrol.controller.task.UntriggerPerformTask;
import com.froobworld.farmcontrol.controller.tracker.CycleHistoryManager;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.utils.Actioner;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FarmController {
    private final FarmControl farmControl;
    private final CycleHistoryManager cycleHistoryManager;
    private final Map<World, Map<Trigger, Set<ActionProfile>>> worldTriggerProfilesMap = new HashMap<>();
    private Integer triggerTaskId = null;
    private TriggerCheckTask triggerCheckTask = null;
    private boolean registered;

    public FarmController(FarmControl farmControl) {
        this.farmControl = farmControl;
        cycleHistoryManager = new CycleHistoryManager(farmControl);
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
        Trigger reactiveTrigger = farmControl.getTriggerManager().getTrigger("reactive");
        for (String profileName : farmControl.getFcConfig().worldSettings.of(world).profiles.reactive.get()) {
            ActionProfile actionProfile = farmControl.getProfileManager().getActionProfile(profileName.toLowerCase());
            if (actionProfile == null) {
                farmControl.getLogger().warning("Unknown profile for world '" + world.getName() + "': '" + profileName.toLowerCase() + "'");
                continue;
            }
            triggerProfileMap.computeIfAbsent(reactiveTrigger, trigger -> new HashSet<>()).add(actionProfile);
        }
    }

    public void removeWorld(World world) {
        worldTriggerProfilesMap.remove(world);
        for (Entity entity : world.getLivingEntities()) {
            Actioner.undoAllActions(entity, farmControl);
        }
    }

    public void register() {
        if (triggerTaskId != null) {
            throw new IllegalStateException("Already registered");
        }
        triggerCheckTask = new TriggerCheckTask(farmControl, this, worldTriggerProfilesMap);
        long cyclePeriod = farmControl.getFcConfig().cyclePeriod.get();
        long startUpDelay = farmControl.getFcConfig().startUpDelay.get();
        registered = true;
        triggerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(farmControl, triggerCheckTask, startUpDelay + cyclePeriod, cyclePeriod);
    }

    public void unRegister() {
        registered = false;
        Bukkit.getScheduler().cancelTask(triggerTaskId);
        triggerTaskId = null;
        triggerCheckTask.stop();
        triggerCheckTask = null;
    }

    public void submitActionPerformTask(ActionPerformTask actionPerformTask) {
        if (registered) {
            Bukkit.getScheduler().runTask(farmControl, actionPerformTask);
        }
    }

    public void submitUntriggerPerformTask(UntriggerPerformTask untriggerPerformTask) {
        if (registered) {
            Bukkit.getScheduler().runTask(farmControl, untriggerPerformTask);
        }
    }

}
