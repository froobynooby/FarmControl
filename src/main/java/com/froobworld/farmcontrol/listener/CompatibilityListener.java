package com.froobworld.farmcontrol.listener;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.RemoveRandomMovementAction;
import com.froobworld.farmcontrol.utils.Actioner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
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

    public CompatibilityListener(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Mob)) {
            return;
        }
        Actioner.undoAllActions(entity, farmControl, action -> {
            return farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).interact.get();
        });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        Actioner.undoAllActions(entity, farmControl, action -> {
            return farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).damage.get();
        });
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getTarget();
        if (!(entity instanceof Mob)) {
            return;
        }
        Actioner.undoAllActions(entity, farmControl, action -> {
            return farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).target.get();
        });
    }

    @EventHandler
    public void onEntityTempt(EntityTargetLivingEntityEvent event) {
        if (event.getReason() != EntityTargetEvent.TargetReason.TEMPT) {
            return;
        }
        Entity entity = event.getEntity();
        Actioner.undoAllActions(entity, farmControl, action -> {
            if (action instanceof RemoveRandomMovementAction) return false; // Hacky solution for https://github.com/froobynooby/FarmControl/issues/4
            return farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).tempt.get();
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            Actioner.undoAllActions(entity, farmControl);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        farmControl.getFarmController().addWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        farmControl.getFarmController().removeWorld(event.getWorld());
    }

}
