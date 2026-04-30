package org.Pursar.autoPlace;

import org.Pursar.autoPlace.command.PlayerCommand;
import org.Pursar.autoPlace.listener.PlayerListener;
import org.Pursar.autoPlace.manager.StatusManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPlace extends JavaPlugin {

    private static AutoPlace instance;

    private StatusManager statusManager;

    @Override
    public void onEnable() {
        instance = this;
        statusManager = new StatusManager(this);

        getCommand("자동심기").setExecutor(new PlayerCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        statusManager.loadData();
        getLogger().info("플러그인이 활성화 되었습니다.");
    }

    @Override
    public void onDisable() {
        statusManager.saveData();
        getLogger().info("플러그인이 비활성화 되었습니다.");
    }

    public static AutoPlace getInstance() {
        return instance;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }
}
