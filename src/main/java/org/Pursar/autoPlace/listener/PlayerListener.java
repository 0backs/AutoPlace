package org.Pursar.autoPlace.listener;

import org.Pursar.autoPlace.AutoPlace;
import org.Pursar.autoPlace.manager.StatusManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class PlayerListener implements Listener {

    private static final Set<Material> AUTOPLACE_TYPE = Set.of(
            Material.WHEAT, Material.BEETROOTS, Material.CARROTS,
            Material.POTATOES, Material.NETHER_WART
    );

    private final AutoPlace plugin;
    private final StatusManager statusManager;

    public PlayerListener(AutoPlace plugin) {
        this.plugin = plugin;
        this.statusManager = plugin.getStatusManager();
    }

    @EventHandler(ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!statusManager.getStatus(player)) {
            return;
        }

        Block block = event.getBlock();
        Material type = block.getType();
        Location loc = block.getLocation();

        if (!AUTOPLACE_TYPE.contains(type)) {
            return;
        }

        Material seedType = switch (type) {
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case NETHER_WART -> Material.NETHER_WART;
            default -> Material.WHEAT_SEEDS;
        };

        ItemStack seed = new ItemStack(seedType);
        if (statusManager.hasItemAmount(player, seed) <= 0) {
            return;
        }

        statusManager.removeItem(player, seed, 1);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            loc.getBlock().setType(type);
        }, 1L);
    }
}
