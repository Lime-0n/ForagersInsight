package com.tiomadre.foragersinsight.core.mixin;

import com.tiomadre.foragersinsight.common.worldgen.FIConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.RichSoilBlock;

import java.util.Optional;

@Mixin(RichSoilBlock.class)
public class RichSoilTreeMixin {
    @Unique
    private static final int LILAC_TREE_CHANCE = 8;

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void foragersInsight$growLilacTree(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (random.nextInt(LILAC_TREE_CHANCE) != 0) {
            return;
        }

        BlockPos lilacPos = pos.above();
        BlockState lowerLilacState = level.getBlockState(lilacPos);
        if (!lowerLilacState.is(Blocks.LILAC) || lowerLilacState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        BlockPos upperLilacPos = lilacPos.above();
        BlockState upperLilacState = level.getBlockState(upperLilacPos);
        if (!upperLilacState.is(Blocks.LILAC)) {
            return;
        }

        Optional<ConfiguredFeature<?, ?>> configuredFeature = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(FIConfiguredFeatures.LILAC_TREE_KEY)
                .map(Holder.Reference::value);
        if (configuredFeature.isEmpty()) {
            return;
        }

        level.setBlock(pos, Blocks.ROOTED_DIRT.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(lilacPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(upperLilacPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        boolean placed = configuredFeature.get().place(level, level.getChunkSource().getGenerator(), random, lilacPos);
        if (!placed) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
            level.setBlock(lilacPos, lowerLilacState, Block.UPDATE_ALL);
            level.setBlock(upperLilacPos, upperLilacState, Block.UPDATE_ALL);
        }
    }
}