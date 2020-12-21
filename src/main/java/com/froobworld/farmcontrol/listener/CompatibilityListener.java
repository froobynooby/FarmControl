package com.froobworld.farmcontrol.listener;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.action.Action;
import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

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
        FcData fcData = FcData.get(entity);
        if (fcData == null) {
            return;
        }
        for (Action action : farmControl.getActionManager().getActions()) {
            if (farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).interact.get()) {
                if (fcData.removeAction(action)) {
                    action.undoAction((Mob) entity);
                }
            }
        }
        fcData.save(entity);
        FcData.removeIfEmpty(entity);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        FcData fcData = FcData.get(entity);
        if (fcData == null) {
            return;
        }
        for (Action action : farmControl.getActionManager().getActions()) {
            if (farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).damage.get()) {
                if (fcData.removeAction(action)) {
                    action.undoAction((Mob) entity);
                }
            }
        }
        fcData.save(entity);
        FcData.removeIfEmpty(entity);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getTarget();
        if (!(entity instanceof Mob)) {
            return;
        }
        FcData fcData = FcData.get(entity);
        if (fcData == null) {
            return;
        }
        for (Action action : farmControl.getActionManager().getActions()) {
            if (farmControl.getFcConfig().worldSettings.of(entity.getWorld()).actionSettings.undoOn.of(action).target.get()) {
                if (fcData.removeAction(action)) {
                    action.undoAction((Mob) entity);
                }
            }
        }
        fcData.save(entity);
        FcData.removeIfEmpty(entity);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            FcData fcData = FcData.get(entity);
            if (fcData == null) {
                return;
            }
            for (Action action : farmControl.getActionManager().getActions()) {
                if (fcData.removeAction(action)) {
                    action.undoAction((Mob) entity);
                }
            }
            fcData.save(entity);
            FcData.removeIfEmpty(entity);
        }
    }

}
