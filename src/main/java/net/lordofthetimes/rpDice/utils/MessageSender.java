package net.lordofthetimes.rpDice.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.lordofthetimes.rpDice.RpDice;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MessageSender {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final YamlDocument messages;
    private final RpDice plugin;

    public MessageSender(RpDice plugin){
        this.messages = plugin.messages;
        this.plugin = plugin;
    }


    public void sendMessage(CommandSender sender, String code){
        if(messages.contains(code)){
            sender.sendMessage(deserialize(messages.getString("msg-prefix") + messages.getString(code)));
            return;
        }
        sender.sendMessage(deserialize(messages.getString("msg-prefix") + code));
    }

    public <T> void sendMessage(CommandSender sender, String code, Map<String,T> params){
        AtomicReference<String> message = new AtomicReference<>(messages.getString(code));

        params.forEach((key,value) -> {
            message.set(message.get().replace(key,value.toString()));
        });
        sendMessage(sender, message.get());
    }

    public void sendHelp(CommandSender sender){
        String message = "<gold><bold>———===[ <#FFD54F>RP Dice</#FFD54F> ]===———</bold></gold>\n" +
                "<yellow><bold>Version: <white>" + plugin.getDescription().getVersion() + "</white></bold></yellow>\n" +
                "<yellow><bold>By: <white>" + String.join(", ", plugin.getDescription().getAuthors()) + "</white></bold></yellow>\n" +
                messages.getString("msg-help-top");

        if(sender.hasPermission("rpdice.reload")){
            message += messages.getString("msg-help-reload");
        }
        if(sender.hasPermission("rpdice.help")){
            message += messages.getString("msg-help-help");
        }
        if(sender.hasPermission("rpdice.roll")){
            message += messages.getString("msg-help-roll");
        }
        if(sender.hasPermission("rpdice.roll.custom")){
            message += messages.getString("msg-help-custom");
        }
        if(sender.hasPermission("rpdice.roll.action")){
            message += messages.getString("msg-help-action");
        }
        message += "<gold><bold>————=====================————</bold></gold>";

        sendMessage(sender,message);
    }

    public void sendCooldown(CommandSender sender, long time){
        sendMessage(sender,messages.getString("msg-cooldown").replace("<%time%>",String.valueOf (time/1000f)));
    }

    public static Component deserialize(String input) {
        return miniMessage.deserialize(input);
    }
}
