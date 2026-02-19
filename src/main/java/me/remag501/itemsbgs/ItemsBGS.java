package me.remag501.itemsbgs;

import me.remag501.bgscore.BGSCore;
import me.remag501.bgscore.api.BGSApi;
import me.remag501.bgscore.api.command.CommandService;
import me.remag501.bgscore.api.event.EventService;
import me.remag501.bgscore.api.namespace.NamespaceService;
import me.remag501.bgscore.api.task.TaskService;
import me.remag501.itemsbgs.command.ItemsBGSCommand;
import me.remag501.itemsbgs.item.GrenadeItem;
import me.remag501.itemsbgs.item.MolotovItem;
import me.remag501.itemsbgs.item.TearGasItem;
//import me.remag501.itemsbgs.listener.ItemListener;
import me.remag501.itemsbgs.listener.ItemListener;
import me.remag501.itemsbgs.manager.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the ItemsBGS Spigot plugin.
 * Now manages the ItemManager for a scalable item structure.
 */
public class ItemsBGS extends JavaPlugin {

    private ItemManager itemManager;
    private TaskService taskService;
    private NamespaceService namespaceService;

    @Override
    public void onEnable() {
        getLogger().info("ItemsBGS: Initializing Item Manager and registering items.");

        // Get services from api
        EventService eventService = BGSApi.events();
        taskService = BGSApi.tasks();
        namespaceService = BGSApi.namespaces();
        CommandService commandService = BGSApi.commands();

        // Initialize and register items
        itemManager = new ItemManager(namespaceService);
        registerCustomItems();

        // Register the event listener (passing the manager)
        new ItemListener(eventService, itemManager);

        // Register command executor (passing the manager)
        ItemsBGSCommand itemsCmd = new ItemsBGSCommand(itemManager);

        // Register for this plugin command
        getCommand("itemsbgs").setExecutor(itemsCmd);
        getCommand("itemsbgs").setTabCompleter(itemsCmd);
        commandService.registerSubcommand("item", itemsCmd);

        getLogger().info("ItemsBGS has been enabled!");
    }

    /**
     * Registers all custom item classes with the ItemManager.
     * Adding a new item simply requires adding a line here.
     */
    private void registerCustomItems() {
        itemManager.registerItem(new MolotovItem(taskService, namespaceService));
        itemManager.registerItem(new TearGasItem(taskService, namespaceService));
        itemManager.registerItem(new GrenadeItem());
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemsBGS has been disabled!");
    }

}
