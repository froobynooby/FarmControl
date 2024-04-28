package com.froobworld.farmcontrol.hook.nms.mobgoal;

import com.froobworld.farmcontrol.utils.NmsUtils;

import java.util.List;

public class Bukkit1_16MobGoalNmsHook extends BaseMobGoalNmsHook {

    public Bukkit1_16MobGoalNmsHook() {
        super(
                "d",
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
