package com.froobworld.farmcontrol.utils;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public final class Actioner {

    private Actioner() {
    }

    public static void undoAllActions(Entity entity, FarmControl farmControl) {
        if (!(entity instanceof Mob)) {
            return;
        }
        FcData fcData = FcData.get(entity);
        if (fcData == null) {
            return;
        }
        for (Action action : farmControl.getActionManager().getActions()) {
            if (fcData.removeAction(action)) {
                action.undoAction((Mob) entity);
            }
        }
        fcData.save(entity);
        FcData.removeIfEmpty(entity);
    }

}
