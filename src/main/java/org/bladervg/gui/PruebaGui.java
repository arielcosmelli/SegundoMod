package org.bladervg.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bladervg.model.Medalla;

import java.util.List;

@Getter

public class PruebaGui {
  public void open(ServerPlayerEntity player) {
    ChestTemplate template = ChestTemplate
        .builder(6)
        .build();

    // Botones
    Medalla fuego = new Medalla("fire", "minecraft:emerald", "Medalla Fuego", List.of("Esta es una medalla por ganar a Giovani"), 1);
    GooeyButton medallaFuego = fuego.getIcon(player);

    template.set(0, medallaFuego);
    //

    GooeyPage page = GooeyPage.builder()
        .template(template)
        .build();

    UIManager.openUIForcefully(player, page);
  }

}
