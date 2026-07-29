package me.midpoint.modules;

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
    private final int MAX = 30;
    private boolean returning = false;
    private Random r = new Random();
    private int delay = 0;

    public AutoMinerModule() { super("AutoMiner"); }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
        if (delay-- > 0) return;
        delay = 5 + r.nextInt(15);

        if (basePos == null) basePos = mc.player.getBlockPos();

        if (!hasPickaxe()) { findPickaxe(); return; }
        if (returning) { moveToBase(); return; }

        BlockPos head = mc.player.getBlockPos().offset(dir, 1);
        var block = mc.world.getBlockState(head).getBlock();
        if (block == Blocks.LAVA || block == Blocks.WATER) { return; }

        BlockPos ore = findOre();
        if (ore != null) { mine(ore); return; }

        mine(head);
        length++;
        mc.player.setPosition(head.getX() + 0.5, head.getY(), head.getZ() + 0.5);
        if (length >= MAX) returning = true;
    }

    private BlockPos findOre() {
        var pos = mc.player.getBlockPos();
        for (int x = -5; x <= 5; x++)
            for (int y = -5; y <= 5; y++)
                for (int z = -5; z <= 5; z++) {
                    var p = pos.add(x, y, z);
                    var b = mc.world.getBlockState(p).getBlock();
                    if (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE ||
                        b == Blocks.ANCIENT_DEBRIS || b == Blocks.GOLD_ORE) return p;
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
        enabled = false;
    }
}
