package com.froobworld.farmcontrol.hook.nms.mobgoal;

import java.util.List;

public class Mojmap1_20MobGoalNmsHook extends BaseMobGoalNmsHook {

    public Mojmap1_20MobGoalNmsHook() {
        super(
                "availableGoals",
                "goal",
                "net.minecraft.world.entity.Mob",
                "net.minecraft.world.entity.ai.goal.GoalSelector",
                "net.minecraft.world.entity.ai.goal.WrappedGoal",
                "net.minecraft.world.entity.ai.goal.Goal",
                List.of(
                        "net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal",
                        "net.minecraft.world.entity.ai.goal.RandomStrollGoal",
                        "net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal",
                        "net.minecraft.world.entity.ai.goal.RandomSwimmingGoal"
                )
        );
    }

}
