package xyz.connorchickenway.towers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinderwolf.swm.api.SlimePlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.connorchickenway.towers.cmd.CommandManager;
import xyz.connorchickenway.towers.config.ConfigurationManager;
import xyz.connorchickenway.towers.game.builder.adapters.CuboidAdapter;
import xyz.connorchickenway.towers.game.builder.adapters.ItemStackAdapter;
import xyz.connorchickenway.towers.game.builder.adapters.LocationAdapter;
import xyz.connorchickenway.towers.game.builder.setup.SetupListener;
import xyz.connorchickenway.towers.game.entity.manager.EntityManager;
import xyz.connorchickenway.towers.game.manager.GameManager;
import xyz.connorchickenway.towers.game.scoreboard.manager.ScoreboardManager;
import xyz.connorchickenway.towers.nms.NMSManager;
import xyz.connorchickenway.towers.utilities.Cuboid;
import xyz.connorchickenway.towers.utilities.GameMode;
import xyz.connorchickenway.towers.utilities.Logger;
import xyz.connorchickenway.towers.utilities.location.Location;
import xyz.connorchickenway.towers.utilities.vault.VaultManager;

public class AmazingTowers extends JavaPlugin {

    private ConfigurationManager configurationManager;
    private VaultManager vaultManager;
    private CommandManager commandManager;
    private NMSManager nmsManager;
    private EntityManager entityManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        this.configurationManager = new ConfigurationManager();
        this.configurationManager.load();
        if (GameMode.isBungeeMode())
            if (checkIfBungee())
                getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.vaultManager = new VaultManager(this);
        this.vaultManager.load();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SetupListener(), this);
        if (pm.isPluginEnabled("SlimeWorldManager")) {
            SLIME_PLUGIN = (SlimePlugin) pm.getPlugin("SlimeWorldManager");
        }
        this.commandManager = new CommandManager(this);
        this.nmsManager = new NMSManager(this);
        this.entityManager = new EntityManager(this);
        this.gameManager = new GameManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.commandManager.load();
        this.nmsManager.load();
        this.entityManager.load();
        this.gameManager.load();
        this.scoreboardManager.load();
    }

    @Override
    public void onDisable() {
        this.configurationManager.disable();
        this.commandManager.disable();
        this.nmsManager.disable();
        this.entityManager.disable();
    }

    private boolean checkIfBungee() {
        if (!getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("bungeecord")) {
            Logger.severe("This server is not BungeeCord.");
            Logger.severe("If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell.");
            return false;
        }
        return true;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public NMSManager getNMSManager() {
        return nmsManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public static AmazingTowers getInstance() {
        return instance;
    }

    private static AmazingTowers instance;

    public static Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(Cuboid.class, new CuboidAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    public static SlimePlugin SLIME_PLUGIN;

}
