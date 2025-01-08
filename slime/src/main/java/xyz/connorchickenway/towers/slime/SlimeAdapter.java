package xyz.connorchickenway.towers.slime;

public interface SlimeAdapter {

    void createEmptyWorld(String worldName, boolean readOnly);

    void loadWorld(String worldName, boolean readOnly);

    void deleteWorld(String worldName);

    boolean worldExists(String worldName);

}
