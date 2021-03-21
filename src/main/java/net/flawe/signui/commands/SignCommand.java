package net.flawe.signui.commands;

import net.flawe.signui.SignUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.flawe.signui.util.ColorUtil.*;

public class SignCommand implements CommandExecutor, TabCompleter {

    private final SignUI plugin;

    public SignCommand(SignUI signUI) {
        this.plugin = signUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("signui.use")) {
            sender.sendMessage(format("&7[&6SignUI&7] &fYou don't have permissions for use this command!"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(format("&7[&6SignUI&7] &fEnter sub command!"));
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload": {
                plugin.reloadConfig();
                sender.sendMessage(format("&7[&6SignUI&7] &fPlugin configuration success reloaded!"));
            }
            break;
            case "help": {
                sender.sendMessage(format(new String[]
                        {
                                "&7[&6SignUI&7] &fCommand list:",
                                "&7[&6SignUI&7] &6/sign &ereload"
                        }));
            }
            break;
            default:
                sender.sendMessage(format("&7[&6SignUI&7] &fThis command isn't found!"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("signui.use") && !sender.isOp())
            return null;
        if (args.length == 1)
            return Stream.of("reload", "help")
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        return null;
    }
}
