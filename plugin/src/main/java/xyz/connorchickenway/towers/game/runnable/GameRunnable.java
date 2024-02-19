package xyz.connorchickenway.towers.game.runnable;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.connorchickenway.towers.AmazingTowers;
import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.runnable.util.TaskId;

public abstract class GameRunnable implements Runnable {

    private final TaskId taskId;
    private final long period;
    private BukkitTask bukkitTask;

    public GameRunnable(Game game, TaskId taskId, long period, boolean async) {
        this.taskId = taskId;
        this.period = period;
        this.startTimer(async);
    }

    public GameRunnable(Game game, TaskId taskId, long period) {
        this(game, taskId, period, false);
    }

    public TaskId getTaskID() {
        return taskId;
    }

    public abstract void doStuff();

    public void startTimer(boolean async) {
        if (bukkitTask == null)
            if (async)
                bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(AmazingTowers.getInstance(), this, 0L, period);
            else
                bukkitTask = Bukkit.getScheduler().runTaskTimer(AmazingTowers.getInstance(), this, 0L, period);
    }

    public void cancel() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
    }

}
