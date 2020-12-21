package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.trigger.Trigger;
import com.froobworld.farmcontrol.group.GroupDefinition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ProfileManager {
    private final FarmControl farmControl;
    private final Map<String, UnpairedActionProfile> unpairedActionProfileMap = new HashMap<>();

    public ProfileManager(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    public ActionProfile getActionProfile(Trigger trigger, String profileName) {
        UnpairedActionProfile unpairedActionProfile = unpairedActionProfileMap.get(profileName);
        if (unpairedActionProfile == null) {
            return null;
        }
        return unpairedActionProfile.pair(trigger);
    }

    public void load() throws IOException {
        File file = new File(farmControl.getDataFolder(), "profiles.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            Files.copy(Objects.requireNonNull(farmControl.getResource("resources/profiles.yml")), file.toPath());
        }
        ConfigurationSection profilesSection = YamlConfiguration.loadConfiguration(file).getConfigurationSection("profiles");
        for (String name : Objects.requireNonNull(profilesSection).getKeys(false)) {
            try {
                ConfigurationSection profileSection = Objects.requireNonNull(profilesSection.getConfigurationSection(name));
                GroupDefinition groupDefinition = GroupDefinition.fromConfigurationSection(Objects.requireNonNull(profileSection.getConfigurationSection("group")));
                Set<Action> actions = new HashSet<>();
                for (String actionName : profileSection.getStringList("actions")) {
                    Action action = farmControl.getActionManager().getAction(actionName.toLowerCase());
                    if (action == null) {
                        farmControl.getLogger().warning("Unknown action for profile '" + name + "': '" + actionName.toLowerCase() + "'");
                        continue;
                    }
                    actions.add(action);
                }
                unpairedActionProfileMap.put(name, new UnpairedActionProfile(groupDefinition, actions));
            } catch (Exception ex) {
                farmControl.getLogger().warning("Unable to load the profile '" + name + "'. Incorrect syntax?");
            }
        }
    }

    public void reload() throws IOException {
        unpairedActionProfileMap.clear();
        load();
    }

    private static class UnpairedActionProfile {
        private final GroupDefinition groupDefinition;
        private final Set<Action> actions;

        private UnpairedActionProfile(GroupDefinition groupDefinition, Set<Action> actions) {
            this.groupDefinition = groupDefinition;
            this.actions = actions;
        }

        public ActionProfile pair(Trigger trigger) {
            return new ActionProfile(trigger, groupDefinition, actions);
        }

    }

}
