package xyz.connorchickenway.towers.game.sign;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.sign.manager.AttachedManager;
import xyz.connorchickenway.towers.game.sign.manager.AttachedManager.AttachedBlock;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.nms.NMSManager;
import xyz.connorchickenway.towers.utilities.StringUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.connorchickenway.towers.config.StaticConfiguration.sign_lines;

public class GameSign {

    private final Game game;
    private final Location signLocation;
    private final Block attachedBlock;

    public GameSign(Game game, Location signLocation) {
        this.game = game;
        this.signLocation = signLocation;
        this.attachedBlock = NMSManager.get().getBlockUtilities().getAttachedBlock(signLocation.getBlock());
        this.game.setGameSign(this);
    }

    public void updateLine(int index, String text) {
        Sign sign = (Sign) signLocation.getBlock().getState();
        sign.setLine(index, StringUtils.color(text
                .replace("%status%", getStatus(game.getState()))
                .replace("%map%", game.getGameName())
                .replace("%online_players%", game.getOnlinePlayers() + "")
                .replace("%max_players%", game.getMaxPlayers() + "")));
        sign.update();
    }

    public void update() {
        AtomicInteger aInteger = new AtomicInteger(0);
        sign_lines.forEach(line -> updateLine(aInteger.getAndIncrement(), line));
        this.updateAttachedBlock();
    }

    public void updateAttachedBlock() {
        AttachedBlock aBlock = AttachedManager.get().getAttachedBlock(game.getState());
        if (aBlock != null)
            aBlock.setType(attachedBlock);
    }

    public void click(GamePlayer gPlayer) {
        if (gPlayer.getGame() != null) {
            return;
        }
        game.join(gPlayer);
    }

    public void save() {
        try {
            File file = new File(game.getFolder(), game.getGameName() + ".sign");
            if (!file.exists())
                file.createNewFile();
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
            outputStream.writeInt(signLocation.getBlockX());
            outputStream.writeInt(signLocation.getBlockY());
            outputStream.writeInt(signLocation.getBlockZ());
            outputStream.flush();
            outputStream.close();
        } catch (Exception ignored) {

        }
    }

    public void delete() {
        File file = new File(game.getFolder(), game.getGameName() + ".sign");
        if (file.exists())
            file.delete();
    }

    public Block getAttachedBlock() {
        return this.attachedBlock;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public Game getGame() {
        return game;
    }

    private static String getStatus(GameState state) {
        switch (state) {
            default:
            case LOBBY:
                return StaticConfiguration.lobby_status;
            case STARTING:
                return StaticConfiguration.starting_status;
            case GAME:
                return StaticConfiguration.game_status;
            case FINISH:
                return StaticConfiguration.finish_status;
            case RELOADING:
                return StaticConfiguration.reloading_status;

        }
    }

}
