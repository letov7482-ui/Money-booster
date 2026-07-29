package me.midpoint;

import me.midpoint.config.ConfigManager;
import me.midpoint.config.ModConfig;
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
    public static KeyBinding openGuiKey;
    public static ModConfig CONFIG;

    @Override
    public void onInitializeClient() {
        // Загрузка конфига
        ConfigManager.init();
        CONFIG = ConfigManager.getConfig();

        // Регистрация модулей
        modules.add(new AntiBanModule());
        modules.add(new SmartAutoBuyerModule());
        modules.add(new HumanizedAutoMinerModule());

        // Клавиша для открытия Mod Menu (можно через P)
        openGuiKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.midpoint.openGui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.midpoint.general"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed()) {
                // Открываем экран настроек Cloth Config
                me.shedaniel.clothconfig2.api.ConfigBuilder builder =
                    me.shedaniel.clothconfig2.api.ConfigBuilder.create()
                        .setParentScreen(client.currentScreen)
                        .setTitle(net.minecraft.text.Text.literal("Midpoint God Mode"))
                        .setSavingRunnable(ConfigManager::save);
                
                // Добавляем категории
                builder.getOrCreateCategory(net.minecraft.text.Text.literal("Модули"))
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startBooleanToggle(net.minecraft.text.Text.literal("AntiBan"), CONFIG.antiBanEnabled)
                        .setDefaultValue(true)
                        .setTooltip(net.minecraft.text.Text.literal("Имитация поведения человека"))
                        .setSaveConsumer(newValue -> CONFIG.antiBanEnabled = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startBooleanToggle(net.minecraft.text.Text.literal("SmartAutoBuyer"), CONFIG.autoBuyerEnabled)
                        .setDefaultValue(true)
                        .setTooltip(net.minecraft.text.Text.literal("Умный перекупщик"))
                        .setSaveConsumer(newValue -> CONFIG.autoBuyerEnabled = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startBooleanToggle(net.minecraft.text.Text.literal("HumanizedAutoMiner"), CONFIG.autoMinerEnabled)
                        .setDefaultValue(true)
                        .setTooltip(net.minecraft.text.Text.literal("Умный шахтёр"))
                        .setSaveConsumer(newValue -> CONFIG.autoMinerEnabled = newValue)
                        .build());

                // Категория AntiBan
                builder.getOrCreateCategory(net.minecraft.text.Text.literal("AntiBan"))
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntSlider(net.minecraft.text.Text.literal("Интенсивность"), CONFIG.antiBanIntensity, 1, 10)
                        .setDefaultValue(5)
                        .setTooltip(net.minecraft.text.Text.literal("1 - минимальная, 10 - максимальная"))
                        .setSaveConsumer(newValue -> CONFIG.antiBanIntensity = newValue)
                        .build());

                // Категория AutoBuyer
                builder.getOrCreateCategory(net.minecraft.text.Text.literal("AutoBuyer"))
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntField(net.minecraft.text.Text.literal("Лимит покупок в минуту"), CONFIG.buyLimitPerMinute)
                        .setDefaultValue(3)
                        .setMin(1)
                        .setMax(20)
                        .setTooltip(net.minecraft.text.Text.literal("Сколько покупок за минуту"))
                        .setSaveConsumer(newValue -> CONFIG.buyLimitPerMinute = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startDoubleField(net.minecraft.text.Text.literal("Минимальная прибыль %"), CONFIG.minProfitPercent)
                        .setDefaultValue(15.0)
                        .setMin(5.0)
                        .setMax(50.0)
                        .setTooltip(net.minecraft.text.Text.literal("При какой прибыли покупать"))
                        .setSaveConsumer(newValue -> CONFIG.minProfitPercent = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startDoubleField(net.minecraft.text.Text.literal("Наценка при продаже %"), CONFIG.sellMarkupPercent)
                        .setDefaultValue(20.0)
                        .setMin(5.0)
                        .setMax(100.0)
                        .setTooltip(net.minecraft.text.Text.literal("Наценка при перепродаже"))
                        .setSaveConsumer(newValue -> CONFIG.sellMarkupPercent = newValue)
                        .build());

                // Категория AutoMiner
                builder.getOrCreateCategory(net.minecraft.text.Text.literal("AutoMiner"))
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntSlider(net.minecraft.text.Text.literal("Радиус поиска"), CONFIG.miningRadius, 3, 12)
                        .setDefaultValue(6)
                        .setTooltip(net.minecraft.text.Text.literal("Радиус поиска руды"))
                        .setSaveConsumer(newValue -> CONFIG.miningRadius = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntField(net.minecraft.text.Text.literal("Длина туннеля"), CONFIG.maxTunnelLength)
                        .setDefaultValue(35)
                        .setMin(10)
                        .setMax(80)
                        .setTooltip(net.minecraft.text.Text.literal("Максимальная длина туннеля"))
                        .setSaveConsumer(newValue -> CONFIG.maxTunnelLength = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntSlider(net.minecraft.text.Text.literal("Задержка копания мин"), CONFIG.miningDelayMin, 50, 300)
                        .setDefaultValue(120)
                        .setTooltip(net.minecraft.text.Text.literal("Минимальная задержка между ударами"))
                        .setSaveConsumer(newValue -> CONFIG.miningDelayMin = newValue)
                        .build())
                    .addEntry(me.shedaniel.clothconfig2.api.ConfigEntryBuilder.create()
                        .startIntSlider(net.minecraft.text.Text.literal("Задержка копания макс"), CONFIG.miningDelayMax, 100, 500)
                        .setDefaultValue(300)
                        .setTooltip(net.minecraft.text.Text.literal("Максимальная задержка между ударами"))
                        .setSaveConsumer(newValue -> CONFIG.miningDelayMax = newValue)
                        .build());

                client.setScreen(builder.build());
            }

            if (client.player == null || client.world == null) return;
            for (BaseModule m : modules) {
                if (m.isEnabled()) m.tick();
            }
        });

        System.out.println("[Midpoint] God Mode загружен! Модулей: " + modules.size());
        System.out.println("[Midpoint] Нажми P для открытия настроек.");
    }
                              }
