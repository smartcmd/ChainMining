package me.daoge.chainmining;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.io.File;

/**
 * ChainMining - A chain mining plugin for AllayMC.
 * When a player breaks a block in the chainable blocks list,
 * adjacent blocks of the same type will also be broken.
 *
 * @author daoge_cmd
 */
public class ChainMining extends Plugin {

    @Getter
    private static ChainMining instance;

    @Getter
    private ChainMiningConfig config;

    @Override
    public void onLoad() {
        instance = this;
        loadConfiguration();
    }

    @Override
    public void onEnable() {
        // Register event listener
        Server.getInstance().getEventBus().registerListener(new ChainMiningListener());

        // Register command
        Registries.COMMANDS.register(new ChainMiningCommand());

        pluginLogger.info("ChainMining plugin enabled!");
    }

    @Override
    public void onDisable() {
        saveConfiguration();
        pluginLogger.info("ChainMining plugin disabled!");
    }

    @Override
    public boolean isReloadable() {
        return true;
    }

    @Override
    public void reload() {
        loadConfiguration();
        pluginLogger.info("ChainMining configuration reloaded!");
    }

    /**
     * Load the configuration from file.
     */
    private void loadConfiguration() {
        File dataFolder = pluginContainer.dataFolder().toFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, "config.yml");
        config = ConfigManager.create(ChainMiningConfig.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(configFile);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    /**
     * Save the configuration to file.
     */
    public void saveConfiguration() {
        if (config != null) {
            config.save();
        }
    }
}
