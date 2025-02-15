package org.bladervg.model;

import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.bladervg.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Medalla {
  private String id; // ID
  private String item; // Item
  private String name; // Nombre
  private List<String> lore; // Lore
  private int customModelData; // CustomModelData -> imagen/icon

  public Medalla(String id, String item, String name, List<String> lore, int customModelData) {
    this.id = id;
    this.item = item;
    this.name = name;
    this.lore = lore;
    this.customModelData = customModelData;
  }

  public GooeyButton getIcon(ServerPlayerEntity player) {
    boolean hasPermission = PermissionUtils.hasPermission(player.getCommandSource(), List.of("segundomod.medallas." + getId()), 4);
    var itemStack = getItemStack(hasPermission);

    GooeyButton button = GooeyButton
        .builder()
        .display(itemStack)
        .build();

    return button;
  }


  private ItemStack getItemStack(boolean hasPermission) {
    ItemStack itemStack;
    String item = hasPermission ? getItem() : "minecraft:barrier";
    itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(item)));
    itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(getName()));
    List<Text> texts = new ArrayList<>();
    for (String lore : getLore()) {
      texts.add(Text.literal(lore));
    }
    itemStack.set(DataComponentTypes.LORE, new LoreComponent(texts));
    itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(customModelData));
    return itemStack;
  }
}
