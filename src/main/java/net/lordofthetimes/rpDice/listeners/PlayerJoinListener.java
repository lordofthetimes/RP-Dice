package net.lordofthetimes.rpDice.listeners;

import net.lordofthetimes.rpDice.RpDice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final RpDice plugin;

    public PlayerJoinListener(RpDice plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("rpdice.updateinfo")) {
            plugin.updateChecker.sendVersionPlayer(player);
        }
    }
}
