package me.remag501.itemsbgs.item;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface CustomItem {

    /**
     * @return A unique String identifier for the item (e.g., "molotov", "grenade").
     */
    String getId();

    /**
     * @return The item stack used for creation and inventory display.
     */
    ItemStack getItem(int amount);

    /**
     * The core logic that runs when the item is activated (dropped).
     * @param activator The player who dropped the item.
     * @param activationLoc The location where the item effect should be focused.
     * @param plugin The main plugin instance for scheduling tasks.
     */
    void onActivate(Player activator, Location activationLoc, Plugin plugin);
}