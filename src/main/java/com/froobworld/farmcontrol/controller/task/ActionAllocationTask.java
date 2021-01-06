package com.froobworld.farmcontrol.controller.task;

import com.froobworld.farmcontrol.controller.ActionProfile;
import com.froobworld.farmcontrol.controller.FarmController;
import com.froobworld.farmcontrol.controller.TriggerActionPair;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.data.FcData;
import com.froobworld.farmcontrol.group.EntityGrouper;
import com.froobworld.farmcontrol.group.EntityGrouperResult;
import com.froobworld.farmcontrol.group.Group;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.utils.MixedEntitySet;

import java.util.*;
import java.util.function.Predicate;

public class ActionAllocationTask implements Runnable {
    private final FarmController farmController;
    private final Set<Trigger> triggers;
    private final List<SnapshotEntity> snapshotEntities;
    private final List<ActionProfile> actionProfiles;
    private final Predicate<SnapshotEntity> shouldExcludePredicate;
    private final Set<Action> allActions;

    public ActionAllocationTask(FarmController farmController, Set<Trigger> triggers, List<SnapshotEntity> snapshotEntities, List<ActionProfile> actionProfiles, Predicate<SnapshotEntity> shouldExcludePredicate, Set<Action> allActions) {
        this.farmController = farmController;
        this.triggers = triggers;
        this.snapshotEntities = snapshotEntities;
        this.actionProfiles = actionProfiles;
        this.shouldExcludePredicate = shouldExcludePredicate;
        this.allActions = allActions;
    }

    @Override
    public void run() {
        Map<SnapshotEntity, Set<TriggerActionPair>> triggerActionMap = new HashMap<>();
        Map<SnapshotEntity, Set<TriggerActionPair>> unTriggerActionMap = new HashMap<>();

        actionProfiles.sort(Comparator.comparingInt(profile -> profile.removes() ? -1 : 1));
        for (ActionProfile actionProfile : actionProfiles) {
            boolean removes = actionProfile.removes();
            EntityGrouperResult result = EntityGrouper.groupEntities(snapshotEntities, actionProfile.getGroupDefinition());
            for (Group group : result.getGroups()) {
                MixedEntitySet.MixedEntityIterator iterator = group.getMembers().iterator();
                while (group.meetsCondition() && iterator.hasNext()) {
                    SnapshotEntity next = iterator.next();
                    if (shouldExcludePredicate.test(next)) {
                        iterator.skipLast();
                        continue;
                    }
                    if (removes) {
                        snapshotEntities.remove(next);
                        iterator.remove();
                    }
                    Set<TriggerActionPair> triggerActionPairs = triggerActionMap.computeIfAbsent(next, e -> new HashSet<>());
                    Set<TriggerActionPair> unTriggerActionPairs = unTriggerActionMap.getOrDefault(next, Collections.emptySet());
                    for (Action action : actionProfile.getActions()) {
                        TriggerActionPair triggerActionPair = new TriggerActionPair(actionProfile.getTrigger(), action);
                        triggerActionPairs.add(triggerActionPair);
                        unTriggerActionPairs.remove(triggerActionPair);
                    }
                }
            }
        }
        for (SnapshotEntity entity : snapshotEntities) {
            FcData fcData = entity.getFcData();
            Set<String> appliedActions;
            for (Trigger trigger : triggers) {
                if (fcData != null && (appliedActions = fcData.getActions(trigger)) != null && !appliedActions.isEmpty()) {
                    for (Action action : allActions) {
                        TriggerActionPair triggerActionPair = new TriggerActionPair(trigger, action);
                        if (appliedActions.contains(action.getName()) && !triggerActionMap.getOrDefault(entity, Collections.emptySet()).contains(triggerActionPair)) {
                            unTriggerActionMap.computeIfAbsent(entity, e -> new HashSet<>()).add(triggerActionPair);
                        }
                    }
                }
            }
        }

        farmController.submitActionPerformTask(new ActionPerformTask(triggerActionMap, unTriggerActionMap));
    }

}
