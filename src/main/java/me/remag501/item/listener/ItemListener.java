package me.remag501.item.listener;

import me.remag501.bgscore.api.event.EventService;
import me.remag501.item.manager.ItemManager;
import me.remag501.item.model.CustomItem;
import me.remag501.item.model.ProjectileItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener {

    public ItemListener(EventService eventService, ItemManager itemManager) {
        eventService.subscribe(PlayerInteractEvent.class)
                // 1. Action Filter
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                // 2. Item Null Check
                .filter(e -> e.getItem() != null && e.getItem().getType() != Material.AIR)
                // 3. Identification Filter (Uses your ItemManager)
                .filter(e -> itemManager.getCustomItemId(e.getItem()) != null)
                // 4. Final Handler (The execution phase)
                .handler(event -> {
                    String id = itemManager.getCustomItemId(event.getItem());
                    CustomItem item = itemManager.getItemById(id);
                    if (item == null) return;

                    event.setCancelled(true);
                    Player player = event.getPlayer();

                    if (item instanceof ProjectileItem proj) {
                        itemManager.handleProjectile(player, proj, event.getItem());
                    } else {
                        item.onActivate(player);
                    }
                });
    }

}
