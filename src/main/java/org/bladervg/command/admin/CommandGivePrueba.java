package org.bladervg.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.bladervg.utils.PermissionUtils;

import java.util.List;

public class CommandGivePrueba {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment Environment, LiteralArgumentBuilder<ServerCommandSource> base) {

    dispatcher.register(
        base.then(
            CommandManager.literal("give")
                .requires(source -> PermissionUtils.hasPermission(source, List.of("segundomod.give", "segundomod.admin"), 4))
                .executes(context -> {
                  ItemStack itemStack = Items.PLAYER_HEAD.getDefaultStack();
                  ServerPlayerEntity player = context.getSource().getPlayer();
                  assert player != null;
                  //itemStack.setCount(64);
                  itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("%player%".replace("%player%", player.getGameProfile().getName())));
                  itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(1));
                  itemStack.set(DataComponentTypes.LORE, new LoreComponent(List.of(Text.literal("Cabeza de Jugador %player%".replace("%player%", player.getGameProfile().getName())))));
                  itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(player.getGameProfile()));
                  player.getInventory().insertStack(itemStack);
                  return 1;
                })
        )


    );
  }
}
