package me.midpoint.modules;

import me.midpoint.MidpointClient;
import me.midpoint.config.ModConfig;
import java.util.Random;

public class AntiBanModule extends BaseModule {
    private Random random = new Random();
    private int timer = 0;
    private int sneakTimer = 0;
    private int jumpTimer = 0;

    public AntiBanModule() {
        super("AntiBan");
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
        ModConfig config = MidpointClient.CONFIG;
        if (!config.antiBanEnabled) return;

        timer++;
        sneakTimer++;
        jumpTimer++;

        int intensity = config.antiBanIntensity;
        int delay = Math.max(20, 120 - intensity * 10);

        if (config.antiBanLook && timer > delay + random.nextInt(30)) {
            mc.player.setYaw(mc.player.getYaw() + (random.nextFloat() - 0.5f) * 20f);
            mc.player.setPitch(mc.player.getPitch() + (random.nextFloat() - 0.5f) * 8f);
            timer = 0;
        }

        if (config.antiBanJump && jumpTimer > 80 + random.nextInt(120)) {
            if (mc.player.isOnGround() && random.nextInt(100) < 3) {
                mc.player.jump();
                jumpTimer = 0;
            }
        }

        if (config.antiBanSneak && sneakTimer > 100 + random.nextInt(150)) {
            if (random.nextInt(100) < 2) {
                mc.options.sneakKey.setPressed(true);
                try { Thread.sleep(50 + random.nextInt(150)); } catch (Exception ignored) {}
                mc.options.sneakKey.setPressed(false);
                sneakTimer = 0;
            }
        }
    }
}
