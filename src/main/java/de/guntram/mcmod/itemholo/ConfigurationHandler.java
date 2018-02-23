package de.guntram.mcmod.itemholo;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

    private static ConfigurationHandler instance;

    private Configuration config;
    private String configFileName;
    private boolean itemTitles, mobTitles;
    private String itemPattern, mobPattern;

    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }

    public void load(final File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        // System.out.println("OnConfigChanged for "+event.getModID());
        if (event.getModID().equalsIgnoreCase(ItemHolo.MODID)) {
            loadConfig();
        }
    }
    
    private void loadConfig() {
        itemTitles=config.getBoolean("Show item titles", Configuration.CATEGORY_CLIENT, true, "Show titles for items");
        mobTitles=config.getBoolean("Show mob titles", Configuration.CATEGORY_CLIENT, true, "Show mob titles and health");
        itemPattern=config.getString("Item Search", Configuration.CATEGORY_CLIENT, "", "Search for this expression in items");
        mobPattern=config.getString("Mob Search", Configuration.CATEGORY_CLIENT, "", "Search for this expression in mob names");
        if (config.hasChanged())
            config.save();
    }
    
    public static Configuration getConfig() {
        return getInstance().config;
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }
    
    public static boolean showItemTitles() { return getInstance().itemTitles; }
    public static boolean showMobTitles() { return getInstance().mobTitles; }
    public static String getItemPattern() { return getInstance().itemPattern; }
    public static String getMobPattern() { return getInstance().mobPattern; }
}
