package me.remag501.itemsbgs.runnable;

import me.remag501.bgscore.api.KeyedTickable; // Your new interface
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MolotovTracker implements KeyedTickable {

    private final Item item;
    private final Plugin plugin;
    private int ticksStationary = 0;

    // Config Constants
    private static final int ACTIVATION_THRESHOLD = 10;
    private static final int PROC_DELAY = 2;
    private static final int FIRE_RADIUS = 2;
    private static final int FIRE_DURATION = 60;

    public MolotovTracker(Item item, Plugin plugin) {
        this.item = item;
        this.plugin = plugin;
    }

    @Override
    public UUID getUniqueId() {
        // Link this task to the item entity's UUID
        return item.getUniqueId();
    }

    @Override
    public boolean tick() {
        // Logic: Return true to stop the task, false to keep ticking

        if (!item.isValid()) {
            return true;
        }

        if (item.getTicksLived() < ACTIVATION_THRESHOLD) return false;

        // Check if on ground/stationary
        if (!item.getLocation().subtract(0, 0.1, 0).getBlock().getType().isAir()) {
            ticksStationary++;
        } else {
            ticksStationary = 0;
        }

        if (ticksStationary >= PROC_DELAY) {
            if (item.isOnGround() || item.isInWater()) {
                activate();
                return true; // Finish task
            }
        }

        // Failsafe
        if (item.getTicksLived() > 60) {
            activate();
            return true; // Finish task
        }

        return false; // Continue ticking
    }

    private void activate() {
        Location location = item.getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.5f);

        for (int x = -FIRE_RADIUS; x <= FIRE_RADIUS; x++) {
            for (int z = -FIRE_RADIUS; z <= FIRE_RADIUS; z++) {
                Block blockBelow = location.clone().add(x, -1, z).getBlock();
                Block fireBlock = blockBelow.getRelative(0, 1, 0);

                if (blockBelow.getType().isSolid() && fireBlock.getType() == Material.AIR) {
                    fireBlock.setType(Material.FIRE);

                    // Note: Delayed tasks like fire removal are still fine
                    // as simple BukkitRunnables for now.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (fireBlock.getType() == Material.FIRE) {
                                fireBlock.setType(Material.AIR);
                            }
                        }
                    }.runTaskLater(plugin, FIRE_DURATION);
                }
            }
        }
        item.remove();
    }
}