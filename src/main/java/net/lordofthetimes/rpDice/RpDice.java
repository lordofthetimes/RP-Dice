package net.lordofthetimes.rpDice;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import net.lordofthetimes.rpDice.commands.RollCommand;
import net.lordofthetimes.rpDice.commands.RpDiceCommand;
import net.lordofthetimes.rpDice.listeners.PlayerJoinListener;
import net.lordofthetimes.rpDice.utils.CommandCooldown;
import net.lordofthetimes.rpDice.utils.LogHelper;
import net.lordofthetimes.rpDice.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class RpDice extends JavaPlugin {

    public final LogHelper logger = new LogHelper(this.getLogger());
    public YamlDocument config;
    public UpdateChecker updateChecker;
    public CommandCooldown cooldown;

    public RollCommand rollCommand;
    public RpDiceCommand rpDiceCommand;

    private PlayerJoinListener playerJoinListener;

    public boolean papiEnabled = false;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIPaperConfig(this).verboseOutput(false));
    }

    @Override
    public void onEnable() {
        String version = this.getPluginMeta().getVersion();

        logger.logInfo("\n################################\n\n" +
                "▗▄▄▖ ▗▄▄▖     ▗▄▄▄  ▗▄▄▄▖ ▗▄▄▖▗▄▄▄▖ \n" +
                "▐▌ ▐▌▐▌ ▐▌    ▐▌  █   █  ▐▌   ▐▌    \n"+
                "▐▛▀▚▖▐▛▀▘     ▐▌  █   █  ▐▌   ▐▛▀▀▘ \n"+
                "▐▌ ▐▌▐▌       ▐▙▄▄▀ ▗▄█▄▖▝▚▄▄▖▐▙▄▄▖ \n" +
                "RP Dice v" + version + " - Enjoy your roleplay!\n\n" +
                "################################");
        try {
            File configFile = new File(getDataFolder(), "config.yml");

            config = YamlDocument.create(
                    configFile,
                    this.getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.DEFAULT,
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
                            .build()
            );
            config.save();
        } catch (Exception e) {
            logger.logError("Failed to load or update config! Plugin is being disabled ",e);
            onDisable();
        }
        logger.logInfo("Config loaded successfully!");

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && config.getBoolean("papi.enabled")) {
            getLogger().info("PAPI detected!");
            papiEnabled = true;
        }

        updateChecker = new UpdateChecker(this,version);
        cooldown = new CommandCooldown(config);

        rollCommand = new RollCommand(this);
        rpDiceCommand = new RpDiceCommand(this);

        playerJoinListener = new PlayerJoinListener(this);
        getServer().getPluginManager().registerEvents(playerJoinListener,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() throws IOException {
        config.reload();
    }
}
