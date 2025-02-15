package org.bladervg;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.fabricmc.api.ModInitializer;
import org.bladervg.command.CommandTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SegundoMod implements ModInitializer {
    public static final String MOD_ID = "segundomod";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        event();
    }

    public void load() {

    }

    private void event() {
        CommandRegistrationEvent.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LOGGER.info("Se registra el comando de SegundoMod");
            CommandTree.register(commandDispatcher, commandRegistryAccess, registrationEnvironment);
        });
    }
}