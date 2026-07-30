package me.midpoint.modules;

import me.midpoint.MidpointClient;
import me.midpoint.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class AutoMinerModule extends BaseModule {
    private BlockPos basePos;
    private Direction dir = Direction.SOUTH;
    private int length = 0;
    private boolean returning = false;
    private Random random = new Random();
    private int delay = 0;

    public AutoMinerModule() { super("AutoMiner"); }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
        ModConfig config = MidpointClient.CONFIG;
        if (!config.autoMinerEnabled) return;

        if (delay-- > 0) return;
        delay = 5 + random.nextInt(15);

        if (basePos == null) basePos = mc.player.getBlockPos();

        if (!hasPickaxe()) { findPickaxe(); return; }
        if (returning) { moveToBase(); return; }

        BlockPos head = mc.player.getBlockPos().offset(dir, 1);
        var block = mc.world.getBlockState(head).getBlock();
        if (block == Blocks.LAVA || block == Blocks.WATER) {
            dir = dir.rotateYClockwise();
            return;
        }

        BlockPos ore = findOre(config.miningRadius);
        if (ore != null) { mine(ore); return; }

        mine(head);
        length++;
        mc.player.setPosition(head.getX() + 0.5, head.getY(), head.getZ() + 0.5);

        if (length >= config.maxTunnelLength) returning = true;
    }

    private BlockPos findOre(int radius) {
        var pos = mc.player.getBlockPos();
        ModConfig config = MidpointClient.CONFIG;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    var p = pos.add(x, y, z);
                    Block block = mc.world.getBlockState(p).getBlock();
                    if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                        if (config.mineDiamond) return p;
                    } else if (block == Blocks.ANCIENT_DEBRIS) {
                        if (config.mineNetherite) return p;
                    } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
                        if (config.mineGold) return p;
                    } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
                        if (config.mineEmerald) return p;
                    } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
                        if (config.mineIron) return p;
                    }
                }
            }
        }
        return null;
    }

    private void mine(BlockPos p) {
        mc.interactionManager.updateBlockBreakingProgress(p, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void moveToBase() {
        var pos = mc.player.getBlockPos();
        if (pos.getX() > basePos.getX()) mc.player.setPosition(pos.getX() - 1, pos.getY(), pos.getZ());
        else if (pos.getX() < basePos.getX()) mc.player.setPosition(pos.getX() + 1, pos.getY(), pos.getZ());
        else if (pos.getZ() > basePos.getZ()) mc.player.setPosition(pos.getX(), pos.getY(), pos.getZ() - 1);
        else if (pos.getZ() < basePos.getZ()) mc.player.setPosition(pos.getX(), pos.getY(), pos.getZ() + 1);
        else if (pos.getY() > basePos.getY()) mc.player.setPosition(pos.getX(), pos.getY() - 1, pos.getZ());
        else if (pos.getY() < basePos.getY()) mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
        else returning = false;
    }

    private boolean hasPickaxe() {
        var hand = mc.player.getMainHandStack();
        return hand.getItem() == Items.DIAMOND_PICKAXE || hand.getItem() == Items.NETHERITE_PICKAXE;
    }

    private void findPickaxe() {
        for (int i = 0; i < 9; i++) {
            var s = mc.player.getInventory().getStack(i);
            if (s.getItem() == Items.DIAMOND_PICKAXE || s.getItem() == Items.NETHERITE_PICKAXE) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
        MidpointClient.CONFIG.autoMinerEnabled = false;
    }
                        }
