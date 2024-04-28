package com.froobworld.farmcontrol.hook.nms;

import com.froobworld.farmcontrol.hook.nms.mobgoal.MobGoalNmsHook;
import com.froobworld.farmcontrol.hook.nms.mobgoal.Bukkit1_16MobGoalNmsHook;
import com.froobworld.farmcontrol.hook.nms.mobgoal.Bukkit1_20_5MobGoalNmsHook;
import com.froobworld.farmcontrol.hook.nms.mobgoal.Mojmap1_20MobGoalNmsHook;
import com.froobworld.farmcontrol.hook.nms.tick.Bukkit1_16TickTimesHook;
import com.froobworld.farmcontrol.hook.nms.tick.TickTimesNmsHook;

import java.util.stream.Stream;

public class NmsHooks {
    private final MobGoalNmsHook mobGoalNmsHook;
    private final TickTimesNmsHook tickTimesNmsHook;

    public NmsHooks() {
        mobGoalNmsHook = Stream.of(
                new Mojmap1_20MobGoalNmsHook(),
                new Bukkit1_20_5MobGoalNmsHook(),
                new Bukkit1_16MobGoalNmsHook()
                ).filter(MobGoalNmsHook::isCompatible)
                .findFirst()
                .orElse(null);
        tickTimesNmsHook = Stream.of(
                        new Bukkit1_16TickTimesHook()
                ).filter(TickTimesNmsHook::isCompatible)
                .findFirst()
                .orElse(null);
    }

    public MobGoalNmsHook getMobGoalNmsHook() {
        return mobGoalNmsHook;
    }

    public TickTimesNmsHook getTickTimeNmsHook() {
        return tickTimesNmsHook;
    }
}
