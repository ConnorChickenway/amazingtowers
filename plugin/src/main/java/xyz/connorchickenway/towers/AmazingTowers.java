package xyz.connorchickenway.towers;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.connorchickenway.towers.cmd.CommandManager;
import xyz.connorchickenway.towers.config.ConfigurationManager;
import xyz.connorchickenway.towers.game.entity.manager.EntityManager;
import xyz.connorchickenway.towers.game.manager.GameManager;
import xyz.connorchickenway.towers.game.scoreboard.manager.ScoreboardManager;
import xyz.connorchickenway.towers.nms.NMSManager;

public class AmazingTowers extends JavaPlugin
{

    private ConfigurationManager configurationManager;
    private CommandManager commandManager;
    private NMSManager nmsManager;
    private EntityManager entityManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable()
    {
        instance = this;
        this.configurationManager = new ConfigurationManager();
        this.commandManager = new CommandManager( this );
        this.nmsManager = new NMSManager( this );
        this.entityManager = new EntityManager( this );
        this.gameManager = new GameManager( this );
        this.scoreboardManager = new ScoreboardManager( this );
        this.configurationManager.load();
        this.commandManager.load();
        this.nmsManager.load();
        this.entityManager.load();
        this.gameManager.load();
        this.scoreboardManager.load();
    }

    @Override
    public void onDisable()
    {
        this.configurationManager.disable();
        this.commandManager.disable();
        this.nmsManager.disable();
        this.entityManager.disable();
    }

    public ConfigurationManager getConfigurationManager()
    {
        return configurationManager;
    }

    public NMSManager getNMSManager()
    {
        return nmsManager;
    }
    
    public EntityManager getEntityManager()
    {
        return entityManager;
    }
    
    public GameManager getGameManager()
    {
        return gameManager;
    }
    
    public ScoreboardManager getScoreboardManager()
    {
        return scoreboardManager;
    }

    public static AmazingTowers getInstance()
    {
        return instance;
    }

    private static AmazingTowers instance;


}
