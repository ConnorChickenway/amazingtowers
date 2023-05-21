package xyz.connorchickenway.towers.game.runnable;

import org.bukkit.scheduler.BukkitRunnable;

import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.runnable.util.TaskId;

public abstract class GameRunnable extends BukkitRunnable
{
    
    private final TaskId taskId;
    private final long period;

    public GameRunnable( Game game, TaskId taskId, long period )
    {
        this.taskId = taskId;
        this.period = period;
        this.startTimer();
    }

    public TaskId getTaskID()
    {
        return taskId;
    }

    public abstract void doStuff();

    public void startTimer()
    {
        if ( this.isCancelled() )
            this.runTaskTimer( AmazingTowers.getInstance(), 0L, period );
    }

}
