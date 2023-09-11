package xyz.connorchickenway.towers.game.sign;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

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

public class GameSign 
{

    private final Game game;
    private Sign sign;
    private Block attachedBlock;

    public GameSign( Game game, Sign sign )
    {
        this.game = game;
        this.sign = sign;
        this.attachedBlock = NMSManager.get().getBlockUtilities().getAttachedBlock( sign.getBlock() );
        this.game.setGameSign( this );
    }

    public void updateLine( int index, String text )
    {
        sign.setLine( index , StringUtils.color( text
                .replace( "%status%", getStatus( game.getState() ) )
                .replace( "%map%", game.getGameName() )
				.replace( "%online_players%", game.getOnlinePlayers() + "" )
                .replace( "%max_players%", game.getMaxPlayers() + "" ) ) );
        sign.update();
    }

    public void update()
    {
        AtomicInteger aInteger = new AtomicInteger( 0 );
        StaticConfiguration.sign_lines.forEach( line ->
            updateLine( aInteger.getAndIncrement(), line ) );
        this.updateAttachedBlock();
    }

    public void updateAttachedBlock()
    {
        AttachedBlock aBlock = AttachedManager.get().getAttachedBlock( game.getState() );
        if ( aBlock != null )
            aBlock.setType( attachedBlock );
    }

    public void click( GamePlayer gPlayer )
    {
        if ( gPlayer.getGame() != null )
        {
            return;
        }
        game.join( gPlayer );
    }

    public void save()
    {
        try
        {
            File file = new File( game.getFolder(),  game.getGameName() + ".sign" );
            if ( !file.exists() )
                file.createNewFile();
            DataOutputStream outputStream = new DataOutputStream( new FileOutputStream( file ) );
            outputStream.writeInt( sign.getX() );
            outputStream.writeInt( sign.getY() );
            outputStream.writeInt( sign.getZ() );
            outputStream.flush();
            outputStream.close();
        } catch ( Exception e )
        {
            
        }
    }

    public void delete()
    {
        File file = new File( game.getFolder(),  game.getGameName() + ".sign" );
        if ( file.exists() )
            file.delete();
    }

    public Block getAttachedBlock()
    {
        return this.attachedBlock;
    }
    
    public Sign getSign()
    {
        return sign;
    }
    
    public Game getGame()
    {
        return game;
    }
    
    private static String getStatus( GameState state )
    {
        switch( state )
        {
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
            default:
                break;
        }
        return null;
    }

}
