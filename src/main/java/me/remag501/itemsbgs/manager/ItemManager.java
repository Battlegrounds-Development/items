package me.remag501.itemsbgs.manager;

import me.remag501.bgscore.BGSCore;
import me.remag501.itemsbgs.ItemsBGS;
import me.remag501.itemsbgs.model.CustomItem;
import me.remag501.itemsbgs.model.ProjectileItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Centralized manager for registering, retrieving, and identifying custom items.
 */
public class ItemManager {

    // Key used in PersistentDataContainer to store the item's unique ID
    private final NamespacedKey itemKey;
    private final Map<String, CustomItem> registeredItems = new HashMap<>();
    private final ItemsBGS plugin;

    public ItemManager(ItemsBGS plugin) {
        this.itemKey = new NamespacedKey(plugin, "custom_item_id");
        this.plugin = plugin;
    }

    /**
     * Registers a custom item instance. This should be done during plugin startup.
     * @param item The CustomItem implementation to register.
     */
    public void registerItem(CustomItem item) {
        registeredItems.put(item.getId(), item);
        plugin.getLogger().info("Registered custom item: " + item.getId());
    }

    /**
     * Gets a registered CustomItem by its ID.
     * @param id The unique identifier string.
     * @return The CustomItem object, or null if not found.
     */
    public CustomItem getItemById(String id) {
        return registeredItems.get(id);
    }

    /**
     * Gets the unique ID of the custom item held in an ItemStack.
     * @param item The ItemStack to check.
     * @return The item's unique ID, or null if it's not a custom item.
     */
    public String getCustomItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
    }

    /**
     * Creates a new instance of the custom item with the given amount.
     * @param id The ID of the item to create.
     * @param amount The quantity.
     * @return The ItemStack, or null if the ID is invalid.
     */
    public ItemStack createItemStack(String id, int amount) {
        CustomItem customItem = getItemById(id);
        if (customItem != null) {
            ItemStack stack = customItem.getItem(amount);
            // Apply the unique ID to the PersistentDataContainer
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, id);
            stack.setItemMeta(meta);
            return stack;
        }
        return null;
    }

    /**
     * @return A set of all registered item IDs.
     */
    public Set<String> getRegisteredIds() {
        return registeredItems.keySet();
    }

    public void registerItemLogic() {
        BGSCore.getInstance().getApi().subscribe(PlayerInteractEvent.class)
                // 1. Action Filter
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                // 2. Item Null Check
                .filter(e -> e.getItem() != null && e.getItem().getType() != Material.AIR)
                // 3. Identification Filter (Uses your ItemManager)
                .filter(e -> getCustomItemId(e.getItem()) != null)
                // 4. Final Handler (The execution phase)
                .handler(event -> {
                    String id = getCustomItemId(event.getItem());
                    CustomItem item = getItemById(id);
                    if (item == null) return;

                    event.setCancelled(true);
                    Player player = event.getPlayer();

                    if (item instanceof ProjectileItem proj) {
                        handleProjectile(player, proj, event.getItem());
                    } else {
                        item.onActivate(player);
                    }
                });
    }

    private void handleProjectile(Player player, ProjectileItem proj, ItemStack held) {
        Location loc = proj.getActivationLocation(player);
        if (loc == null) {
            player.sendMessage("§cNo valid target found within range!");
            return;
        }

        // Consumption logic remains the same
        ItemStack one = held.clone();
        one.setAmount(1);
        player.getInventory().removeItem(one);
        player.updateInventory();

        proj.onThrow(player, loc);
    }

}
