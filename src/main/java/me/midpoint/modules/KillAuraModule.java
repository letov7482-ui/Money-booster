package me.midpoint.modules;

import me.midpoint.MidpointClient;
import me.midpoint.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class KillAuraModule extends BaseModule {
    private Random random = new Random();
    private long lastAttackTime = 0;

    public KillAuraModule() {
        super("KillAura");
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null || mc.world == null) return;

        ModConfig config = MidpointClient.CONFIG;
        if (!config.killAuraEnabled) return;

        long now = System.currentTimeMillis();
        int delay = config.killAuraDelayMin + random.nextInt(config.killAuraDelayMax - config.killAuraDelayMin);
        if (now - lastAttackTime < delay) return;

        Box box = mc.player.getBoundingBox().expand(config.killAuraRange);
        List<Entity> targets = mc.world.getOtherEntities(mc.player, box,
            e -> e instanceof LivingEntity && e.isAlive() && e != mc.player
        );

        if (targets.isEmpty()) return;

        Entity target = targets.get(0);
        if (config.randomTarget && targets.size() > 1) {
            target = targets.get(random.nextInt(targets.size()));
        }

        if (random.nextInt(100) < config.missChance) {
            mc.player.lookAt(target.getPos().add(
                (random.nextFloat() - 0.5f) * 2.5,
                (random.nextFloat() - 0.5f) * 1.5,
                (random.nextFloat() - 0.5f) * 2.5
            ));
            return;
        }

        if (config.autoRotate) {
            rotateToEntity(target);
        }

        switch (config.killAuraMode) {
            case COMBO -> {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                if (random.nextInt(100) < 15) {
                    try { Thread.sleep(50 + random.nextInt(80)); } catch (Exception ignored) {}
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
            case CRITS -> {
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                    try { Thread.sleep(30 + random.nextInt(50)); } catch (Exception ignored) {}
                }
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            case ALL -> {
                if (random.nextInt(100) < 30) {
                    if (mc.player.isOnGround()) mc.player.jump();
                    try { Thread.sleep(30 + random.nextInt(50)); } catch (Exception ignored) {}
                }
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                if (random.nextInt(100) < 10) {
                    try { Thread.sleep(50 + random.nextInt(80)); } catch (Exception ignored) {}
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }

        lastAttackTime = now;
    }

    private void rotateToEntity(Entity target) {
        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        Vec3d playerPos = mc.player.getEyePos();

        double diffX = targetPos.x - playerPos.x;
        double diffY = targetPos.y - playerPos.y;
        double diffZ = targetPos.z - playerPos.z;

        double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float targetYaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float targetPitch = (float) (-Math.atan2(diffY, distance) * 180.0 / Math.PI);

        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        float step = 25.0f + random.nextFloat() * 15f;
        mc.player.setYaw(currentYaw + Math.max(-step, Math.min(step, targetYaw - currentYaw)));
        mc.player.setPitch(currentPitch + Math.max(-step, Math.min(step, targetPitch - currentPitch)));
    }
        }
