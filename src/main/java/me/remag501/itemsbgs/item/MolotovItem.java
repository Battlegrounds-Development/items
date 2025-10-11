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
 */
public class MolotovItem implements CustomItem {

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
                "§cDrop to use."
        ));

        // The manager handles setting the PersistentDataContainer ID
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onActivate(Player activator, Location activationLoc, Plugin plugin) {
        activator.sendMessage("§6Molotov thrown!");

        // Play sound and particle effect
        activationLoc.getWorld().playSound(activationLoc, Sound.ITEM_FIRECHARGE_USE, 2.0F, 1.0F);

        // Create a small area of fire
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location fireLoc = activationLoc.clone().add(x, 0, z);
                if (fireLoc.getBlock().getType().isAir()) {
                    fireLoc.getBlock().setType(Material.FIRE);
                }
            }
        }
    }
}
