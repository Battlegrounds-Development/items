package me.remag501.itemsbgs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;

/**
 * Handles the /giveitems command for distributing custom items.
 */
public class ItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            return false; // Show plugin.yml usage
        }

        String itemType = args[0].toLowerCase();
        int amount = 1;

        // Parse quantity if provided
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 1) {
                    amount = 1; // Minimum 1
                } else if (amount > 64) {
                    amount = 64; // Max stack size
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid amount specified. Defaulting to 1.");
            }
        }

        ItemStack itemToGive = null;
        String itemName = "";

        // Determine which item to give
        if (itemType.equals(CustomItems.MOLOTOV_ID) || itemType.startsWith("molo")) {
            itemToGive = CustomItems.createMolotov(amount);
            itemName = "Molotov Cocktail";
        } else if (itemType.equals(CustomItems.GRENADE_ID) || itemType.startsWith("gren")) {
            itemToGive = CustomItems.createGrenade(amount);
            itemName = "Frag Grenade";
        } else {
            player.sendMessage("§cInvalid item name. Use 'molotov' or 'grenade'.");
            return true;
        }

        // Give the item and notify the player
        if (itemToGive != null) {
            player.getInventory().addItem(itemToGive);
            player.sendMessage("§aYou received §b" + amount + " " + itemName + "§a.");
        }

        return true;
    }
}

