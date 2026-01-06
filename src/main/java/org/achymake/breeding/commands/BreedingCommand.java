package org.achymake.breeding.commands;

import org.achymake.breeding.Breeding;
import org.achymake.breeding.data.Message;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BreedingCommand implements CommandExecutor, TabCompleter {
    private Breeding getInstance() {
        return Breeding.getInstance();
    }
    private Message getMessage() {
        return getInstance().getMessage();
    }
    public BreedingCommand() {
        getInstance().getCommand("breeding").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getInstance().reload();
                    player.sendMessage(getMessage().addColor("&6" + getInstance().name() + "&f: reloaded"));
                    return true;
                }
            }
        } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getInstance().reload();
                    consoleCommandSender.sendMessage(getInstance().name() + ": reloaded");
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        var commands = new ArrayList<String>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("reload");
            }
        }
        return commands;
    }
}