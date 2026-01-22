package me.remag501.itemsbgs;

import me.remag501.bgscore.BGSCore;
import me.remag501.itemsbgs.command.ItemsBGSCommand;
import me.remag501.itemsbgs.item.GrenadeItem;
import me.remag501.itemsbgs.item.MolotovItem;
import me.remag501.itemsbgs.item.TearGasItem;
//import me.remag501.itemsbgs.listener.ItemListener;
import me.remag501.itemsbgs.manager.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the ItemsBGS Spigot plugin.
 * Now manages the ItemManager for a scalable item structure.
 */
public class ItemsBGS extends JavaPlugin {

    private ItemManager itemManager;

    @Override
    public void onEnable() {
        getLogger().info("ItemsBGS: Initializing Item Manager and registering items.");

        // 1. Initialize and register items
        itemManager = new ItemManager(this);
        registerCustomItems();

        // 2. Register command executor (passing the manager)
        ItemsBGSCommand itemsCmd = new ItemsBGSCommand(itemManager);
        // Register for this plugin command
        getCommand("itemsbgs").setExecutor(itemsCmd);
        getCommand("itemsbgs").setTabCompleter(itemsCmd);
        // Register for core
        BGSCore.getInstance().getCommandRouter().registerSubcommand("item", itemsCmd);

        // 3. Register the event listener (passing the manager)
//        getServer().getPluginManager().registerEvents(new ItemListener(itemManager), this);
        itemManager.registerItemLogic();

        getLogger().info("ItemsBGS has been enabled!");
    }

    /**
     * Registers all custom item classes with the ItemManager.
     * Adding a new item simply requires adding a line here.
     */
    private void registerCustomItems() {
        itemManager.registerItem(new MolotovItem(this));
        itemManager.registerItem(new TearGasItem(this));
        itemManager.registerItem(new GrenadeItem());
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemsBGS has been disabled!");
    }

}
