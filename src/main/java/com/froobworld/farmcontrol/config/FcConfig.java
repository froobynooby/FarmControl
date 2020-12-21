package com.froobworld.farmcontrol.config;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.nabconfiguration.*;
import com.froobworld.nabconfiguration.annotations.Entry;
import com.froobworld.nabconfiguration.annotations.Section;
import com.froobworld.nabconfiguration.annotations.SectionMap;
import org.bukkit.World;

import java.io.File;
import java.util.List;

public class FcConfig extends NabConfiguration {
    private static final int CURRENT_VERSION = 1;

    public FcConfig(FarmControl farmControl) {
        super(
                new File(farmControl.getDataFolder(), "config.yml"),
                () -> farmControl.getResource("resources/config.yml"),
                i -> farmControl.getResource("resources/config-patches/" + i + ".patch"),
                CURRENT_VERSION
        );
    }

    @Entry(key = "cycle-period")
    public final ConfigEntry<Long> cyclePeriod = ConfigEntries.longEntry();

    @SectionMap(key = "world-settings", defaultKey = "default")
    public final ConfigSectionMap<World, WorldSettings> worldSettings = new ConfigSectionMap<>(World::getName, WorldSettings.class, true);

    public static class WorldSettings extends ConfigSection {

        @Entry(key = "profiles")
        public final ConfigEntry<List<String>> profiles = ConfigEntries.stringListEntry();

        @Section(key = "exclusion-settings")
        public final ExclusionSettings exclusionSettings = new ExclusionSettings();

        @Section(key = "action-settings")
        public final ActionSettings actionSettings = new ActionSettings();

        public static class ExclusionSettings extends ConfigSection {

            @Entry(key = "tamed")
            public final ConfigEntry<Boolean> tamed = new ConfigEntry<>();

            @Entry(key = "named")
            public final ConfigEntry<Boolean> named = new ConfigEntry<>();

            @Entry(key = "love-mode")
            public final ConfigEntry<Boolean> loveMode = new ConfigEntry<>();

            @Entry(key = "leashed")
            public final ConfigEntry<Boolean> leashed = new ConfigEntry<>();

            @Entry(key = "younger-than")
            public final ConfigEntry<Long> youngerThan = ConfigEntries.longEntry();

            @Entry(key = "type")
            public final ConfigEntry<List<String>> type = ConfigEntries.stringListEntry();

            @Entry(key = "metadata")
            public final ConfigEntry<List<String>> metadata = ConfigEntries.stringListEntry();

        }

        public static class ActionSettings extends ConfigSection {

            @SectionMap(key = "undo-on", defaultKey = "default")
            public final ConfigSectionMap<Action, UndoOnSettings> undoOn = new ConfigSectionMap<>(Action::getName, UndoOnSettings.class, true);

            public static class UndoOnSettings extends ConfigSection {

                @Entry(key = "interact")
                public final ConfigEntry<Boolean> interact = new ConfigEntry<>();

                @Entry(key = "damage")
                public final ConfigEntry<Boolean> damage = new ConfigEntry<>();

                @Entry(key = "target")
                public final ConfigEntry<Boolean> target = new ConfigEntry<>();

            }

        }

    }


}
