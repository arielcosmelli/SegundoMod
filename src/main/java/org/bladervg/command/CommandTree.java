package org.bladervg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.bladervg.command.admin.CommandGivePrueba;
import org.bladervg.command.admin.CommandInventoryPrueba;
import org.bladervg.utils.PermissionUtils;

import java.util.List;

public class CommandTree {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
    LiteralArgumentBuilder<ServerCommandSource> base = CommandManager.literal("segundomod").requires(source -> PermissionUtils.hasPermission(source, List.of("segundomod.user", "segundomod.admin"), 4));

    CommandGivePrueba.register(dispatcher, registryAccess, environment, base);
    CommandInventoryPrueba.register(dispatcher, registryAccess, environment, base);
  }


}
