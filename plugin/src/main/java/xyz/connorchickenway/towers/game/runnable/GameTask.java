package xyz.connorchickenway.towers.game.runnable;

import xyz.connorchickenway.towers.game.Game;
import xyz.connorchickenway.towers.game.runnable.util.TaskId;

public abstract class GameTask extends GameRunnable {

    public GameTask(Game game, TaskId taskId, long tick, boolean async) {
        super(game, taskId, tick, async);
    }

    public GameTask(Game game, TaskId taskId, long tick) {
        super(game, taskId, tick);
    }

    @Override
    public void run() {
        if (cancelTask()) {
            this.doCancelStuff();
            this.cancel();
        }
        this.doStuff();
    }

    public abstract boolean cancelTask();

    public abstract void doCancelStuff();

}
