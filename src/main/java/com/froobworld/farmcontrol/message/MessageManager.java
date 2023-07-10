package com.froobworld.farmcontrol.message;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.message.adapter.MessageAdapter;
import com.froobworld.farmcontrol.message.adapter.PaperMessageAdapter;
import com.froobworld.farmcontrol.message.adapter.SpigotMessageAdapter;
import org.bukkit.entity.Player;

public class MessageManager {
    private final FarmControl farmControl;
    private MessageAdapter messageAdapter;

    public MessageManager(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    public void reload() throws Exception {
        if (PaperMessageAdapter.isCompatible()) {
            messageAdapter = new PaperMessageAdapter(farmControl);
        } else {
            messageAdapter = new SpigotMessageAdapter();
        }
    }

    public void sendMessage(Player player, String messageKey) {
        if (messageAdapter == null) {
            throw new IllegalStateException("No message adapter");
        }
        messageAdapter.sendMessage(player, messageKey);
    }

}
