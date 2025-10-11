package me.remag501.itemsbgs.listener;

import me.remag501.itemsbgs.item.CustomItem;
import me.remag501.itemsbgs.manager.ItemManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles the logic for custom items when they are dropped (activated).
 * This class relies only on the ItemManager and CustomItem interface.
 */
public class ItemListener implements Listener {

    private final Plugin plugin;
    private final ItemManager itemManager;

    public ItemListener(Plugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    /**
     * Listens for the drop event and delegates activation to the CustomItem instance.
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        String itemId = itemManager.getCustomItemId(droppedItem);

        // Check if the item is a registered custom item
        if (itemId != null) {
            // Cancel the event so the item entity is not spawned
            event.setCancelled(true);

            CustomItem customItem = itemManager.getItemById(itemId);
            if (customItem == null) {
                event.getPlayer().sendMessage("§cError: Custom item logic not found for " + itemId);
                return;
            }

            Player player = event.getPlayer();

            // Item removal must happen here since the drop event is cancelled
            ItemStack itemInHand = droppedItem.clone();
            itemInHand.setAmount(1);
            player.getInventory().removeItem(itemInHand);

            // Calculate the activation location (simulating a throw)
            Location activationLoc = player.getTargetBlock(null, 10).getLocation().add(0.5, 0.5, 0.5);

            // Execute the activation logic on a slight delay to simulate a throw animation
            new BukkitRunnable() {
                @Override
                public void run() {
                    // This calls the specific CustomItem implementation's logic
                    customItem.onActivate(player, activationLoc, plugin);
                }
            }.runTaskLater(plugin, 5L); // 5 ticks delay (0.25 seconds)
        }
    }
}
