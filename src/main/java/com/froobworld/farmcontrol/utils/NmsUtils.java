package com.froobworld.farmcontrol.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

import static org.joor.Reflect.on;

public class NmsUtils {
    private static final String NMS_PACKAGE_NAME = on(Bukkit.getServer()).call("getHandle")
            .type()
            .getPackage()
            .getName();
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "");
    private static final boolean PRE_1_17 = NMS_PACKAGE_NAME.contains("1");

    public static String getFullyQualifiedClassName(String className, String post1_17PackageName) {
        return (PRE_1_17 ? NMS_PACKAGE_NAME : ("net.minecraft." + post1_17PackageName)) + "." + className;
    }

    public static String getFieldOrMethodName(String pre1_17Name, String post1_17Name) {
        return PRE_1_17 ? pre1_17Name : post1_17Name;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static class GoalSelectorHelper {
        private static boolean compatible = true;
        private static String goalSelectorFieldName;

        static {
            try {
                Class<?> entityInsentientClass = Class.forName(getFullyQualifiedClassName("EntityInsentient", "world.entity"));
                Class<?> goalSelectorClass = Class.forName(getFullyQualifiedClassName("PathfinderGoalSelector", "world.entity.ai.goal"));
                for (Field field : entityInsentientClass.getFields()) {
                    if (field.getType().equals(goalSelectorClass)) {
                        goalSelectorFieldName = field.getName();
                        break;
                    }
                }
                if (goalSelectorFieldName == null) {
                    compatible = false;
                }
            } catch (ClassNotFoundException e) {
                compatible = false;
            }
        }

        public static String getGoalSelectorFieldName() {
            return goalSelectorFieldName;
        }

        public static boolean isCompatible() {
            return compatible;
        }

    }

}
