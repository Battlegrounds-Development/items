package me.remag501.itemsbgs.command;

import me.remag501.itemsbgs.manager.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /giveitems command, now relying on ItemManager.
 */
public class ItemCommand implements CommandExecutor, TabCompleter {

    private final ItemManager itemManager;

    public ItemCommand(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§cUsage: /giveitems <item_id> [amount]");
            player.sendMessage("§cAvailable items: " + String.join(", ", itemManager.getRegisteredIds()));
            return true;
        }

        String itemId = args[0].toLowerCase();
        int amount = 1;

        // Parse quantity
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
                amount = Math.max(1, Math.min(64, amount)); // Clamp amount between 1 and 64
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid amount specified. Defaulting to 1.");
            }
        }

        // Use the ItemManager to create the item stack
        ItemStack itemToGive = itemManager.createItemStack(itemId, amount);

        if (itemToGive != null) {
            player.getInventory().addItem(itemToGive);
            player.sendMessage("§aYou received §b" + amount + " " + itemId + "§a.");
        } else {
            player.sendMessage("§cInvalid item ID: " + itemId + ". Use one of: " + String.join(", ", itemManager.getRegisteredIds()));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            // Suggest item IDs
            String partialId = args[0].toLowerCase();
            return itemManager.getRegisteredIds().stream()
                    .filter(id -> id.startsWith(partialId))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            // Suggest quantity
            return List.of("1", "5", "16", "64");
        }
        return new ArrayList<>();
    }
}
