package me.midpoint;

import me.midpoint.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MidpointClient implements ClientModInitializer {
    public static List<BaseModule> modules = new ArrayList<>();
    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // Регистрация модулей
        modules.add(new AntiBanModule());
        modules.add(new AutoMinerModule());
        modules.add(new AutoBuyerModule());

        // Клавиша для включения/выключения всех модулей (P)
        toggleKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.midpoint.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.midpoint"
            )
        );

        // Tick обработчик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed()) {
                for (BaseModule m : modules) {
                    m.toggle();
                }
                System.out.println("[Midpoint] Модули переключены!");
            }

            if (client.player == null) return;
            for (BaseModule m : modules) {
                if (m.enabled) m.tick();
            }
        });

        System.out.println("[Midpoint] God Mode загружен! Модулей: " + modules.size());
        System.out.println("[Midpoint] Нажми P для включения/выключения модулей.");
    }
}
