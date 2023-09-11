package xyz.connorchickenway.towers.game.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.entity.GamePlayer;
import xyz.connorchickenway.towers.game.scoreboard.manager.ScoreboardManager;
import xyz.connorchickenway.towers.game.scoreboard.score.Line;
import xyz.connorchickenway.towers.game.state.GameState;
import xyz.connorchickenway.towers.utilities.scoreboard.FastBoard;

public class GameScoreboard 
{

    private Map<GamePlayer, FastBoard> scoreMap = Maps.newConcurrentMap();

    public void add( GamePlayer gPlayer )
    {
        FastBoard fBoard = new FastBoard( gPlayer.toBukkitPlayer() );
        scoreMap.put( gPlayer , fBoard );
        fBoard.updateTitle( scoreboardManager.getTitle() );
        fBoard.updateLines( scoreboardManager.getLines( gPlayer , gPlayer.getGame().getState() ) );
    }

    public void remove( GamePlayer gPlayer )
    {
        FastBoard board = scoreMap.get( gPlayer );
        board.delete();
        scoreMap.remove( gPlayer );
    }

    public void update( GameState gameState )
    {
        List<Line> lines = scoreboardManager.getLines( gameState );
        scoreMap.forEach( (gPlayer, board) -> 
        {
            List<String> newLines = new ArrayList<>();
            for ( int i = 0; i < lines.size(); i++ ) 
            {
                Line line = lines.get( i );
                newLines.add( line.hasPlaceholders() ? line.getLine( gPlayer ) : line.getLine() );    
            }
            board.updateLines( newLines );
        } );
    }
    
    private static ScoreboardManager scoreboardManager = AmazingTowers.getInstance().getScoreboardManager();

}
