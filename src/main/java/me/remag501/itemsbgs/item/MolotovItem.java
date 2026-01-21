package me.remag501.itemsbgs.item;

import me.remag501.itemsbgs.model.AbstractTargetingItem;
import me.remag501.itemsbgs.runnable.MolotovTracker;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Concrete implementation of the Molotov Cocktail item.
 */
public class MolotovItem extends AbstractTargetingItem {

    public static final String METADATA_KEY = "MOLOTOV_PROJECTILE";
    private final Plugin plugin;

    public MolotovItem(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getId() {
        return "molotov";
    }

    @Override
    public ItemStack getItem(int amount) {
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lMolotov §6Cocktail §e✪✪✪");
            meta.setLore(Arrays.asList(
                    "§8• §fA crude explosive that causes",
                    "§fa short-lived patch of fire.",
                    "§r",
                    "§7§o(( Right-click to use. ))"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void onThrow(Player activator, Location targetLocation) {
        activator.sendMessage("§a§l(!) §aMolotov thrown!");

        ItemStack torchStack = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) torchStack.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.ORANGE);
            torchStack.setItemMeta(meta);
        }

        Item molotovItem = activator.getWorld().dropItem(activator.getEyeLocation(), torchStack);
        molotovItem.setInvulnerable(true);
        molotovItem.setPickupDelay(32767);

        Vector velocity = targetLocation.toVector().subtract(activator.getEyeLocation().toVector());
        velocity.normalize().multiply(1.5).setY(velocity.getY() + 0.3);
        molotovItem.setVelocity(velocity);

        molotovItem.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        // Industry Standard: Pass the plugin dependency into the new class
        new MolotovTracker(molotovItem, plugin).runTaskTimer(plugin, 1L, 1L);
    }
}