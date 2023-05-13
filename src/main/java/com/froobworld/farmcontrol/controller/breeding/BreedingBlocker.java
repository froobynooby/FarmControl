package com.froobworld.farmcontrol.controller.breeding;

import com.froobworld.farmcontrol.FarmControl;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BreedingBlocker implements Listener {
    private static final String  FAILURE_MESSAGE = ChatColor.RED + "Breeding has been disabled for this animal.";
    private static final long MESSAGE_RATE_LIMIT = TimeUnit.MILLISECONDS.toMillis(500);
    private final Map<Player, Map<UUID, Long>> lastMessage = new MapMaker().weakKeys().makeMap();
    private final NamespacedKey pdcKey;

    public BreedingBlocker(FarmControl farmControl) {
        pdcKey = NamespacedKey.fromString("breeding-disabled", farmControl);
        Bukkit.getPluginManager().registerEvents(this, farmControl);
    }

    public void setBreedingDisabled(Entity entity, boolean disabled) {
        if (disabled) {
            entity.getPersistentDataContainer().set(pdcKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            entity.getPersistentDataContainer().remove(pdcKey);
        }
    }

    public boolean isBreedingDisabled(Entity entity) {
        return entity.getPersistentDataContainer().has(pdcKey);
    }

    private void sendFailureMessage(Player player, Entity entity) {
        // Don't spam failure messages for attempting to breed the same animal in quick succession
        if (System.currentTimeMillis() - lastMessage.computeIfAbsent(player, k -> new HashMap<>()).computeIfAbsent(entity.getUniqueId(), k -> 0L) > MESSAGE_RATE_LIMIT) {
            player.sendMessage(FAILURE_MESSAGE);
            lastMessage.get(player).put(entity.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEnterLoveMode(EntityEnterLoveModeEvent event) {
        if (isBreedingDisabled(event.getEntity())) {
            event.setCancelled(true);
            if (event.getHumanEntity() instanceof Player) {
                sendFailureMessage((Player) event.getHumanEntity(), event.getEntity());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onBreed(EntityBreedEvent event) {
        if (isBreedingDisabled(event.getMother()) && isBreedingDisabled(event.getFather())) {
            event.setCancelled(true);
        }
    }

}
