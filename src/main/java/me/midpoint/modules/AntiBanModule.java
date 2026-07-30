package me.midpoint.modules;

import java.util.Random;

public class AntiBanModule extends BaseModule {
    private Random random = new Random();
    private int timer = 0;

    public AntiBanModule() {
        super("AntiBan");
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
        timer++;
        if (timer > 60 + random.nextInt(40)) {
            mc.player.setYaw(mc.player.getYaw() + (random.nextFloat() - 0.5f) * 20f);
            mc.player.setPitch(mc.player.getPitch() + (random.nextFloat() - 0.5f) * 8f);
            timer = 0;
        }
        if (random.nextInt(100) < 2) mc.player.jump();
    }
}
