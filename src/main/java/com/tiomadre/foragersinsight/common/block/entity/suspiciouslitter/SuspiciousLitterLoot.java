package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.random.SimpleWeightedRandomList;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SuspiciousLitterLoot {
    private static final Map<SuspiciousLitterBlock.FoliageType, SimpleWeightedRandomList<Drop>> DROP_TABLE = new EnumMap<>(SuspiciousLitterBlock.FoliageType.class);

    static {
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.OAK, buildDrops(SuspiciousLitterBlock.FoliageType.OAK));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.BIRCH, buildDrops(SuspiciousLitterBlock.FoliageType.BIRCH));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.SPRUCE, buildDrops(SuspiciousLitterBlock.FoliageType.SPRUCE));
        DROP_TABLE.put(SuspiciousLitterBlock.FoliageType.DARK_OAK, buildDrops(SuspiciousLitterBlock.FoliageType.DARK_OAK));
    }

    private SuspiciousLitterLoot() {
    }

    public static void dropLoot(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (!state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            return;
        }
        SuspiciousLitterBlock.FoliageType type = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        SimpleWeightedRandomList<Drop> drops = DROP_TABLE.getOrDefault(type, DROP_TABLE.get(SuspiciousLitterBlock.FoliageType.OAK));
        drops.getRandom(random)
                .map(wrapper -> wrapper.getData().create(random))
                .ifPresent(item -> Block.popResource(level, pos, item));
    }
    // higher weight = more common; lower weight = more rare
    private static SimpleWeightedRandomList<Drop> buildDrops(SuspiciousLitterBlock.FoliageType type) {
        SimpleWeightedRandomList.Builder<Drop> builder = SimpleWeightedRandomList.builder();
        switch (type) {
            // Forest
            case OAK -> {
                builder.add(drop((Supplier<? extends ItemLike>) Items.APPLE), 7);
                builder.add(drop((Supplier<? extends ItemLike>) Items.OAK_SAPLING), 6);
                builder.add(drop(FIBlocks.BOUNTIFUL_OAK_SAPLING), 5);
                builder.add(drop(FIItems.ROSE_HIP), 6);
            }
            //Birch Forest
            case BIRCH -> {
                builder.add(drop((Supplier<? extends ItemLike>) Items.BIRCH_SAPLING), 6);
                builder.add(drop(FIItems.ROSE_HIP), 6);
            }
            //Taiga
            case SPRUCE -> {
                builder.add(drop(FIItems.SPRUCE_TIPS), 7);
                builder.add(drop((Supplier<? extends ItemLike>) Items.SPRUCE_SAPLING), 6);
                builder.add(drop(FIBlocks.BOUNTIFUL_SPRUCE_SAPLING), 5);
                builder.add(drop((Supplier<? extends ItemLike>) Items.SWEET_BERRIES), 6);
            }
            //Dark Forest
            case DARK_OAK -> {
                builder.add(drop(FIItems.BLACK_ACORN), 7);
                builder.add(drop(FIItems.ROSE_HIP), 6);
                builder.add(drop((Supplier<? extends ItemLike>) Items.DARK_OAK_SAPLING), 6);
                builder.add(drop(FIBlocks.BOUNTIFUL_DARK_OAK_SAPLING), 5);
            }
        }
        // Can be found across all biomes
        builder.add(drop((Supplier<? extends ItemLike>) Items.STICK), 8);
        builder.add(drop((Supplier<? extends ItemLike>) ModItems.TREE_BARK.get()), 7);
        builder.add(drop(FIItems.DANDELION_ROOT), 6);
        builder.add(drop(FIItems.POPPY_SEEDS), 6);
        builder.add(drop((Supplier<? extends ItemLike>) Items.RED_MUSHROOM), 5);
        builder.add(drop((Supplier<? extends ItemLike>) Items.BROWN_MUSHROOM), 5);
        builder.add(drop(ModBlocks.RED_MUSHROOM_COLONY), 3);
        builder.add(drop(ModBlocks.BROWN_MUSHROOM_COLONY), 3);
        builder.add(drop(FIItems.BLEWIT_MUSHROOM), 1);
        return builder.build();
    }

    private static Drop drop(Supplier<? extends ItemLike> item) {
        return new Drop(item, 1, 1);
    }

    private record Drop(Supplier<? extends ItemLike> item, int min, int max) {
        public ItemStack create(RandomSource random) {
            int count = min >= max ? min : random.nextIntBetweenInclusive(min, max);
            return new ItemStack(item.get(), count);
        }
    }
}
