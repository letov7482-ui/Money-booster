package me.midpoint.modules;

import net.minecraft.client.MinecraftClient;

public abstract class BaseModule {
    protected MinecraftClient mc = MinecraftClient.getInstance();
    public String name;
    public boolean enabled;

    public BaseModule(String name) {
        this.name = name;
        this.enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void tick() {}
}
