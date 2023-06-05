package xyz.connorchickenway.towers.game.scoreboard.manager;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.config.ConfigurationManager.ConfigName;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.scoreboard.score.Line;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.utilities.ManagerController;
import xyz.connorchickenway.towers.utilities.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ScoreboardManager extends ManagerController
{

    private Map<GameState, List<Line>> linesMap;
    private String title, 
                date;

    public ScoreboardManager( AmazingTowers plugin )
    {
        super( plugin );
        this.linesMap = Maps.newHashMap();
    }

    @Override
    public void load()
    {
        FileConfiguration fc = ConfigName.SCOREBOARD.getConfiguration().getFileConfiguration();
        title = StringUtils.color( fc.getString( "title" , "&e&lTHETOWERS" ) );
        date = new SimpleDateFormat( fc.getString( "pattern", "dd/MM/yy" ) ).format( Calendar.getInstance().getTime() );
        //LINES
        GameState[] states = GameState.values();
        for( int i = 0; i < 3; i++ )
        {
            List<Line> list = Lists.newArrayList();
            GameState state = states[ i ];
            for ( String line : fc.getStringList( "scores." + state.name().toLowerCase() ) )
                list.add( new Line( line ) );
            linesMap.put( state , list );
        }
    }

    @Override
    public void disable()
    {
        throw new UnsupportedOperationException("Unimplemented method 'disable'");  

    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public List<Line> getLines( GameState state )
    {
        return linesMap.get( state );
    }

    public List<String> getLines( GamePlayer gPlayer, GameState state )
    {
        List<String> l = Lists.newArrayList();
        for ( Line line : getLines( state ) )  
            l.add( line.hasPlaceholders() ? line.getLine( gPlayer ) : line.getLine() );
        return l;    
    }

}
