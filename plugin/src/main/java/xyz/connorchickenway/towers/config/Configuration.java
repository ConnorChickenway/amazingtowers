package xyz.connorchickenway.towers.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.connorchickenway.towers.AmazingTowers;

import java.io.*;

public class Configuration {

    private File file;
    private FileConfiguration configuration;

    public Configuration(String name, String directory) {
        this.file = new File(directory, name.contains(".yml") ? name : name + ".yml");
        this.saveResource();
        this.loadConfiguration();
    }

    public void loadConfiguration() {
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    private void saveResource() {
        try {
            if (!file.exists()) file.createNewFile();
            else return;
            InputStream in = AmazingTowers.getInstance().getResource(file.getName());
            if (in == null) return;
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int read;
            while ((read = in.read(buf)) > 0)
                out.write(buf, 0, read);
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setObject(String path, Object object) {
        this.configuration.set(path, object);
    }

    public void saveConfiguration() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration() {
        return configuration;
    }

}
