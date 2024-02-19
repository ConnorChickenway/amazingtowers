package xyz.connorchickenway.towers.utilities;

import xyz.connorchickenway.towers.AmazingTowers;

import java.util.logging.Level;

public class Logger {

    private static java.util.logging.Logger logger = AmazingTowers.getInstance().getLogger();

    public static void info(String info) {
        logger.info(info);
    }

    public static void severe(String m) {
        logger.severe(m);
    }

    public static void error(String info, Exception ex) {
        logger.log(Level.WARNING, info, ex);
    }

}
