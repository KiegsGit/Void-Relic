package com.voidrelic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoidRelicMod implements ModInitializer {

    public static final String MOD_ID = "void_relic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.init();
        ModItems.init();
        ServerTickEvents.END_SERVER_TICK.register(VoidMagnet::tick);
        LOGGER.info("Void Relic loaded.");
    }
}
