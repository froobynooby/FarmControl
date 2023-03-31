package com.froobworld.farmcontrol;

import com.froobworld.farmcontrol.api.action.Action;
import com.froobworld.farmcontrol.api.action.ActionBuilder;
import com.froobworld.farmcontrol.controller.action.CustomAction;
import org.bukkit.entity.Mob;

import java.util.function.Predicate;

public class FarmControlApi implements com.froobworld.farmcontrol.api.FarmControlApi {
    private final FarmControl farmControl;

    public FarmControlApi(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    @Override
    public ActionBuilder createAction(String actionName) {
        return new CustomAction.CustomActionBuilder(actionName);
    }

    @Override
    public void registerAction(Action action) {
        if (!(action instanceof CustomAction)) {
            throw new IllegalArgumentException("Can only register custom actions");
        }
        farmControl.getActionManager().addAction((CustomAction) action);
    }

    @Override
    public void unregisterAction(Action action) {
        if (!(action instanceof CustomAction)) {
            throw new IllegalArgumentException("Can only unregister custom actions");
        }
        farmControl.getActionManager().removeAction((CustomAction) action);
    }

    @Override
    public void registerExclusion(Predicate<Mob> exclusionPredicate) {
        farmControl.getExclusionManager().addCustomPredicate(exclusionPredicate);
    }

    @Override
    public void unregisterExclusion(Predicate<Mob> exclusionPredicate) {
        farmControl.getExclusionManager().removeCustomPredicate(exclusionPredicate);
    }

}
