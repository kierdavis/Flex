package com.kierdavis.flex;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FlexData {
    protected static FlexData instance = null;
    
    public static FlexData getInstance() {
        if (instance == null) {
            instance = new FlexData();
        }
        return instance;
    }
    
    protected File dataFolder = null;
    protected FileConfiguration config = null;
    
    protected FlexData() {
        File pluginsFolder = Bukkit.getPluginManager().getPlugins()[0].getDataFolder().getParentFile();
        
        dataFolder = new File(pluginsFolder, "Flex");
    }
    
    protected void setDefaults(ConfigurationSection c) {
        c.addDefault("text.invalid-command", "No such command /%s.");
        c.addDefault("text.no-subcommands", "No subcommands for command /%s.");
        c.addDefault("text.subcommand-list", "Subcommands of /%s:");
        c.addDefault("text.no-permission", "You don't have permission to use this command.");
        c.addDefault("text.players-only", "This command can only be used by players.");
        c.addDefault("text.internal-error", "There was an internal error while running the command. Please contact an administrator.");
        c.addDefault("text.not-enough-args", "Not enough arguments provided to command /%s.");
    }
    
    public FileConfiguration getConfig() {
        if (config == null) {
            File configFile = new File(dataFolder, "config.yml");
            
            if (configFile.exists()) {
                config = YamlConfiguration.loadConfiguration(configFile);
                setDefaults(config);
            }
            
            else {
                config = new YamlConfiguration();
                setDefaults(config);
                
                File parent = configFile.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                
                config.save(configFile);
            }
        }
        
        return config;
    }
    
    public String getText(String name, Object... arguments) {
        String fmt = getConfig().getConfigurationSection("text").getString(name);
        return String.format(fmt, arguments);
    }
}
