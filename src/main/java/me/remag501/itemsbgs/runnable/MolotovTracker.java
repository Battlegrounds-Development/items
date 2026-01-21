package me.remag501.itemsbgs.runnable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MolotovTracker extends BukkitRunnable {

    private final Item item;
    private final Plugin plugin;

    private int ticksStationary = 0;

    // Config Constants
    private static final int ACTIVATION_THRESHOLD = 10;
    private static final int PROC_DELAY = 2;
    private static final int FIRE_RADIUS = 2;
    private static final int FIRE_DURATION = 60; // 3 seconds

    public MolotovTracker(Item item, Plugin plugin) {
        this.item = item;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!item.isValid()) {
            this.cancel();
            return;
        }

        if (item.getTicksLived() < ACTIVATION_THRESHOLD) return;

        // Check if on ground/stationary
        if (!item.getLocation().subtract(0, 1, 0).getBlock().getType().isAir()) {
            ticksStationary++;
        } else {
            ticksStationary = 0;
        }

        if (ticksStationary >= PROC_DELAY) {
            if (item.isOnGround() || item.isInWater()) {
                activate();
                this.cancel();
            }
        }

        // Failsafe
        if (item.getTicksLived() > 60) {
            activate();
            this.cancel();
        }
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

                    // Clean injection: use the stored plugin instance
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