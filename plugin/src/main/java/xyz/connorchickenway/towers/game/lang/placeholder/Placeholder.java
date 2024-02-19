package xyz.connorchickenway.towers.game.lang.placeholder;

import org.bukkit.entity.Player;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.lang.Lang;
import xyz.connorchickenway.towers.utilities.Pair;
import xyz.connorchickenway.towers.utilities.StringUtils;
import xyz.connorchickenway.towers.utilities.vault.VaultManager;

import java.util.HashMap;
import java.util.Map;

public interface Placeholder {

    String getKey();

    @SafeVarargs
    public static Map<String, String> builder(Pair<String, String>... placeholders) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < placeholders.length; i++) {
            map.put("%" + placeholders[i].getKey() + "%", placeholders[i].getValue());
        }
        return map;
    }

    public static Pair<String, String> pair(Placeholder placeholder, Object... values) {
        String value = (placeholder instanceof OneArgumentPlaceholder ?
                ((OneArgumentPlaceholder) placeholder).perform(values[0]) :
                ((TwoArgumentPlaceholder) placeholder).perform(values[0], values.length >= 2 ? values[1] : null));
        return instance(placeholder.getKey(), value);
    }

    static Pair<String, String> instance(String key, String value) {
        return new Pair<String, String>(key, value);
    }

    public static final OneArgumentPlaceholder COLOR_TEAM = newInstance("color_team"),
            COUNT = newInstance("count"), ONLINE_PLAYERS = newInstance("online_players"),
            MAX_PLAYERS = newInstance("max_players"), MESSAGE = newInstance("msg"),
            TEAM_NAME = newInstance("team_name");

    public static final OneArgumentPlaceholder SECONDS = new OneArgumentPlaceholder() {

        @Override
        public String getKey() {
            return "seconds";
        }

        @Override
        public String perform(Object obj) {
            String[] split = Lang.SECONDS.get()[0].split("::");
            return ((Integer) obj) <= 0 ? split[0] : split[1];
        }

    };

    public static final OneArgumentPlaceholder DISTANCE = new OneArgumentPlaceholder() {

        @Override
        public String getKey() {
            return "distance";
        }

        @Override
        public String perform(Object obj) {
            Player player = (Player) obj;
            double distance = player.getLocation().distanceSquared(player.getKiller().getLocation());
            return Long.toString(Math.round(Math.sqrt(distance)));
        }

    };

    public static final OneArgumentPlaceholder PREFIX = new OneArgumentPlaceholder() {

        private final VaultManager vaultManager = AmazingTowers.getInstance().getVaultManager();

        @Override
        public String perform(Object obj) {
            final String prefix = StringUtils.color(vaultManager.getChat().getPlayerPrefix((Player) obj));
            return vaultManager.hasChat() ? (StringUtils.isBlank(prefix) ? "" : prefix) : "";
        }

        @Override
        public String getKey() {
            return "prefix";
        }

    };

    public static final TwoArgumentPlaceholder KILLER_NAME = new TwoArgumentPlaceholder() {

        @Override
        public String getKey() {
            return "killer_name";
        }

        @Override
        public String perform(Object playerObj, Object chatColorObj) {
            return String.valueOf(chatColorObj) + ((Player) playerObj).getName();
        }

    },
            PLAYER_NAME = new TwoArgumentPlaceholder() {

                @Override
                public String getKey() {
                    return "player_name";
                }

                @Override
                public String perform(Object firstObj, Object secondObj) {
                    return (secondObj != null ? String.valueOf(secondObj) : "") + ((Player) firstObj).getName();
                }

            };

    static OneArgumentPlaceholder newInstance(String key) {
        return new OneArgumentPlaceholder() {

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String perform(Object obj) {
                return String.valueOf(obj);
            }

        };
    }

}
