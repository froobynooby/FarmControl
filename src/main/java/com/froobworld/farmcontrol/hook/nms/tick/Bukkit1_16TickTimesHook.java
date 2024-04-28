package com.froobworld.farmcontrol.hook.nms.tick;

import com.froobworld.farmcontrol.utils.NmsUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

import static org.joor.Reflect.on;

public class Bukkit1_16TickTimesHook implements TickTimesNmsHook {
    private long[] tickTimes;
    private boolean compatible = true;

    public Bukkit1_16TickTimesHook() {
        initTickTimes();
    }

    @Override
    public long[] getTickTimes() {
        return tickTimes;
    }

    @Override
    public boolean isCompatible() {
        return compatible;
    }

    private void initTickTimes() {
        try {
            Class<?> serverClass = Class.forName(NmsUtils.getFullyQualifiedClassName("MinecraftServer", "server"));
            String fieldName = null;
            for (Field field : serverClass.getDeclaredFields()) {
                if (field.getType().equals(long[].class)) {
                    if (fieldName != null) {
                        compatible = false; // we've found multiple possible field names
                        return;
                    }
                    fieldName = field.getName();
                }
            }
            tickTimes = on(Bukkit.getServer())
                    .call("getServer")
                    .get(fieldName);
        } catch (Exception ignored) {}
    }

}
