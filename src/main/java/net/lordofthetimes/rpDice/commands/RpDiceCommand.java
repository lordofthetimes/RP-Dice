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

        String message = "<gold><bold>———===[ <#FFD54F>RP Dice</#FFD54F> ]===———</bold></gold>\n" +
                "<yellow><bold>Version: <white>" + plugin.getDescription().getVersion() + "</white></bold></yellow>\n" +
                "<yellow><bold>By: <white>" + String.join(", ", plugin.getDescription().getAuthors()) + "</white></bold></yellow>\n" +
                "<yellow><bold>Commands you have permission for:</bold></yellow>\n";

        if(sender.hasPermission("rpdice.reload")){
            message += "<green><bold>/rpdice reload</bold></green>\n";
        }
        if(sender.hasPermission("rpdice.help")){
            message += "<green><bold>/rpdice help</bold></green>\n";
        }
        if(sender.hasPermission("rpdice.roll")){
            message += "<green><bold>/roll <preset-dice></bold></green>\n";
        }
        if(sender.hasPermission("rpdice.roll.custom")){
            message += "<green><bold>/roll custom <diceName></bold></green>\n";
        }
        message += "<gold><bold>————=====================————</bold></gold>";

        MessageSender.sendMessage(sender,message);

    }

    private void reload(CommandSender sender){
        if(sender instanceof Player player) if(plugin.cooldown.isOnCooldown(player)) return;
        try{
            plugin.reload();
            MessageSender.sendMessage(sender, "<green>Plugin reloaded successfully!</green>");
        }
        catch (IOException error){
            MessageSender.sendMessage(sender, "<red>Failed to reload the config! " + error + "</red>");
        }
    }
}
