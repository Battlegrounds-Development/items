package me.remag501.itemsbgs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles the logic for custom items when they are dropped (activated).
 */
public class ItemListener implements Listener {

    private final ItemsBGS plugin;

    public ItemListener(ItemsBGS plugin) {
        this.plugin = plugin;
    }

    /**
     * Prevents the custom item from turning into a normal dropped item entity.
     * This allows us to handle the effect instantly upon drop.
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        String itemId = CustomItems.getCustomItemId(droppedItem);

        if (itemId != null) {
            // Cancel the event so the item entity is not spawned immediately
            event.setCancelled(true);

            // Item is a custom item, execute the ability after a short delay (for animation/sound)
            // The item is taken from the player's inventory as part of the drop event flow.
            Player player = event.getPlayer();
            Location location = player.getLocation();

            // The item entity is created but immediately removed in the runnable
            // to allow for the visual effect to happen where the player is looking,
            // or where the player is standing.

            // Get the item that was dropped (the ItemStack the player intended to drop)
            ItemStack itemInHand = droppedItem.clone();
            itemInHand.setAmount(1); // Only processing one item

            // Remove the single item from the player's inventory
            player.getInventory().removeItem(itemInHand);

            // Execute the ability in a slight delay to simulate the throw
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location targetLoc = location; // Start location

                    // Simulate where a thrown item would land (a short distance forward)
                    // In a real application, you might use a projectile entity, but for simplicity:
                    targetLoc = player.getTargetBlock(null, 10).getLocation().add(0.5, 0.5, 0.5);

                    if (itemId.equals(CustomItems.MOLOTOV_ID)) {
                        activateMolotov(targetLoc);
                        player.sendMessage("§6Molotov thrown!");
                    } else if (itemId.equals(CustomItems.GRENADE_ID)) {
                        activateGrenade(targetLoc);
                        player.sendMessage("§bGrenade thrown!");
                    }
                }
            }.runTaskLater(plugin, 5L); // 5 ticks delay (0.25 seconds)
        }
    }

    /**
     * Prevents the dropped custom item entity from being created (as we handle it manually).
     * This is a safeguard if the PlayerDropItemEvent cancellation is bypassed or delayed.
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        String itemId = CustomItems.getCustomItemId(item.getItemStack());

        if (itemId != null) {
            // If the item entity is somehow spawned, remove it immediately
            event.setCancelled(true);
        }
    }


    /**
     * Executes the Molotov ability (creates fire).
     */
    private void activateMolotov(Location loc) {
        // Play sound and particle effect
        loc.getWorld().playSound(loc, Sound.ITEM_FIRECHARGE_USE, 2.0F, 1.0F);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.2, 0.2, 0.2, 0.1);

        // Create a small area of fire
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location fireLoc = loc.clone().add(x, 0, z);
                if (fireLoc.getBlock().getType().isAir()) {
                    fireLoc.getBlock().setType(org.bukkit.Material.FIRE);
                }
            }
        }
    }

    /**
     * Executes the Grenade ability (creates a non-destructive explosion).
     */
    private void activateGrenade(Location loc) {
        // Play a small explosion sound and particle effect
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 1.5F);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1, 0.1, 0.1, 0.1, 0.0);

        // Create an explosion that does NOT destroy blocks (setFire: false, breakBlocks: false)
        // Power of 2 is a small, player-safe explosion.
        loc.getWorld().createExplosion(loc, 2.0F, false, false);
    }
}

