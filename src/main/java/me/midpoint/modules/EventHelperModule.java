package me.midpoint.modules;

import me.midpoint.MidpointClient;
import me.midpoint.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EventHelperModule extends BaseModule {
    public EventHelperModule() { super("EventHelper"); }

    @Override
    public void tick() {
        if (!enabled || mc.player == null || mc.world == null) return;
        ModConfig config = MidpointClient.CONFIG;
        if (!config.eventHelperEnabled) return;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == mc.player) continue;
            double distance = mc.player.distanceTo(entity);
            if (distance > config.eventHelperRange) continue;

            // Мистики
            if (config.showMystic && entity.hasCustomName()) {
                String name = entity.getCustomName().getString();
                if (name.contains("Мистик") || name.contains("Mystic") || name.contains("Босс")) {
                    String label = "§d★ " + name + (config.showDistance ? " §f" + (int)distance + "м" : "");
                    System.out.println("[EventHelper] " + label);
                }
            }

            // Игроки
            if (config.showEvents && entity instanceof PlayerEntity) {
                if (distance < 15) {
                    String label = "§e⚔ " + entity.getName().getString() + (config.showDistance ? " §f" + (int)distance + "м" : "");
                    System.out.println("[EventHelper] " + label);
                }
            }
        }
    }
}
