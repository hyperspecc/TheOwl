package com.theowl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TheOwlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getSession() != null && !TheOwl.overrideActive) {
                TheOwl.initialize(client.getSession());
            }
        });

        TheOwl.LOGGER.info("TheOwl mod initialized");
    }
}
