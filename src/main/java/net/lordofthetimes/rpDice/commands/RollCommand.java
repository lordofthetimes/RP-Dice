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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RollCommand {

    private final YamlDocument config;
    private final CommandCooldown cooldown;
    private final RpDice plugin;

    public RollCommand(RpDice plugin){
        this.config = plugin.config;
        this.cooldown = plugin.cooldown;
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
        ).register();
    }

    private void rollCustom(CommandSender sender, String name){

        if(!name.matches("^\\d+d\\d+$")){
            MessageSender.sendMessage(sender,"<red>Wrong format. Correct format is <number>d<number> e.g. 1d20, 3d4, 12d2");
            return;
        }

        int dIndex = name.indexOf('d');

        int dices = Integer.parseInt(name.substring(0, dIndex));
        int max = Integer.parseInt(name.substring(dIndex + 1));

        int min = 1;


        int diceLimit = config.getInt("customMaxDice");
        int maxLimit = config.getInt("customMax");

        if(dices > diceLimit || max > maxLimit){
            MessageSender.sendMessage(sender,"<red>Over the limit. Maximum amount of dices is " + diceLimit + " and maximum roll is " + maxLimit + " !</red>");
            return;
        }

        roll(sender,dices,min,max,name);
    }

    private void roll(CommandSender sender, int dices, int min, int max, String name){

        if(!(sender instanceof Player player)){
            MessageSender.sendMessage(sender,"<yellow>This command can only be used by a player!</yellow>");
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
            MessageSender.sendMessage(sender,"<yellow>This command can only be used by a player!</yellow>");
            return;
        }

        String message = "<yellow>USAGE: \n/" +
                "roll <preset-dice> - rolls a preset dice from the config\n" +
                "/roll custom <diceName> - rolls custom dice. Format : <number>d<number> e.g. 1d20, 3d4, 12d2";
        MessageSender.sendMessage(player,message);
    }

}
