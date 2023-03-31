package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.api.event.PostEntityActionUndoEvent;
import com.froobworld.farmcontrol.api.event.PreEntityActionUndoEvent;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.function.Predicate;

public final class Actioner {

    private Actioner() {}

    public static void undoAllActions(Entity entity, FarmControl farmControl) {
        undoAllActions(entity, farmControl, action -> true);
    }

    public static void undoAllActions(Entity entity, FarmControl farmControl, Predicate<Action> actionPredicate) {
        if (!(entity instanceof Mob)) {
            return;
        }
        FcData fcData = FcData.get(entity);
        if (fcData == null) {
            return;
        }
        for (Action action : farmControl.getActionManager().getActions()) {
            if (!actionPredicate.test(action)) {
                continue;
            }
            if (fcData.removeAction(action)) {
                Bukkit.getPluginManager().callEvent(new PreEntityActionUndoEvent(entity, action));
                action.undoAction((Mob) entity);
                Bukkit.getPluginManager().callEvent(new PostEntityActionUndoEvent(entity, action));
            }
        }
        fcData.save(entity);
        FcData.removeIfEmpty(entity);
    }

}
