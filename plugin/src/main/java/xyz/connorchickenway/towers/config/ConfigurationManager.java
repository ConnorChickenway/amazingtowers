package xyz.connorchickenway.towers.config;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.lang.Lang;
import xyz.connorchickenway.towers.utilities.ManagerController;

public class ConfigurationManager extends ManagerController {

    private static Configuration[] configurations = new Configuration[ConfigName.values().length];

    public ConfigurationManager() {
        super(AmazingTowers.getInstance());
    }

    @Override
    public void load() {
        plugin.saveDefaultConfig();
        for (ConfigName configName : ConfigName.values())
            configurations[configName.ordinal()] = new Configuration(configName.getName(),
                    plugin.getDataFolder().getAbsolutePath());
        StaticConfiguration.load();
        StaticConfiguration.loadGameMode();
        Lang.loadMessages();
    }

    @Override
    public void disable() {
        for (Configuration configuration : configurations)
            configuration.saveConfiguration();
    }

    public static enum ConfigName {

        SCOREBOARD, LANG;

        public String getName() {
            return toString().toLowerCase();
        }

        public Configuration getConfiguration() {
            return configurations[ordinal()];
        }

        public static ConfigName fromString(String text) {
            for (ConfigName configName : values()) {
                if (configName.getName().equals(text.toLowerCase()))
                    return configName;
            }
            return null;
        }

        public static String names() {
            StringBuilder builder = new StringBuilder();
            for (ConfigName configName : values())
                builder.append(configName.getName())
                        .append(",");
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }

    }

}
