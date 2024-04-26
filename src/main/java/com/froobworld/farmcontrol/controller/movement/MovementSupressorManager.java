package com.froobworld.farmcontrol.controller.movement;

import org.bukkit.entity.Mob;

public class MovementSupressorManager {
    private final GoalSuppressor goalSuppressor;
    private final BehaviourSuppressor behaviourSuppressor;

    public MovementSupressorManager() {
        this.goalSuppressor = new GoalSuppressor();
        this.behaviourSuppressor = new BehaviourSuppressor();
    }

    public void suppress(Mob mob) {
        goalSuppressor.suppress(mob);
        behaviourSuppressor.suppress(mob);
    }

    public void unsuppress(Mob mob) {
        goalSuppressor.unsuppress(mob);
        behaviourSuppressor.unsuppress(mob);
    }

}
