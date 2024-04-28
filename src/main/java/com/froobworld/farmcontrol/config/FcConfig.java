package com.froobworld.farmcontrol.config;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.nabconfiguration.*;
import com.froobworld.nabconfiguration.annotations.Entry;
import com.froobworld.nabconfiguration.annotations.EntryMap;
import com.froobworld.nabconfiguration.annotations.Section;
import com.froobworld.nabconfiguration.annotations.SectionMap;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class FcConfig extends NabConfiguration {
    private static final int CURRENT_VERSION = 9;

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

    @Entry(key = "start-up-delay")
    public final ConfigEntry<Long> startUpDelay = ConfigEntries.longEntry();

    @SuppressWarnings("Convert2MethodRef") // the compiler changes the method reference to WorldInfo:getName, which breaks compatibility
    @SectionMap(key = "world-settings", defaultKey = "default")
    public final ConfigSectionMap<World, WorldSettings> worldSettings = new ConfigSectionMap<>(world -> world.getName(), WorldSettings.class, true);

    public static class WorldSettings extends ConfigSection {

        @Section(key = "profiles")
        public final ProfilesSettings profiles = new ProfilesSettings();

        @Section(key = "exclusion-settings")
        public final ExclusionSettings exclusionSettings = new ExclusionSettings();

        @Section(key = "action-settings")
        public final ActionSettings actionSettings = new ActionSettings();

        @Section(key = "reactive-mode-settings")
        public final ReactiveModeSettings reactiveModeSettings = new ReactiveModeSettings();

        public static class ProfilesSettings extends ConfigSection {

            @Entry(key = "proactive")
            public final ConfigEntry<List<String>> proactive = ConfigEntries.stringListEntry();

            @Entry(key = "reactive")
            public final ConfigEntry<List<String>> reactive = ConfigEntries.stringListEntry();

        }

        public static class ReactiveModeSettings extends ConfigSection {

            @Entry(key = "trigger-mspt-threshold")
            public final ConfigEntry<Double> triggerMsptThreshold = ConfigEntries.doubleEntry();

            @Entry(key = "untrigger-mspt-threshold")
            public final ConfigEntry<Double> untriggerMsptThreshold = ConfigEntries.doubleEntry();

            @Section(key = "untrigger-settings")
            public final UntriggerSettings untriggerSettings = new UntriggerSettings();

            public static class UntriggerSettings extends ConfigSection {

                @Entry(key = "minimum-cycles-before-undo")
                public final ConfigEntry<Integer> minimumCyclesBeforeUndo = ConfigEntries.integerEntry();

                @Entry(key = "maximum-undos-per-cycle")
                public final ConfigEntry<Integer> maximumUndosPerCycle = ConfigEntries.integerEntry();

                @EntryMap(key = "entity-undo-weight", defaultKey = "default")
                public final ConfigEntryMap<EntityType, Double> entityUndoWeight = new ConfigEntryMap<>(Objects::toString, ConfigEntries::doubleEntry, true);

            }

        }

        public static class ExclusionSettings extends ConfigSection {

            @Entry(key = "tamed")
            public final ConfigEntry<Boolean> tamed = new ConfigEntry<>();

            @Entry(key = "named")
            public final ConfigEntry<Boolean> named = new ConfigEntry<>();

            @Entry(key = "love-mode")
            public final ConfigEntry<Boolean> loveMode = new ConfigEntry<>();

            @Entry(key = "leashed")
            public final ConfigEntry<Boolean> leashed = new ConfigEntry<>();

            @Entry(key = "patrol-leader")
            public final ConfigEntry<Boolean> patrolLeader = new ConfigEntry<>();

            @Entry(key = "pickupable")
            public final ConfigEntry<Boolean> pickupable = new ConfigEntry<>();

            @Entry(key = "mounted")
            public final ConfigEntry<Boolean> mounted = new ConfigEntry<>();

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

                @Entry(key = "tempt")
                public final ConfigEntry<Boolean> tempt = new ConfigEntry<>();

            }

        }

    }

    @Section(key = "mspt-tracker-settings")
    public final MsptTrackerSettings msptTracker = new MsptTrackerSettings();

    public static class MsptTrackerSettings extends ConfigSection {

        @Entry(key = "collection-period")
        public final ConfigEntry<Integer> collectionPeriod = ConfigEntries.integerEntry();

    }

}
