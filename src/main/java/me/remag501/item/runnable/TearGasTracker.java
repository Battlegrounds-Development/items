package me.remag501.item.runnable;

import me.remag501.bgscore.api.task.KeyedTickable;
import me.remag501.item.item.TearGasItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class TearGasTracker implements KeyedTickable {

    private final Item item;
    private int ticksStationary = 0;

    // AreaEffectCloud Constants
    private static final int CLOUD_DURATION_TICKS = 10 * 20;
    private static final float CLOUD_RADIUS = 3.0f;
    private static final float CLOUD_RADIUS_PER_TICK = -0.01f;
    private static final int CLOUD_WAIT_TIME = 5;
    private static final int REAPPLICATION_DELAY = 20;

    // Potion Effect Constants
    private static final int EFFECT_DURATION_TICKS = 4 * 20;
    private static final int POISON_AMPLIFIER = 0;
    private static final int NAUSEA_AMPLIFIER = 0;

    // Bridge constants
    private static final int ACTIVATION_TICKS_THRESHOLD = TearGasItem.ACTIVATION_TICKS_THRESHOLD;
    private static final int PROC_DELAY_TICKS = TearGasItem.PROC_DELAY_TICKS;
    private static final double STATIONARY_VELOCITY_THRESHOLD = TearGasItem.STATIONARY_VELOCITY_THRESHOLD;
    private static final int FAILSAFE_DESPAWN_TICKS = TearGasItem.FAILSAFE_DESPAWN_TICKS;

    public TearGasTracker(Item item) {
        this.item = item;
    }

    @Override
    public UUID getOwnerId() {
        // Track by the Item Entity UUID
        return item.getUniqueId();
    }

    @Override
    public String getTaskType() {
        return "teargas_tracker";
    }

    @Override
    public boolean tick() {
        if (!item.isValid()) {
            return true;
        }

        if (item.getTicksLived() < ACTIVATION_TICKS_THRESHOLD) {
            return false;
        }

        // Velocity-based stationary check
        if (item.getVelocity().lengthSquared() < STATIONARY_VELOCITY_THRESHOLD) {
            ticksStationary++;
        } else {
            ticksStationary = 0;
        }

        // Activation check
        if (ticksStationary >= PROC_DELAY_TICKS) {
            if (item.isOnGround() || item.isInWater() || item.getVelocity().lengthSquared() < STATIONARY_VELOCITY_THRESHOLD) {
                spawnTearGasCloud(item.getLocation());
                item.remove();
                return true;
            }
        }

        // Failsafe
        if (item.getTicksLived() > FAILSAFE_DESPAWN_TICKS) {
            spawnTearGasCloud(item.getLocation());
            item.remove();
            return true;
        }

        return false;
    }

    private void spawnTearGasCloud(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.5f);

        Location cloudSpawnLoc = location.clone();
        Block blockAtLoc = cloudSpawnLoc.getBlock();
        if (blockAtLoc.getType().isSolid()) {
            cloudSpawnLoc.add(0, 0.5, 0);
        }

        AreaEffectCloud gasCloud = (AreaEffectCloud) location.getWorld().spawnEntity(cloudSpawnLoc, EntityType.AREA_EFFECT_CLOUD);

        gasCloud.setDuration(CLOUD_DURATION_TICKS);
        gasCloud.setRadius(CLOUD_RADIUS);
        gasCloud.setRadiusPerTick(CLOUD_RADIUS_PER_TICK);
        gasCloud.setWaitTime(CLOUD_WAIT_TIME);
        gasCloud.setReapplicationDelay(REAPPLICATION_DELAY);

        // We use "CONFUSION" for Nausea
        PotionEffect poison = new PotionEffect(PotionEffectType.POISON, EFFECT_DURATION_TICKS, POISON_AMPLIFIER, true, false);
        PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION, EFFECT_DURATION_TICKS, NAUSEA_AMPLIFIER, true, false);

        gasCloud.addCustomEffect(poison, true);
        gasCloud.addCustomEffect(nausea, true);

        gasCloud.setParticle(Particle.SMOKE_NORMAL);
        gasCloud.setColor(Color.fromRGB(150, 150, 150));
    }
}