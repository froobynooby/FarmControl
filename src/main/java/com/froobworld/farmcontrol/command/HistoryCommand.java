package com.froobworld.farmcontrol.command;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.controller.tracker.CycleStats;
import com.froobworld.farmcontrol.utils.DurationDisplayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryCommand implements CommandExecutor {
    private final FarmControl farmControl;

    public HistoryCommand(FarmControl farmControl) {
        this.farmControl = farmControl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<CycleStats> cycleHistory = farmControl.getFarmController().getCycleHistoryManager().getCycleHistory();
        if (cycleHistory.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There is no history to display.");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "Most recent history:");
        for (CycleStats cycleStats : cycleHistory) {
            sender.sendMessage("");
            long timeSinceCycle = System.currentTimeMillis() - cycleStats.getStartTimeMillis();
            sender.sendMessage(ChatColor.GRAY + "From " + ChatColor.RED + DurationDisplayer.getDurationInMinutesAndSeconds(timeSinceCycle) + ChatColor.GRAY + " ago:");
            ComponentBuilder builder = new ComponentBuilder("- ").color(net.md_5.bungee.api.ChatColor.GOLD);
            builder.append("Total of ").color(net.md_5.bungee.api.ChatColor.GRAY)
                    .append(cycleStats.getAffectedEntityCount() + "").color(net.md_5.bungee.api.ChatColor.RED)
                    .append(" entities affected").color(net.md_5.bungee.api.ChatColor.GRAY);
            if (cycleStats.getEntitiesRemoved() > 0) {
                builder.append(", with ").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .append(cycleStats.getEntitiesRemoved() + "").color(net.md_5.bungee.api.ChatColor.RED)
                        .append(" removed.").color(net.md_5.bungee.api.ChatColor.GRAY);
            } else {
                builder.append(".").color(net.md_5.bungee.api.ChatColor.GRAY);
            }
            BaseComponent[] breakdown = cycleStats.getBreakdown();
            builder.getParts().forEach(part -> part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, breakdown)));

            sender.spigot().sendMessage(builder.create());
        }

        return true;
    }
}
