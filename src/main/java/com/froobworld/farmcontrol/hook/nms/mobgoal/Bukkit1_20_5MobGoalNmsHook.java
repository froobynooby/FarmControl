package com.froobworld.farmcontrol.hook.nms.mobgoal;

import com.froobworld.farmcontrol.utils.NmsUtils;

import java.util.List;

public class Bukkit1_20_5MobGoalNmsHook extends BaseMobGoalNmsHook {

    public Bukkit1_20_5MobGoalNmsHook() {
        super(
                "c",
                "a",
                NmsUtils.getFullyQualifiedClassName("EntityInsentient", "world.entity"),
                NmsUtils.getFullyQualifiedClassName("PathfinderGoalSelector", "world.entity.ai.goal"),
                NmsUtils.getFullyQualifiedClassName("PathfinderGoalWrapped", "world.entity.ai.goal"),
                NmsUtils.getFullyQualifiedClassName("PathfinderGoal", "world.entity.ai.goal"),
                List.of(
                        NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomFly", "world.entity.ai.goal"),
                        NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomStroll", "world.entity.ai.goal"),
                        NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomStrollLand", "world.entity.ai.goal"),
                        NmsUtils.getFullyQualifiedClassName("PathfinderGoalRandomSwim", "world.entity.ai.goal")

                )
        );
    }

}
