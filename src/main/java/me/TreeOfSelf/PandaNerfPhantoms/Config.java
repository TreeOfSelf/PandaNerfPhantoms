package me.TreeOfSelf.PandaNerfPhantoms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "config/PandaNerfPhantoms.json";
    
    public int insomniaDays = 7;
    public boolean burnPhantoms = true;
    
    public static Config load() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                return GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                PandaNerfPhantoms.LOGGER.error("Failed to load config, using defaults", e);
            }
        }
        
        Config config = new Config();
        config.save();
        return config;
    }
    
    public void save() {
        File configFile = new File(CONFIG_FILE);
        configFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            PandaNerfPhantoms.LOGGER.error("Failed to save config", e);
        }
    }
    
    public int getInsomniaThresholdTicks() {
        return insomniaDays * 24000;
    }
}

