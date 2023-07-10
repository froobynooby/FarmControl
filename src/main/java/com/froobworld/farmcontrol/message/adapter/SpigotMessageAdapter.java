package com.froobworld.farmcontrol.message.adapter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpigotMessageAdapter implements MessageAdapter {

    @Override
    public void sendMessage(Player player, String messageKey) {
        String message = getMessage(messageKey);
        if (message == null) {
            throw new IllegalArgumentException("No message for key '" + messageKey + "'");
        }
        player.sendMessage(message);
    }

    private String getMessage(String messageKey) {
        if (messageKey.equalsIgnoreCase("breeding-disabled")) {
            return ChatColor.RED + "Breeding has been disabled for this animal.";
        }
        return null;
    }

}
