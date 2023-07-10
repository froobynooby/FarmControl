package com.froobworld.farmcontrol.message.config;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.nabconfiguration.ConfigEntry;
import com.froobworld.nabconfiguration.NabConfiguration;
import com.froobworld.nabconfiguration.annotations.Entry;
import net.kyori.adventure.text.Component;

import java.io.File;

public class MessagesConfig extends NabConfiguration {
    private static final int CURRENT_VERSION = 1;

    public MessagesConfig(FarmControl farmControl) {
        super(
                new File(farmControl.getDataFolder(), "messages.yml"),
                () -> farmControl.getResource("resources/messages.yml"),
                i -> farmControl.getResource("resources/messages-patches/" + i + ".patch"),
                CURRENT_VERSION
        );
    }

    @Entry(key = "breeding-disabled")
    public final ConfigEntry<Component> breedingDisabled = new MiniMessageConfigEntry();

}
