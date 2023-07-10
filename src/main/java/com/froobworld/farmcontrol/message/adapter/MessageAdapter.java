package com.froobworld.farmcontrol.message.adapter;

import org.bukkit.entity.Player;

public interface MessageAdapter {

    void sendMessage(Player player, String messageKey);

}
