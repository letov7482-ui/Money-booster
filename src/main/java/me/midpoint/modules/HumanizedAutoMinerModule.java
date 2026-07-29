package me.midpoint.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HumanizedAutoMinerModule extends BaseModule {
    private boolean isMining = false;
    private BlockPos basePos;
    private Direction currentDirection = Direction.SOUTH;
    private int tunnelLength = 0;
    private final int MAX_TUNNEL_LENGTH = 35 + new Random().nextInt(20);
    private int currentTunnelNumber = 0;
    private boolean isReturning = false;

    private Queue<BlockPos> miningQueue = new ConcurrentLinkedQueue<>();
    private Map<Block, Integer> orePriority = new HashMap<>();

    private Random random = new Random();
    private int humanDelay = 0;
    private int actionCounter = 0;

    public HumanizedAutoMinerModule() {
        super("HumanizedAutoMiner", -1);
        initOrePriority();
    }

    private void initOrePriority() {
        orePriority.put(Blocks.ANCIENT_DEBRIS, 100);
        orePriority.put(Blocks.DIAMOND_ORE, 90);
        orePriority.put(Blocks.DEEPSLATE_DIAMOND_ORE, 90);
        orePriority.put(Blocks.EMERALD_ORE, 80);
        orePriority.put(Blocks.GOLD_ORE, 70);
        orePriority.put(Blocks.NETHER_GOLD_ORE, 70);
        orePriority.put(Blocks.IRON_ORE, 60);
        orePriority.put(Blocks.COPPER_ORE, 50);
        orePriority.put(Blocks.REDSTONE_ORE, 40);
        orePriority.put(Blocks.LAPIS_ORE, 30);
        orePriority.put(Blocks.NETHER_QUARTZ_ORE, 20);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            basePos = mc.player.getBlockPos();
            isMining = true;
            isReturning = false;
            tunnelLength = 0;
            currentTunnelNumber = 0;
            humanDelay = random.nextInt(100) + 50;
            System.out.println("[AutoMiner] Запущен! База: " + basePos);
        }
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null || mc.world == null) return;

        humanDelay--;
        if (humanDelay > 0) return;
        humanDelay = random.nextInt(80) + 40;

        if (!hasValidPickaxe()) {
            findNewPickaxe();
            return;
        }

        if (mc.player.getHealth() < 6.0f) {
            useHealingItem();
            return;
        }

        if (isMining) {
            scanForOres();

            if (!miningQueue.isEmpty()) {
                mineNextOre();
                return;
            }

            if (isReturning) {
                if (mc.player.getBlockPos().equals(basePos)) {
                    isReturning = false;
                    tunnelLength = 0;
                    currentTunnelNumber++;
                    currentDirection = currentDirection.rotateYClockwise();
                    System.out.println("[AutoMiner] Новая ветка #" + currentTunnelNumber);
                } else {
                    moveToBase();
                }
                return;
            }

            if (tunnelLength < MAX_TUNNEL_LENGTH) {
                mineTunnel();
            } else {
                isReturning = true;
                System.out.println("[AutoMiner] Туннель готов, возвращаюсь...");
            }
        }
    }

    private void scanForOres() {
        BlockPos playerPos = mc.player.getBlockPos();
        int radius = 6 + random.nextInt(4);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (orePriority.containsKey(block) && !miningQueue.contains(pos)) {
                        miningQueue.add(pos);
                        System.out.println("[AutoMiner] Найдена руда: " + block.getName().getString());
                    }
                }
            }
        }

        sortQueueByPriority();
    }

    private void sortQueueByPriority() {
        List<BlockPos> sorted = new ArrayList<>(miningQueue);
        sorted.sort((pos1, pos2) -> {
            Block b1 = mc.world.getBlockState(pos1).getBlock();
            Block b2 = mc.world.getBlockState(pos2).getBlock();
            return orePriority.getOrDefault(b2, 0) - orePriority.getOrDefault(b1, 0);
        });
        miningQueue.clear();
        miningQueue.addAll(sorted);
    }

    private void mineTunnel() {
        BlockPos headPos = mc.player.getBlockPos().offset(currentDirection, 1);
        Block block = mc.world.getBlockState(headPos).getBlock();

        if (block == Blocks.LAVA || block == Blocks.WATER) {
            handleObstacle(headPos);
            return;
        }

        mineBlock(headPos);
        checkSideBlocks(headPos);

        actionCounter++;
        if (actionCounter % (random.nextInt(4) + 2) == 0) {
            mc.player.setYaw(mc.player.getYaw() + random.nextFloat() * 15 - 7.5f);
        }

        tunnelLength++;
        Vec3d newPos = new Vec3d(
            headPos.getX() + 0.5,
            headPos.getY(),
            headPos.getZ() + 0.5
        );
        mc.player.setPosition(newPos);
    }

    private void checkSideBlocks(BlockPos pos) {
        for (Direction dir : new Direction[]{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST}) {
            BlockPos sidePos = pos.offset(dir);
            Block block = mc.world.getBlockState(sidePos).getBlock();
            if (orePriority.containsKey(block) && !miningQueue.contains(sidePos)) {
                miningQueue.add(sidePos);
                System.out.println("[AutoMiner] Руда сбоку: " + block.getName().getString());
            }
        }
    }

    private void mineNextOre() {
        BlockPos pos = miningQueue.poll();
        if (pos == null) return;
        useBestPickaxeForOre(pos);
        mineBlock(pos);
    }

    private void useBestPickaxeForOre(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.ANCIENT_DEBRIS || block == Blocks.DIAMOND_ORE) {
            selectPickaxeWithFortune();
        }
    }

    private void selectPickaxeWithFortune() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.DIAMOND_PICKAXE || stack.getItem() == Items.NETHERITE_PICKAXE) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    private void mineBlock(BlockPos pos) {
        mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);

        int delay = 120 + random.nextInt(180);
        try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
    }

    private void handleObstacle(BlockPos pos) {
        BlockPos above = pos.up();
        if (mc.world.getBlockState(above).isAir()) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, mc.world, above, Direction.DOWN);
        }
        BlockPos sidePos = pos.offset(currentDirection.rotateYClockwise());
        if (mc.world.getBlockState(sidePos).isAir()) {
            mineBlock(sidePos);
        } else {
            BlockPos otherSide = pos.offset(currentDirection.rotateYCounterclockwise());
            if (mc.world.getBlockState(otherSide).isAir()) {
                mineBlock(otherSide);
            }
        }
    }

    private boolean hasValidPickaxe() {
        ItemStack mainHand = mc.player.getMainHandStack();
        return mainHand.getItem() == Items.DIAMOND_PICKAXE ||
               mainHand.getItem() == Items.NETHERITE_PICKAXE ||
               mainHand.getItem() == Items.IRON_PICKAXE;
    }

    private void findNewPickaxe() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.DIAMOND_PICKAXE ||
                stack.getItem() == Items.NETHERITE_PICKAXE ||
                stack.getItem() == Items.IRON_PICKAXE) {
                mc.player.getInventory().selectedSlot = i;
                System.out.println("[AutoMiner] Найдена новая кирка в слоте " + i);
                return;
            }
        }
        System.out.println("[AutoMiner] Нет кирки! Останавливаюсь.");
        this.enabled = false;
    }

    private void moveToBase() {
        BlockPos pos = mc.player.getBlockPos();
        if (pos.getX() > basePos.getX()) {
            mc.player.setPosition(pos.getX() - 1, pos.getY(), pos.getZ());
        } else if (pos.getX() < basePos.getX()) {
            mc.player.setPosition(pos.getX() + 1, pos.getY(), pos.getZ());
        } else if (pos.getZ() > basePos.getZ()) {
            mc.player.setPosition(pos.getX(), pos.getY(), pos.getZ() - 1);
        } else if (pos.getZ() < basePos.getZ()) {
            mc.player.setPosition(pos.getX(), pos.getY(), pos.getZ() + 1);
        }
        if (pos.getY() > basePos.getY()) {
            mc.player.setPosition(pos.getX(), pos.getY() - 1, pos.getZ());
        } else if (pos.getY() < basePos.getY()) {
            mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
        }
    }

    private void useHealingItem() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                mc.player.getInventory().selectedSlot = i;
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                return;
            }
        }
    }

    @Override
    public void onDisable() {
        isMining = false;
        miningQueue.clear();
        System.out.println("[AutoMiner] Остановлен.");
    }
                                                }
