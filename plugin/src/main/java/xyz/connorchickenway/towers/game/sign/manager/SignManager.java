package xyz.connorchickenway.towers.game.sign.manager;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.StaticConfiguration;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.manager.GameManager;
import xyz.connorchickenway.towers.game.sign.GameSign;
import xyz.connorchickenway.towers.nms.NMSManager;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.location.Location;

import java.util.Set;

import static xyz.connorchickenway.towers.utilities.StringUtils.color;

public class SignManager extends ManagerController implements Listener {

    private final Set<GameSign> signs = Sets.newHashSet();
    private AttachedManager attachedManager;

    public SignManager(AmazingTowers plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.attachedManager = new AttachedManager(plugin);
        this.attachedManager.load();
    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Location spawnLocation = StaticConfiguration.spawn_location;
        if (spawnLocation != null) {
            Block block = event.getClickedBlock();
            if (block.getWorld() != spawnLocation.getWorld()) return;
            if (!isWallSign(block)) return;
            Player player = event.getPlayer();
            GameSign gameSign = get(block.getLocation());
            if (gameSign != null)
                gameSign.click(plugin.getEntityManager().get(player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Location spawnLocation = StaticConfiguration.spawn_location;
        if (spawnLocation != null) {
            Block block = event.getBlock();
            if (!block.getWorld().equals(spawnLocation.getWorld())) return;
            Player player = event.getPlayer();
            if (!player.isOp()) {
                event.setCancelled(true);
                return;
            }
            String message = color("&cYou have broken an arena sign that belongs to ");
            if (this.isWallSign(block)) {
                GameSign gameSign = get(block.getLocation());
                if (gameSign != null) {
                    player.sendMessage(message + gameSign.getGame().getGameName());
                    gameSign.getGame().setGameSign(null);
                    gameSign.delete();
                    signs.remove(gameSign);
                }
            } else {
                final BlockFace[] faces = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
                for (BlockFace f : faces) {
                    Block b = block.getRelative(f);
                    if (isWallSign(b)) {
                        GameSign gameSign = get(b.getLocation());
                        if (gameSign != null) {
                            player.sendMessage(message + gameSign.getGame().getGameName());
                            gameSign.getGame().setGameSign(null);
                            gameSign.delete();
                            signs.remove(gameSign);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp() || !player.hasPermission("towers.admin")) {
            player.sendMessage(color("&cYou have no permission to do that!"));
            event.getBlock().breakNaturally();
            return;
        }
        String line = event.getLine(0);
        if (line.equalsIgnoreCase("[AmazingTowers]")) {
            final Block block = event.getBlock();
            Location spawnWorld = StaticConfiguration.spawn_location;
            if (spawnWorld == null) {
                player.sendMessage(color("&cThere's no spawn location!. /towers setspawn"));
                return;
            }
            if (!block.getWorld().equals(spawnWorld.getWorld())) {
                block.breakNaturally();
                return;
            }
            line = event.getLine(1);
            String message = "";
            boolean breakNaturally = true;

            if (line.isEmpty())
                message = color("&cYou need to put a game name!");
            else if (!isWallSign(block))
                message = color("&cYou can only use a wallsign to do that action!");
            else if (hasSign(line))
                message = color("&cA sign has that game name already!");
            else {
                Game game = GameManager.get().getGame(line);
                if (game != null) {
                    breakNaturally = false;
                    message = color("&7You've created a sign for the game &a" + game.getGameName() + "&7!");
                    GameSign gameSign = new GameSign(game, block.getLocation());
                    game.setGameSign(gameSign);
                    signs.add(gameSign);
                    Bukkit.getScheduler().runTaskLater(plugin, gameSign::update, 5L);
                    gameSign.save();
                } else
                    message = color("&cThe arena &7" + line + " &cdoes not exist!");
            }
            if (breakNaturally)
                block.breakNaturally();
            event.getPlayer().sendMessage(message);
        }
    }

    private boolean hasSign(String name) {
        for (GameSign sign : signs)
            if (sign.getGame().getGameName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public GameSign get(org.bukkit.Location location) {
        for (GameSign gSign : signs)
            if (gSign.getSignLocation().equals(location))
                return gSign;
        return null;
    }

    public void add(GameSign gameSign) {
        signs.add(gameSign);
    }

    public void remove(GameSign gameSign) {
        signs.remove(gameSign);
    }

    private boolean isWallSign(Block block) {
        return NMSManager.get().getBlockUtilities().isWallSign(block);
    }

    public AttachedManager getAttachedManager() {
        return attachedManager;
    }

}
