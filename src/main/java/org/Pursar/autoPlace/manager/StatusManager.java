package org.Pursar.autoPlace.manager;

import org.Pursar.autoPlace.AutoPlace;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatusManager {

    private final AutoPlace plugin;
    private final File dataFolder;
    private final Map<UUID, Boolean> statusMap;

    public StatusManager(AutoPlace plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.statusMap = new ConcurrentHashMap<>();

        dataFolder.mkdirs();
    }

    public void loadData() {
        File dataFile = new File(dataFolder, "playerdata.yml");
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection sec = config.getConfigurationSection("list");
        if (sec == null) {
            return;
        }

        for (String uuidStr : sec.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            boolean status = config.getBoolean("list." + uuid, false);
            statusMap.put(uuid, status);
        }
    }

    public void saveData() {
        if (statusMap.isEmpty()) {
            return;
        }

        File dataFile = new File(dataFolder, "playerdata.yml");
        YamlConfiguration config = new YamlConfiguration();

        statusMap.forEach((uuid, status) -> {
            config.set("list." + uuid, status);
        });

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("데이터 저장 중, 오류가 발생했습니다. :");
            e.printStackTrace();
        }
    }

    public boolean getStatus(Player player) {
        return statusMap.getOrDefault(player.getUniqueId(), false);
    }

    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();
        boolean status = statusMap.getOrDefault(uuid, false);
        statusMap.put(uuid, !status);
        return !status;
    }

    public int hasItemAmount(Player player, ItemStack item) {
        int total = 0;

        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType() != Material.AIR) {
                ItemStack invItem = content.clone();
                int itemAmount = content.getAmount();
                if (invItem.isSimilar(item)) {
                    total += itemAmount;
                }
            }
        }

        return total;
    }

    public void removeItem(Player player, ItemStack itemStack, int amount) {
        if (amount <= 0) {
            return;
        }

        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        int total = 0;
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }

            if (item.isSimilar(itemStack)) {
                total += item.getAmount();
            }
        }

        if (total < amount) {
            return;
        }

        int remaining = amount;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) {
                continue;
            }

            if (!item.isSimilar(itemStack)) {
                continue;
            }

            int stackAmount = item.getAmount();

            if (stackAmount <= remaining) {
                remaining -= stackAmount;
                contents[i] = null;
            } else {
                item.setAmount(stackAmount - remaining);
                remaining = 0;
            }

            if (remaining <= 0) {
                break;
            }
        }

        inv.setContents(contents);
    }
}
