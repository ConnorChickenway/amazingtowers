package xyz.connorchickenway.towers.utilities;

public enum GameMode {

    MULTI_ARENA, BUNGEE_MODE;

    private static GameMode gameMode;

    public static void setGameMode(GameMode gameMode) {
        GameMode.gameMode = gameMode;
    }

    public static boolean isMultiArena() {
        return gameMode == GameMode.MULTI_ARENA;
    }

    public static boolean isBungeeMode() {
        return gameMode == GameMode.BUNGEE_MODE;
    }

}
