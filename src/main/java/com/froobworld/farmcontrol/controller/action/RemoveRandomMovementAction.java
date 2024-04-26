package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.controller.movement.MovementSupressorManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class RemoveRandomMovementAction extends Action {
    private final MovementSupressorManager movementSupressorManager;

    public RemoveRandomMovementAction() {
        super("remove-random-movement", Mob.class, false, false, true);
        this.movementSupressorManager = new MovementSupressorManager();
    }

    @Override
    public void doAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        movementSupressorManager.suppress(mob);
    }

    @Override
    public void undoAction(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        movementSupressorManager.unsuppress(mob);
    }

}
