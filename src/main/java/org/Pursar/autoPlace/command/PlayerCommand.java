package org.Pursar.autoPlace.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.Pursar.autoPlace.AutoPlace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

    private final AutoPlace plugin;

    public PlayerCommand(AutoPlace plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player player)) {
            cs.sendMessage("플레이어만 사용이 가능합니다.");
            return false;
        }

        if (!player.hasPermission("autoplace.use")) {
            player.sendMessage(Component.text("명령어를 사용할 권한이 없습니다.", NamedTextColor.RED));
            return false;
        }

        if (plugin.getStatusManager().toggle(player)) {
            player.sendMessage(Component.text("자동심기가 활성화 되었습니다.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("자동심기가 비활성화 되었습니다.", NamedTextColor.RED));
        }

        return true;
    }
}
