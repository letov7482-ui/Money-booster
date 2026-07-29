package me.midpoint;

import me.midpoint.modules.*;
import net.fabricmc.api.ClientModInitializer;

import java.util.ArrayList;
import java.util.List;

public class MidpointClient implements ClientModInitializer {
    public static List<BaseModule> modules = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        modules.add(new AntiBanModule());
        modules.add(new AutoMinerModule());
        modules.add(new AutoBuyerModule());

        System.out.println("[Midpoint] God Mode загружен! Модулей: " + modules.size());
    }
}
