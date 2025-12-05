package com.froobworld.farmcontrol.controller.tracker;

import com.froobworld.farmcontrol.FarmControl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationCentre {
    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private final FarmControl farmControl;
    private final ExecutorService executorService;
    private final Set<UUID> notifiableUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public NotificationCentre(FarmControl farmControl) {
        this.farmControl = farmControl;
        this.executorService = Executors.newSingleThreadExecutor();
        load();
    }

    public void notify(CycleStats cycleStats) {
        ComponentBuilder notificationBuilder = new ComponentBuilder()
                .append("FarmControl - ").color(ChatColor.GOLD)
                .append("" + cycleStats.getAffectedEntityCount()).color(ChatColor.RED)
                .append(" entities affected").color(ChatColor.GRAY);
        if (cycleStats.getEntitiesRemoved() > 0) {
            notificationBuilder
                    .append(", including ").color(ChatColor.GRAY)
                    .append("" + cycleStats.getEntitiesRemoved()).color(ChatColor.RED)
                    .append(" removed.").color(ChatColor.GRAY);
        } else {
            notificationBuilder.append(".").color(ChatColor.GRAY);
        }
        BaseComponent[] breakdown = cycleStats.getBreakdown();
        notificationBuilder.getParts().forEach(part -> part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, breakdown)));
        BaseComponent[] message = notificationBuilder.create();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("farmcontrol.command.notify")) {
                continue;
            }
            if (notifiableUsers.contains(player.getUniqueId())) {
                player.spigot().sendMessage(message);
            }
        }
        if (notifiableUsers.contains(CONSOLE_UUID)) {
            Bukkit.getConsoleSender().spigot().sendMessage(message);
        }
    }

    public void setNotifiable(CommandSender sender, boolean notifiable) {
        UUID uuid;
        if (sender instanceof Player) {
            uuid = ((Player) sender).getUniqueId();
        } else {
            uuid = CONSOLE_UUID;
        }
        if (notifiable) {
            notifiableUsers.add(uuid);
        } else {
            notifiableUsers.remove(uuid);
        }
        save();
    }

    public boolean isNotifiable(CommandSender sender) {
        UUID uuid;
        if (sender instanceof Player) {
            uuid = ((Player) sender).getUniqueId();
        } else {
            uuid = CONSOLE_UUID;
        }
        return notifiableUsers.contains(uuid);
    }

    public synchronized void load() {
        notifiableUsers.clear();
        File userFile = new File(farmControl.getDataFolder(), "notifiable-users.txt");
        if (!userFile.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            for(String line; (line = reader.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    notifiableUsers.add(UUID.fromString(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void save() {
        final Set<UUID> users = new HashSet<>(notifiableUsers);
        final File userFile = new File(farmControl.getDataFolder(), "notifiable-users.txt");
        executorService.submit(() -> {
            if (!userFile.exists()) {
                try {
                    userFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(userFile, false))) {
                for (UUID uuid : users) {
                    writer.println(uuid.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
