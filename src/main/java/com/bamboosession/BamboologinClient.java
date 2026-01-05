package com.bamboosession;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class BamboologinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Initialize session management on first tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getSession() != null && !BambooSession.overrideActive) {
                BambooSession.initialize(client.getSession());
            }
        });

        BambooSession.LOGGER.info("Bamboo Session mod initialized");
    }
}