package org.bladervg.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.bladervg.gui.PruebaGui;
import org.bladervg.utils.PermissionUtils;

import java.util.List;

public class CommandInventoryPrueba {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment, LiteralArgumentBuilder<ServerCommandSource> base) {
    dispatcher.register(
        base.then(
            CommandManager.literal("inventory")
                .requires(source -> PermissionUtils.hasPermission(source, List.of("segundomod.inventory", "segundomod.admin"), 4))
                .executes(context -> {
                      var player = context.getSource().getPlayer();
                      assert player != null;
                      new PruebaGui().open(player);
                      return 1;
                    }
                )
        )
    );
  }
}
