package me.midpoint.modules;

import java.util.Random;

public class AntiBanModule extends BaseModule {
    private Random random = new Random();
    private int actionTimer = 0;
    private int sneakTimer = 0;
    private int jumpTimer = 0;
    private int lookTimer = 0;

    public AntiBanModule() {
        super("AntiBan", -1);
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;

        actionTimer++;
        sneakTimer++;
        jumpTimer++;
        lookTimer++;

        // Случайные прыжки
        if (jumpTimer > random.nextInt(60) + 40) {
            mc.options.jumpKey.setPressed(true);
            jumpTimer = 0;
            if (random.nextBoolean()) {
                try { Thread.sleep(100 + random.nextInt(150)); } catch (InterruptedException ignored) {}
            }
            mc.options.jumpKey.setPressed(false);
        }

        // Случайные приседания
        if (sneakTimer > random.nextInt(120) + 60) {
            mc.options.sneakKey.setPressed(true);
            sneakTimer = 0;
            try { Thread.sleep(200 + random.nextInt(300)); } catch (InterruptedException ignored) {}
            mc.options.sneakKey.setPressed(false);
        }

        // Плавные повороты
        if (lookTimer > random.nextInt(80) + 40) {
            mc.player.setYaw(mc.player.getYaw() + (random.nextFloat() - 0.5f) * 30f);
            mc.player.setPitch(mc.player.getPitch() + (random.nextFloat() - 0.5f) * 10f);
            lookTimer = 0;
        }

        // Остановки
        if (actionTimer > random.nextInt(200) + 100) {
            mc.options.forwardKey.setPressed(false);
            mc.options.backKey.setPressed(false);
            mc.options.leftKey.setPressed(false);
            mc.options.rightKey.setPressed(false);
            actionTimer = 0;
            try { Thread.sleep(500 + random.nextInt(1500)); } catch (InterruptedException ignored) {}
        }
    }
}
