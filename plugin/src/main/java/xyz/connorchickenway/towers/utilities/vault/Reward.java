package xyz.connorchickenway.towers.utilities.vault;

import xyz.connorchickenway.towers.config.StaticConfiguration;

public enum Reward {

    KILL, WIN, POINT, BY_PLAYING;

    public int getValue() {
        switch (ordinal()) {
            case 0:
                return StaticConfiguration.kill_value;
            case 1:
                return StaticConfiguration.win_value;
            case 2:
                return StaticConfiguration.point_value;
            case 3:
                return StaticConfiguration.by_playing_value;
        }
        return 0;
    }

}
