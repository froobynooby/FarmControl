package com.froobworld.farmcontrol.command;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StatusCommand implements CommandExecutor {
    private FarmControl farmControl;

    public StatusCommand(FarmControl farmControl) {
        this.farmControl = farmControl;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cl, @NotNull String[] args) {
        World world;
        if (args.length == 1) {
            if (sender instanceof Player) {
                world = ((Player) sender).getWorld();
            } else {
                sender.sendMessage(ChatColor.RED + "You must specify a world.");
                return false;
            }
        } else {
            world = Bukkit.getWorld(args[1]);
        }
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Unknown world.");
            return true;
        }
        int entityCount = 0;
        int affectedCount = 0;
        Map<String, AtomicInteger> actionCount = new HashMap<>();
        for (LivingEntity entity : world.getLivingEntities()) {
            entityCount++;
            FcData fcData = FcData.get(entity);
            if (fcData != null) {
                affectedCount++;
                for (String action : fcData.getActions()) {
                    actionCount.computeIfAbsent(action, a -> new AtomicInteger(0)).incrementAndGet();
                }
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Status for world '" + ChatColor.RED + world.getName() + ChatColor.GRAY + "'");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Total entities: " + ChatColor.RED + entityCount);
        sender.sendMessage(ChatColor.GOLD + "Total affected entities: " + ChatColor.RED + affectedCount);
        if (affectedCount > 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Breakdown:");
            for (String action : actionCount.keySet()) {
                sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GRAY + action + ": " + ChatColor.RED + actionCount.get(action));
            }
        }
        return true;
    }

}
