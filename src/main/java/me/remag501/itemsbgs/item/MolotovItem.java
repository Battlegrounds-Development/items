package me.remag501.itemsbgs.item;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * Concrete implementation of the Molotov Cocktail item.
 * Extends AbstractTargetingItem and is ONLY required to implement onThrow(),
 * as onActivate() is satisfied by the default method in ProjectileItem.
 */
public class MolotovItem extends AbstractTargetingItem {

    private static final String ID = "molotov";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ItemStack getItem(int amount) {
        ItemStack item = new ItemStack(Material.REDSTONE_TORCH, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§6§lMolotov Cocktail");
        meta.setLore(Arrays.asList(
                "§7A crude explosive that causes",
                "§7a short-lived patch of fire.",
                "",
                "§eRight-click to use."
        ));

        // The manager handles setting the PersistentDataContainer ID
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Executes the Molotov effect logic after consumption and target validation.
     * This is the only item-specific execution logic required.
     */
    @Override
    public void onThrow(Player activator, Location targetLocation, Plugin plugin) {
        activator.sendMessage("§6Molotov thrown!");

        // Play sound
        targetLocation.getWorld().playSound(targetLocation, Sound.ITEM_FIRECHARGE_USE, 2.0F, 1.0F);

        // Create a small area of fire (1 block radius around the target location)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location fireLoc = targetLocation.clone().add(x, 0, z);
                // Only place fire if the block is air
                if (fireLoc.getBlock().getType().isAir()) {
                    fireLoc.getBlock().setType(Material.FIRE);
                }
            }
        }
    }
}
