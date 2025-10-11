package me.remag501.itemsbgs;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemsBGS extends JavaPlugin {

    private static ItemsBGS plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("ItemsBGS has been enabled!");

        // Register the command executor
        getCommand("giveitems").setExecutor(new ItemCommand());

        // Register the event listener
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemsBGS has been disabled!");
    }

    /**
     * Static accessor for the plugin instance, used for NamespacedKey creation.
     */
    public static ItemsBGS getPlugin() {
        return plugin;
    }
}
