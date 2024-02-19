package xyz.connorchickenway.towers.game.scoreboard.score;

import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.scoreboard.placeholder.PlaceholderKey;
import xyz.connorchickenway.towers.utilities.StringUtils;

import java.util.List;

public class Line {

    private String text;
    private List<PlaceholderKey> keys;

    public Line(String text) {
        this.text = text != null ? StringUtils.color(text) : "";
        this.keys = PlaceholderKey.search(this.text);
    }

    public String getLine() {
        return text;
    }

    public String getLine(GamePlayer gPlayer) {
        String text = this.text;
        for (PlaceholderKey key : keys)
            text = text.replaceAll(key.toPlaceholder(), key.perform(gPlayer));
        return text;
    }

    public boolean hasPlaceholders() {
        return keys != null;
    }

}
