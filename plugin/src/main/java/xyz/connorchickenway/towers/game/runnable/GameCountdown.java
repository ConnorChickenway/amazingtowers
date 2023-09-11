package xyz.connorchickenway.towers.game.runnable;

import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.runnable.util.TaskId;

public abstract class GameCountdown extends GameRunnable
{

    private int seconds;

    public GameCountdown( Game game, TaskId taskId, int seconds )
    {
        super( game, taskId, 20 );
        this.seconds = seconds;
    }

    @Override
    public void run()
    {
        if ( seconds == 0 )
        {
            doStuff();
            cancel();
        } else if ( seconds % 10 == 0 || seconds <= 5 ) broadcast();
        doPerSecond();
        --seconds;
    }

    public abstract void broadcast();

    public abstract void doPerSecond();

    public int getSeconds()
    {
        return seconds;
    }

    public void setSeconds( int seconds )
    {
        this.seconds = seconds;
    } 

}
