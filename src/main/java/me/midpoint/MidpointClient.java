package me.midpoint;

import me.midpoint.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MidpointClient implements ClientModInitializer {
    public static List<BaseModule> modules = new ArrayList<>();
    
    // Горячие клавиши
    private static KeyBinding toggleAntiBan;
    private static KeyBinding toggleAutoMiner;
    private static KeyBinding toggleAutoBuyer;

    @Override
    public void onInitializeClient() {
        // Регистрация модулей
        modules.add(new AntiBanModule());
        modules.add(new AutoMinerModule());
        modules.add(new AutoBuyerModule());

        // Регистрация горячих клавиш (без Fabric API!)
        toggleAntiBan = new KeyBinding(
            "key.midpoint.antiban",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F1,
            "key.midpoint.category"
        );
        
        toggleAutoMiner = new KeyBinding(
            "key.midpoint.autominer",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F2,
            "key.midpoint.category"
        );
        
        toggleAutoBuyer = new KeyBinding(
            "key.midpoint.autobuyer",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F3,
            "key.midpoint.category"
        );

        // Только для Fabric API — но мы не можем зарегистрировать клавиши без Fabric API
        // Поэтому используем прямой перехват в миксине

        System.out.println("[Midpoint] God Mode загружен! Модулей: " + modules.size());
        System.out.println("[Midpoint] Настрой модули через код или добавь GUI.");
    }
}
