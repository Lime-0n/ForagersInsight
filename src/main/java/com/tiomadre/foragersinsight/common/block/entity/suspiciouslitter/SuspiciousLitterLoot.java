package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
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
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Supplier;

public final class SuspiciousLitterLoot {

    private static final Map<SuspiciousLitterBlock.FoliageType, List<WeightedDrop>> DROP_TABLE =
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
        return chooseLoot(state, random, 0);
    }

    public static ItemStack chooseLoot(BlockState state, RandomSource random, int luckOfTheTreesLevel) {
        if (!state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            return ItemStack.EMPTY;
        }

        SuspiciousLitterBlock.FoliageType type = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        List<WeightedDrop> drops =
                DROP_TABLE.getOrDefault(type, DROP_TABLE.get(SuspiciousLitterBlock.FoliageType.OAK));

        SimpleWeightedRandomList<Drop> weightedDrops = buildWeightedList(drops, luckOfTheTreesLevel);

        return weightedDrops.getRandom(random)
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

    private static void addDrop(List<WeightedDrop> drops,
                                Map<ItemLike, Integer> weights,
                                Supplier<? extends ItemLike> item,
                                int weight) {
        drops.add(new WeightedDrop(drop(item), weight));
        weights.put(item.get().asItem(), weight);
    }

    private static SimpleWeightedRandomList<Drop> buildWeightedList(List<WeightedDrop> drops, int luckLevel) {
        SimpleWeightedRandomList.Builder<Drop> builder = SimpleWeightedRandomList.builder();
        int medianWeight = calculateMedianWeight(drops);

        for (WeightedDrop drop : drops) {
            int adjustedWeight = adjustWeight(drop.weight(), luckLevel, medianWeight);
            if (adjustedWeight > 0) {
                builder.add(drop.drop(), adjustedWeight);
            }
        }

        return builder.build();
    }

    private static int adjustWeight(int baseWeight, int luckLevel, int medianWeight) {
        if (luckLevel <= 0) {
            return baseWeight;
        }
        if (baseWeight < medianWeight) {
            return baseWeight + luckLevel;
        }
        if (baseWeight > medianWeight) {
            return Math.max(1, baseWeight - luckLevel);
        }
        return baseWeight;
    }

    private static int calculateMedianWeight(List<WeightedDrop> drops) {
        if (drops.isEmpty()) {
            return 1;
        }
        List<Integer> weights = new ArrayList<>();
        for (WeightedDrop drop : drops) {
            weights.add(drop.weight());
        }
        Collections.sort(weights);
        int middle = weights.size() / 2;
        return weights.get(middle);
    }

    // higher weight = more common; lower weight = more rare
    private static List<WeightedDrop> buildDrops(SuspiciousLitterBlock.FoliageType type) {
        List<WeightedDrop> drops = new ArrayList<>();
        Map<ItemLike, Integer> weights = new java.util.HashMap<>();

        switch (type) {
            // Forest
            case OAK -> {
                addDrop(drops, weights, () -> Items.OAK_LEAVES, 8);
                addDrop(drops, weights, () -> Items.APPLE, 7);
                addDrop(drops, weights, FIItems.ROSE_HIP, 6);
            }

            // Birch Forest
            case BIRCH -> {
                addDrop(drops, weights, () -> Items.BIRCH_LEAVES, 8);
                addDrop(drops, weights, FIItems.ROSE_HIP, 6);
            }

            // Taiga
            case SPRUCE -> {
                addDrop(drops, weights, () -> Items.SPRUCE_LEAVES, 8);
                addDrop(drops, weights, FIItems.SPRUCE_TIPS, 7);
                addDrop(drops, weights, () -> Items.SWEET_BERRIES, 6);
            }

            // Dark Forest
            case DARK_OAK -> {
                addDrop(drops, weights, () -> Items.DARK_OAK_LEAVES, 8);
                addDrop(drops, weights, FIItems.BLACK_ACORN, 7);
                addDrop(drops, weights, FIItems.ROSE_HIP, 6);
            }
        }

        // Can be found across all biomes
        addDrop(drops, weights, ModItems.TREE_BARK, 7);
        addDrop(drops, weights, FIItems.DANDELION_ROOT, 6);
        addDrop(drops, weights, FIItems.POPPY_SEEDS, 6);
        addDrop(drops, weights, () -> Items.RED_MUSHROOM, 5);
        addDrop(drops, weights, () -> Items.BROWN_MUSHROOM, 5);
        addDrop(drops, weights, FIItems.BLEWIT_MUSHROOM, 2);

        DROP_WEIGHTS.put(type, weights);
        return drops;
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

    private record WeightedDrop(Drop drop, int weight) {
    }
}
