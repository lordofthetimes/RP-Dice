package net.lordofthetimes.rpDice;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.lordofthetimes.rpDice.utils.LogHelper;
import net.lordofthetimes.rpDice.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RpDice extends JavaPlugin {

    public final LogHelper logger = new LogHelper(this.getLogger());
    public YamlDocument config;
    public UpdateChecker updateChecker;

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

        updateChecker = new UpdateChecker(this,version);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
