package net.lordofthetimes.rpDice.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.lordofthetimes.rpDice.RpDice;
import net.lordofthetimes.rpDice.utils.CommandCooldown;
import net.lordofthetimes.rpDice.utils.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RollCommand {

    private final YamlDocument config;
    private final CommandCooldown cooldown;
    private final MessageSender msg;
    private final RpDice plugin;

    public RollCommand(RpDice plugin){
        this.config = plugin.config;
        this.cooldown = plugin.cooldown;
        this.msg = plugin.msg;
        this.plugin = plugin;

        CommandAPICommand rollCommand = new CommandAPICommand("roll")
                .withPermission("rpdice.roll")
                .executes((sender,arg) -> {
                    help(sender);
                });

        Section allDices =  config.getSection("dices");

        allDices.getKeys().forEach((objKey) -> {
            String key = objKey.toString();
            int dices = allDices.getInt(key + ".dices");
            int min = allDices.getInt(key + ".min");
            int max = allDices.getInt(key + ".max");
            rollCommand.withSubcommand(new CommandAPICommand(key)
                    .withPermission("rpdice.roll")
                    .executes((sender,args) -> {
                        roll(sender,dices,min,max, key);
                    })
            );
        });

        rollCommand.withSubcommand(new CommandAPICommand("custom")
                .withPermission("rpdice.roll.custom")
                .withArguments(new StringArgument("diceName"))
                .executes((sender,args) -> {
                    rollCustom(sender, args.get("diceName").toString());
                })
        );
        rollCommand.withSubcommand(new CommandAPICommand("action")
                .withPermission("rpdice.roll.action")
                .withArguments(new GreedyStringArgument("action"))
                .executes((sender,args) -> {
                    rollAction(sender, args.get("action").toString());
                })
        ).register();
    }

    private void rollAction(CommandSender sender, String action){
        if(!(sender instanceof Player player)){
            msg.sendMessage(sender,"msg-only-player");
            return;
        }

        Random rn = new Random();

        List<String> options = config.getStringList("action.results");
        String result = options.get(rn.nextInt(options.size() + 1));

        String message = config.getString("action.resultMessage");
        message = message.replace("<%username%>",player.getName());
        message = message.replace("<%action%>",action);
        message = message.replace("<%result%>",result);

        if(plugin.papiEnabled){
            message = PlaceholderAPI.setPlaceholders(player,message);
        }

        Bukkit.getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(message));
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
            if(withinDistance(onlinePlayer,player)){
                onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        }
    }

    private void rollCustom(CommandSender sender, String name){

        if(!(sender instanceof Player player)){
            msg.sendMessage(sender,"msg-only-player");
            return;
        }

        if(!name.matches("^\\d+d\\d+$")){
            msg.sendMessage(sender,"msg-roll-wrong-format");
            return;
        }

        int dIndex = name.indexOf('d');

        int dices = Integer.parseInt(name.substring(0, dIndex));
        int max = Integer.parseInt(name.substring(dIndex + 1));

        int min = 1;


        int diceLimit = config.getInt("customMaxDice");
        int maxLimit = config.getInt("customMax");


        if(dices > diceLimit || max > maxLimit){
            msg.sendMessage(sender,"msg-custom-limit",
                    Map.of("<%diceLimit%>" , String.valueOf(diceLimit) ,
                            "<%maxLimit%>", String.valueOf(maxLimit)));
            return;
        }

        roll(sender,dices,min,max,name);
    }

    private void roll(CommandSender sender, int dices, int min, int max, String name){

        if(!(sender instanceof Player player)){
            msg.sendMessage(sender,"msg-only-player");
            return;
        }

        if(cooldown.isOnCooldown(player)) return;

        List<Integer> results = new ArrayList<>();
        Random rn = new Random();

        for(int i = 0; i < dices; i++){
            results.add(rn.nextInt(max - min + 1) + 1);
        }

        sendRoll(player,results, name);
    }

    private void sendRoll(Player player, List<Integer> results, String name){
        String message = config.getString("resultMessage");

        int total = 0;
        String allResults = "";

        for (Integer result : results) {
            allResults += result + ", ";
            total += result;
        }
        allResults = allResults.substring(0, allResults.length() - 2);

        message = message.replace("<%username%>",player.getName());
        message = message.replace("<%diceName%>",name);
        message = message.replace("<%rollList%>",allResults);
        message = message.replace("<%total%>", String.valueOf(total));

        if(plugin.papiEnabled){
            message = PlaceholderAPI.setPlaceholders(player,message);
        }

        Bukkit.getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(message));
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
            if(withinDistance(onlinePlayer,player)){
                onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        }

    }

    private boolean withinDistance(Player p1, Player p2) {

        int distance = config.getInt("resultDistance");

        if (!p1.getWorld().equals(p2.getWorld())) return false;

        return p1.getLocation().distanceSquared(p2.getLocation()) <= distance * distance;
    }

    private void help(CommandSender sender){

        if(!(sender instanceof Player player)){
            msg.sendMessage(sender,"msg-only-player");
            return;
        }

        msg.sendMessage(player,"msg-roll-usage");
    }

}
