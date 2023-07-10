package com.froobworld.farmcontrol.message.adapter;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.message.config.MessagesConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PaperMessageAdapter implements MessageAdapter {
    private final MessagesConfig messagesConfig;

    public PaperMessageAdapter(FarmControl farmControl) throws Exception {
        this.messagesConfig = new MessagesConfig(farmControl);
        messagesConfig.load();
    }

    @Override
    public void sendMessage(Player player, String messageKey) {
        Component message = getMessage(messageKey);
        if (message == null) {
            throw new IllegalArgumentException("No message for key '" + messageKey + "'");
        }
        player.sendMessage(message);
    }

    private Component getMessage(String messageKey) {
        if (messageKey.equalsIgnoreCase("breeding-disabled")) {
            return messagesConfig.breedingDisabled.get();
        }
        return null;
    }

    public static boolean isCompatible() {
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

}
