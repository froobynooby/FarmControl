package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.controller.action.RemoveRandomMovementAction;
import com.froobworld.farmcontrol.utils.Actioner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class CompatibilityListener implements Listener {
    private final FarmControl farmControl;
    private final FarmController farmController;

    public CompatibilityListener(FarmControl farmControl, FarmController farmController) {
        this.farmControl = farmControl;
        this.farmController = farmController;
        Bukkit.getPluginManager().registerEvents(this, farmControl);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Actioner.undoActions(event.getRightClicked(), action -> {
            return farmControl.getFcConfig().worldSettings.of(event.getRightClicked().getWorld()).actionSettings.undoOn.of(action).interact.get();
        }, farmControl);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Actioner.undoActions(event.getEntity(), action -> {
            return farmControl.getFcConfig().worldSettings.of(event.getEntity().getWorld()).actionSettings.undoOn.of(action).damage.get();
        }, farmControl);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null) {
            return;
        }
        Actioner.undoActions(event.getTarget(), action -> {
            return farmControl.getFcConfig().worldSettings.of(event.getEntity().getWorld()).actionSettings.undoOn.of(action).target.get();
        }, farmControl);
    }

    @EventHandler
    public void onEntityTempt(EntityTargetLivingEntityEvent event) {
        if (event.getReason() != EntityTargetEvent.TargetReason.TEMPT) {
            return;
        }
        Actioner.undoActions(event.getEntity(), action -> {
            if (action instanceof RemoveRandomMovementAction) return false; // Hacky solution for https://github.com/froobynooby/FarmControl/issues/4
            return farmControl.getFcConfig().worldSettings.of(event.getEntity().getWorld()).actionSettings.undoOn.of(action).tempt.get();
        }, farmControl);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            Actioner.undoActions(entity, Action::undoOnUnload, farmControl);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        farmController.addWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        farmController.removeWorld(event.getWorld());
    }

}
