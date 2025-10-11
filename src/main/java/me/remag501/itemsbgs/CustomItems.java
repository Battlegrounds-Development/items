package me.remag501.itemsbgs;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

/**
 * Utility class for creating and identifying custom items using PersistentDataContainer.
 */
public class CustomItems {

    // PersistentDataContainer key to uniquely identify the custom items
    private static final NamespacedKey ITEM_KEY = new NamespacedKey(ItemsBGS.getPlugin(), "custom_item_id");

    public static final String MOLOTOV_ID = "molotov";
    public static final String GRENADE_ID = "grenade";

    /**
     * Creates a custom Molotov item.
     * Molotov uses a REDSTONE_TORCH and ignites a small area.
     * @param amount The quantity of items.
     * @return The Molotov ItemStack.
     */
    public static ItemStack createMolotov(int amount) {
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH, amount);
        ItemMeta meta = item.getItemMeta();

        // Custom Display Name
        meta.setDisplayName("§6§lMolotov Cocktail");

        // Custom Lore
        meta.setLore(Arrays.asList(
                "§7A crude explosive that causes",
                "§7a short-lived patch of fire.",
                "",
                "§cDrop to use."
        ));

        // Set unique identifier using PersistentDataContainer
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, MOLOTOV_ID);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a custom Grenade item.
     * Grenade uses a COBBLESTONE and causes a small explosion.
     * @param amount The quantity of items.
     * @return The Grenade ItemStack.
     */
    public static ItemStack createGrenade(int amount) {
        ItemStack item = new ItemStack(Material.COBBLESTONE, amount);
        ItemMeta meta = item.getItemMeta();

        // Custom Display Name
        meta.setDisplayName("§b§lFrag Grenade");

        // Custom Lore
        meta.setLore(Arrays.asList(
                "§7A simple timed explosive that",
                "§7causes a small, non-destructive blast.",
                "",
                "§bDrop to use."
        ));

        // Set unique identifier using PersistentDataContainer
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, GRENADE_ID);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Checks if an ItemStack is one of the custom items and returns its ID.
     * @param item The ItemStack to check.
     * @return The custom item ID (MOLOTOV_ID or GRENADE_ID), or null if it is not a custom item.
     */
    public static String getCustomItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING);
    }
}
