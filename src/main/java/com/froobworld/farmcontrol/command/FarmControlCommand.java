package com.froobworld.farmcontrol.command;

import com.froobworld.farmcontrol.FarmControl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FarmControlCommand implements CommandExecutor {
    public static final String NO_PERMISSION_MESSAGE = ChatColor.RED + "You don't have permission to use this command.";
    private final FarmControl farmControl;

    private final ReloadCommand reloadCommand;
    private final StatusCommand statusCommand;

    public FarmControlCommand(FarmControl farmControl) {
        this.farmControl = farmControl;
        reloadCommand = new ReloadCommand(farmControl);
        statusCommand = new StatusCommand(farmControl);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender, s);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            if (sender.hasPermission("farmcontrol.command.reload")) {
                return reloadCommand.onCommand(sender, command, s, args);
            } else {
                sender.sendMessage(NO_PERMISSION_MESSAGE);
                return true;
            }
        }
        if ((args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("stats"))) {
            if (sender.hasPermission("farmcontrol.command.status")) {
                return statusCommand.onCommand(sender, command, s, args);
            } else {
                sender.sendMessage(NO_PERMISSION_MESSAGE);
                return true;
            }
        }
        sendHelp(sender, s);
        return true;
    }

    private void sendHelp(CommandSender sender, String cl) {
        sender.sendMessage(ChatColor.YELLOW + "FarmControl v" + farmControl.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Plugin page: " + ChatColor.WHITE + "https://www.spigotmc.org/resources/86923/");
        sender.sendMessage("");
        if (sender.hasPermission("farmcontrol.command.reload")) {
            sender.sendMessage("/" + cl + " reload");
        }
        if (sender.hasPermission("farmcontrol.command.status")) {
            sender.sendMessage("/" + cl + " status " + (sender instanceof Player ? "[world]" : "<world>"));
        }
    }

    public TabCompleter getTabCompleter() {
        return new TabCompleter() {
            @Override
            public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
                List<String> completions = new ArrayList<>();
                if (args.length == 1) {
                    if (sender.hasPermission("farmcontrol.command.reload")) {
                        completions.add("reload");
                        completions.add("rl");
                    }
                    if (sender.hasPermission("farmcontrol.command.status")) {
                        completions.add("status");
                        completions.add("stats");
                    }
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("stats")) {
                        if (sender.hasPermission("farmcontrol.command.status")) {
                            for (World world : Bukkit.getWorlds()) {
                                completions.add(world.getName());
                            }
                        }
                    }
                }
                return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
            }
        };
    }

}
