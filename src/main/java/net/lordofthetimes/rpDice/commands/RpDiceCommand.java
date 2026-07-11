package net.lordofthetimes.rpDice.commands;

import dev.jorel.commandapi.CommandAPICommand;
import net.lordofthetimes.rpDice.RpDice;
import net.lordofthetimes.rpDice.utils.MessageSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class RpDiceCommand {
    private final RpDice plugin;

    public RpDiceCommand(RpDice plugin){
        this.plugin = plugin;

        new CommandAPICommand("rpdice")
                .withPermission("rpdice")
                .executes((sender, args) -> {
                    help(sender);
                })
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("rpdice.reload")
                        .executes((sender, args) -> {
                            reload(sender);
                        })
                )
                .withSubcommand(new CommandAPICommand("help")
                        .withPermission("rpdice.help")
                        .executes((sender, args) -> {
                            help(sender);
                        })
                )
                .register();
    }

    private void help(CommandSender sender){
        if(sender instanceof Player player) if(plugin.cooldown.isOnCooldown(player)) return;

        plugin.msg.sendHelp(sender);

    }

    private void reload(CommandSender sender){
        if(sender instanceof Player player) if(plugin.cooldown.isOnCooldown(player)) return;
        try{
            plugin.reload();
            plugin.msg.sendMessage(sender, "msg-reload-success");
        }
        catch (IOException error){
            plugin.logger.logError("<red>Failed to reload the config!",error);
            plugin.msg.sendMessage(sender, "msg-reload-fail");
        }
    }
}
