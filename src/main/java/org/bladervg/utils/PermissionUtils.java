package org.bladervg.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bladervg.SegundoMod;

import java.util.List;

public class PermissionUtils {

    public static boolean hasPermission(ServerCommandSource source, List<String> permissions, int level) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        if (luckPerms == null) {
            SegundoMod.LOGGER.error("LuckPerms not found");
            return false;
        }
        UserManager userManager = luckPerms.getUserManager();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return source.hasPermissionLevel(level);
        User user = userManager.getUser(player.getUuid());
        if (user == null) {
            SegundoMod.LOGGER.error("User not found in LuckPerms");
            return false;
        }
        for (String permission : permissions) {
            if (permission == null || permission.isEmpty()) return true;
            if (user.getCachedData().getPermissionData().checkPermission(permission).asBoolean()) return true;
        }
        return source.hasPermissionLevel(level);
    }
}
