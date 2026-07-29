package me.midpoint.modules;

import me.midpoint.config.ConfigManager;
import net.minecraft.client.MinecraftClient;

public abstract class BaseModule {
    protected MinecraftClient mc = MinecraftClient.getInstance();
    public String name;
    public boolean enabled;
    public int keyBind;

    public BaseModule(String name, int keyBind) {
        this.name = name;
        this.keyBind = keyBind;
        this.enabled = true;
    }

    // Проверка включён ли модуль (с учётом настроек)
    public boolean isEnabled() {
        return enabled && isConfigEnabled();
    }

    // Каждый модуль переопределяет этот метод
    protected abstract boolean isConfigEnabled();

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void tick() {}
    public void render() {}
}
