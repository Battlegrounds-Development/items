package me.remag501.item.item;

import me.remag501.bgscore.api.namespace.NamespaceService;
import me.remag501.bgscore.api.task.TaskService;
import me.remag501.item.model.AbstractTargetingItem;
import me.remag501.item.runnable.MolotovTracker;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Concrete implementation of the Molotov Cocktail item.
 */
public class MolotovItem extends AbstractTargetingItem {

    public static final String METADATA_KEY = "MOLOTOV_PROJECTILE";

    private final TaskService taskService;
    private final NamespaceService namespaceService;

    public MolotovItem(TaskService taskService, NamespaceService namespaceService) {
        this.taskService = taskService;
        this.namespaceService = namespaceService;
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

        // Tag thrown item entity with namespace
        NamespacedKey key = namespaceService.getCustomItemKey();
        molotovItem.getPersistentDataContainer().set(key, PersistentDataType.STRING, getId());

        // Industry Standard: Pass the plugin dependency into the new class
        taskService.subscribe(new MolotovTracker(taskService, molotovItem));
    }
}