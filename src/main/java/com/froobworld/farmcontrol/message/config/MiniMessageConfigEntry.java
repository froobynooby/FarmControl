package com.froobworld.farmcontrol.message.config;

import com.froobworld.nabconfiguration.ConfigEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MiniMessageConfigEntry extends ConfigEntry<Component> {

    public MiniMessageConfigEntry(TagResolver... tagResolvers) {
        super(string -> MiniMessage.miniMessage().deserialize((String) string, tagResolvers));
    }

}
