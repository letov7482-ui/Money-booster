package me.midpoint;

import me.midpoint.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

public class MidpointClient implements ClientModInitializer {
    public static List<BaseModule> modules = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        modules.add(new AntiBanModule());
        modules.add(new AutoMinerModule());
        modules.add(new AutoBuyerModule());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            for (BaseModule m : modules) {
                if (m.enabled) m.tick();
            }
        });

        System.out.println("[Midpoint] Загружен!");
    }
}
