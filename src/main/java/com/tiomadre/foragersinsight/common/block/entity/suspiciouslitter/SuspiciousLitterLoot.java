package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.OptionalInt;

public final class SuspiciousLitterLoot {

    private static final Map<SuspiciousLitterBlock.FoliageType, SimpleWeightedRandomList<Drop>> DROP_TABLE =
            new EnumMap<>(SuspiciousLitterBlock.FoliageType.class);
    private static final Map<SuspiciousLitterBlock.FoliageType, Map<ItemLike, Integer>> DROP_WEIGHTS =
            new EnumMap<>(SuspiciousLitterBlock.FoliageType.class);

    static {
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.OAK, buildDrops(SuspiciousLitterBlock.FoliageType.OAK));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.BIRCH, buildDrops(SuspiciousLitterBlock.FoliageType.BIRCH));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.SPRUCE, buildDrops(SuspiciousLitterBlock.FoliageType.SPRUCE));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.DARK_OAK, buildDrops(SuspiciousLitterBlock.FoliageType.DARK_OAK));
    }

    private SuspiciousLitterLoot() {
    }

    public static ItemStack chooseLoot(BlockState state, RandomSource random) {
        if (!state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            return ItemStack.EMPTY;
        }

        SuspiciousLitterBlock.FoliageType type = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        SimpleWeightedRandomList<Drop> drops =
                DROP_TABLE.getOrDefault(type, DROP_TABLE.get(SuspiciousLitterBlock.FoliageType.OAK));

        return drops.getRandom(random)
                .map(wrapper -> wrapper.getData().create(random))
                .orElse(ItemStack.EMPTY);
    }

    public static OptionalInt getDropWeight(BlockState state, ItemStack stack) {
        if (stack.isEmpty() || !state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            return OptionalInt.empty();
        }

        SuspiciousLitterBlock.FoliageType type = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        Map<ItemLike, Integer> weights = DROP_WEIGHTS.get(type);
        if (weights == null) {
            weights = DROP_WEIGHTS.get(SuspiciousLitterBlock.FoliageType.OAK);
        }
        if (weights == null) {
            return OptionalInt.empty();
        }

        Integer weight = weights.get(stack.getItem());
        return weight == null ? OptionalInt.empty() : OptionalInt.of(weight);
    }

    public static void dropLoot(ServerLevel level, BlockPos pos, BlockState state, ItemStack itemStack) {
        ItemStack stack = itemStack.isEmpty() ? chooseLoot(state, level.random) : itemStack;
        if (!stack.isEmpty()) {
            Block.popResource(level, pos, stack.copy());
        }
    }
    // higher weight = more common; lower weight = more rare
    private static SimpleWeightedRandomList<Drop> buildDrops(SuspiciousLitterBlock.FoliageType type) {
        SimpleWeightedRandomList.Builder<Drop> builder = SimpleWeightedRandomList.builder();
        Map<ItemLike, Integer> weights = new java.util.HashMap<>();

        java.util.function.BiConsumer<Supplier<? extends ItemLike>, Integer> add = (item, weight) -> {
            builder.add(drop(item), weight);
            weights.put(item.get().asItem(), weight);
        };

        switch (type) {
            // Forest
            case OAK -> {
                add.accept(() -> Items.APPLE, 7);
                add.accept(() -> Items.OAK_SAPLING, 6);
                add.accept(FIBlocks.BOUNTIFUL_OAK_SAPLING, 5);
                add.accept(FIItems.ROSE_HIP, 6);
            }

            // Birch Forest
            case BIRCH -> {
                add.accept(() -> Items.BIRCH_SAPLING, 6);
                add.accept(FIItems.ROSE_HIP, 6);
            }

            // Taiga
            case SPRUCE -> {
                add.accept(FIItems.SPRUCE_TIPS, 7);
                add.accept(() -> Items.SPRUCE_SAPLING, 6);
                add.accept(FIBlocks.BOUNTIFUL_SPRUCE_SAPLING, 5);
                add.accept(() -> Items.SWEET_BERRIES, 6);
            }

            // Dark Forest
            case DARK_OAK -> {
                add.accept(FIItems.BLACK_ACORN, 7);
                add.accept(FIItems.ROSE_HIP, 6);
                add.accept(() -> Items.DARK_OAK_SAPLING, 6);
                add.accept(FIBlocks.BOUNTIFUL_DARK_OAK_SAPLING, 5);
            }
        }

        // Can be found across all biomes
        add.accept(() -> Items.STICK, 8);
        add.accept(ModItems.TREE_BARK, 7);
        add.accept(FIItems.DANDELION_ROOT, 6);
        add.accept(FIItems.POPPY_SEEDS, 6);
        add.accept(() -> Items.RED_MUSHROOM, 5);
        add.accept(() -> Items.BROWN_MUSHROOM, 5);
        add.accept(ModBlocks.RED_MUSHROOM_COLONY, 3);
        add.accept(ModBlocks.BROWN_MUSHROOM_COLONY, 3);
        add.accept(FIItems.BLEWIT_MUSHROOM, 2);

        DROP_WEIGHTS.put(type, weights);
        return builder.build();
    }

    private static Drop drop(Supplier<? extends ItemLike> item) {
        return new Drop(item, 1, 1);
    }

    private record Drop(Supplier<? extends ItemLike> item, int min, int max) {
        public ItemStack create(RandomSource random) {
            int count = (min >= max) ? min : random.nextIntBetweenInclusive(min, max);
            return new ItemStack(item.get(), count);
        }
    }
}