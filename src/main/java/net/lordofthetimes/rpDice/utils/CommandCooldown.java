package net.lordofthetimes.rpDice.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.lordofthetimes.rpDice.RpDice;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CommandCooldown {

    private final YamlDocument config;
    private final MessageSender msg;

    public CommandCooldown(RpDice plugin){
        this.config = plugin.config;
        this.msg = plugin.msg;
    }

    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();


    public Boolean isOnCooldown(Player player){
        if(player.hasPermission("rpdice.bypass-cooldown")) return false;

        UUID uuid = player.getUniqueId();
        long timeLeft;

        if(cooldown.containsKey(uuid)) {
            timeLeft = System.currentTimeMillis() - cooldown.get(uuid);
        }
        else{
            timeLeft = System.currentTimeMillis();
        }

        if(timeLeft < config.getInt("commandCooldown")){
            msg.sendCooldown(player,timeLeft);
            return true;
        }
        cooldown.put(player.getUniqueId(),System.currentTimeMillis());
        return false;
    }

}
