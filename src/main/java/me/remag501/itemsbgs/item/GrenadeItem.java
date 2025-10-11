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
 * Concrete implementation of the Frag Grenade item.
 */
public class GrenadeItem implements CustomItem {

    private static final String ID = "grenade";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ItemStack getItem(int amount) {
        ItemStack item = new ItemStack(Material.COBBLESTONE, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§b§lFrag Grenade");
        meta.setLore(Arrays.asList(
                "§7A simple timed explosive that",
                "§7causes a small, non-destructive blast.",
                "",
                "§bDrop to use."
        ));

        // The manager handles setting the PersistentDataContainer ID
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onActivate(Player activator, Location activationLoc, Plugin plugin) {
        activator.sendMessage("§bGrenade thrown!");

        // Play a small explosion sound and particle effect
        activationLoc.getWorld().playSound(activationLoc, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 1.5F);

        // Create an explosion that does NOT destroy blocks (setFire: false, breakBlocks: false)
        // Power of 2 is a small, player-safe explosion.
        activationLoc.getWorld().createExplosion(activationLoc, 2.0F, false, false);
    }
}
