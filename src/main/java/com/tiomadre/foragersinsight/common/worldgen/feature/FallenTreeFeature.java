package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.tiomadre.foragersinsight.common.block.HollowLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;

public class FallenTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final float SINGLE_MUSHROOM_LOG_CHANCE = 0.20F;
    private static final float GROWING_COLONY_AND_MUSHROOM_LOG_CHANCE = 0.01F;
    private static final float COLONY_AND_MUSHROOM_LOG_CHANCE = 0.025F;

    public FallenTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        Direction direction = random.nextBoolean() ? Direction.EAST : Direction.NORTH;
        Direction sideA = direction.getClockWise();
        Direction sideB = direction.getCounterClockWise();
        int logLength = 3 + random.nextInt(2);

        for (int i = 0; i < logLength; i++) {
            BlockPos logPos = origin.relative(direction, i);
            if (!canPlaceOnGround(level, logPos)) {
                return false;
            }
        }

        HollowLogBlock.LogTexture biomeTexture = getBiomeLogTexture(level, origin);
        BlockState fallenLogState = FIBlocks.HOLLOW_LOG.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis())
                .setValue(HollowLogBlock.LOG_TEXTURE, biomeTexture);
        List<BlockPos> logTopPositions = new ArrayList<>();
        boolean placed = false;
        for (int i = 0; i < logLength; i++) {
            BlockPos logPos = origin.relative(direction, i);
            this.setBlock(level, logPos, fallenLogState);
            placed = true;

            logTopPositions.add(logPos.above());
            placeGrassNextToLog(level, random, logPos.relative(sideA));
            placeGrassNextToLog(level, random, logPos.relative(sideB));
        }

        placeMushroomDecorations(level, random, logTopPositions);
        return placed;
    }

    private static boolean canPlaceOnGround(WorldGenLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(pos).canBeReplaced()
                && !level.getFluidState(pos).isSource()
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    private static void placeGrassNextToLog(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (random.nextFloat() > 0.45F || !canPlaceOnGround(level, pos)) {
            return;
        }

        if (random.nextFloat() < 0.35F && canPlaceDoublePlant(level, pos)) {
            level.setBlock(pos, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(pos.above(), Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
            return;
        }

        level.setBlock(pos, Blocks.GRASS.defaultBlockState(), 2);
    }

    private static void placeMushroomDecorations(WorldGenLevel level, RandomSource random, List<BlockPos> logTopPositions) {
        if (logTopPositions.isEmpty()) {
            return;
        }

        BlockState colonyState = getOptionalColonyState(random);
        if (colonyState != null && logTopPositions.size() > 1) {
            int colonyIndex = random.nextInt(logTopPositions.size());
            BlockPos colonyPos = logTopPositions.get(colonyIndex);

            List<BlockPos> remainingPositions = new ArrayList<>(logTopPositions);
            remainingPositions.remove(colonyIndex);
            BlockPos mushroomPos = remainingPositions.get(random.nextInt(remainingPositions.size()));

            BlockState growingColonyState = getGrowingColonyState(colonyState, random);
            if (growingColonyState != null && random.nextFloat() < GROWING_COLONY_AND_MUSHROOM_LOG_CHANCE) {
                boolean colonyPlaced = placeColony(level, colonyPos, growingColonyState);
                boolean mushroomPlaced = placeSmallMushroom(level, mushroomPos, random);
                if (colonyPlaced || mushroomPlaced) {
                    return;
                }
            }

            if (random.nextFloat() < COLONY_AND_MUSHROOM_LOG_CHANCE) {
                boolean colonyPlaced = placeColony(level, colonyPos, colonyState);
                boolean mushroomPlaced = placeSmallMushroom(level, mushroomPos, random);
                if (colonyPlaced || mushroomPlaced) {
                    return;
                }
            }
        }

        if (random.nextFloat() < SINGLE_MUSHROOM_LOG_CHANCE) {
            BlockPos mushroomPos = logTopPositions.get(random.nextInt(logTopPositions.size()));
            placeSmallMushroom(level, mushroomPos, random);
        }
    }

    private static boolean placeColony(WorldGenLevel level, BlockPos pos, BlockState colonyState) {
        if (!canReplaceDecoration(level, pos)) {
            return false;
        }

        boolean hasHollowLogSupport = HollowLogBlock.supportsMushroomOnFace(level.getBlockState(pos.below()), Direction.UP);
        if (colonyState.canSurvive(level, pos) || hasHollowLogSupport) {
            level.setBlock(pos, colonyState, 2);
            return true;
        }

        return false;
    }

    private static boolean placeSmallMushroom(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (!canReplaceDecoration(level, pos)) {
            return false;
        }

        boolean hasHollowLogSupport = HollowLogBlock.supportsMushroomOnFace(level.getBlockState(pos.below()), Direction.UP);
        BlockState mushroomState = random.nextBoolean() ? Blocks.RED_MUSHROOM.defaultBlockState() : Blocks.BROWN_MUSHROOM.defaultBlockState();
        if (mushroomState.canSurvive(level, pos) || hasHollowLogSupport) {
            level.setBlock(pos, mushroomState, 2);
            return true;
        }

        return false;
    }

    private static boolean canReplaceDecoration(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced() && !level.getFluidState(pos).isSource();
    }

    private static BlockState getOptionalColonyState(RandomSource random) {
        ResourceLocation colonyId = random.nextBoolean()
                ? new ResourceLocation("farmersdelight", "red_mushroom_colony")
                : new ResourceLocation("farmersdelight", "brown_mushroom_colony");
        Block colony = BuiltInRegistries.BLOCK.getOptional(colonyId).orElse(null);
        return colony != null ? colony.defaultBlockState() : null;
    }

    private static BlockState getGrowingColonyState(BlockState colonyState, RandomSource random) {
        for (var property : colonyState.getProperties()) {
            if (!(property instanceof IntegerProperty integerProperty) || !"age".equals(property.getName())) {
                continue;
            }

            int maxAge = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
            if (maxAge <= 1) {
                return null;
            }

            int growingAge = 1 + random.nextInt(maxAge - 1);
            return colonyState.setValue(integerProperty, growingAge);
        }

        return null;
    }

    private static boolean canPlaceDoublePlant(WorldGenLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(pos).canBeReplaced()
                && level.getBlockState(pos.above()).canBeReplaced()
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    private static HollowLogBlock.LogTexture getBiomeLogTexture(WorldGenLevel level, BlockPos pos) {
        return HollowLogBlock.getDominantBiomeTexture(level.getBiome(pos));
    }
}