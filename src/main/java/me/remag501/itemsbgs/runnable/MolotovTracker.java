package me.remag501.itemsbgs.runnable;

import me.remag501.bgscore.BGSCore;
import me.remag501.bgscore.api.KeyedTickable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

import java.util.UUID;

public class MolotovTracker implements KeyedTickable {

    private final Item item;
    private int ticksStationary = 0;

    // Config Constants
    private static final int ACTIVATION_THRESHOLD = 10;
    private static final int PROC_DELAY = 2;
    private static final int FIRE_RADIUS = 2;
    private static final int FIRE_DURATION = 60;

    public MolotovTracker(Item item) {
        this.item = item;
    }

    @Override
    public UUID getOwnerId() {
        // Link this task to the item entity's UUID
        return item.getUniqueId();
    }

    @Override
    public String getTaskType() {
        // Essential for "The Purge" and debugging
        return "molotov_tracker";
    }

    @Override
    public boolean tick() {
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
                return true;
            }
        }

        // Failsafe (3 seconds)
        if (item.getTicksLived() > 60) {
            activate();
            return true;
        }

        return false;
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

                    // Using the new Core API for delayed cleanup!
                    BGSCore.getInstance().getApi().delay(FIRE_DURATION, () -> {
                        if (fireBlock.getType() == Material.FIRE) {
                            fireBlock.setType(Material.AIR);
                        }
                    });
                }
            }
        }
        item.remove();
    }
}