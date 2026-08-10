package com.tiomadre.foragersinsight.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.tiomadre.foragersinsight.common.block.HollowLogBlock;
import com.tiomadre.foragersinsight.common.block.WallMushroomBlock;
import com.tiomadre.foragersinsight.common.block.WallMushroomColonyBlock;
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
import java.util.Collections;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;

public class FallenTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final float SINGLE_MUSHROOM_LOG_CHANCE = 0.50F;
    private static final float SINGLE_WALL_MUSHROOM_LOG_CHANCE = 0.50F;
    private static final float GROWING_COLONY_AND_MUSHROOM_LOG_CHANCE = 0.30F;
    private static final float COLONY_AND_MUSHROOM_LOG_CHANCE = 0.15F;
    private static final float GROWING_WALL_COLONY_AND_MUSHROOM_LOG_CHANCE = 0.30F;
    private static final float WALL_COLONY_AND_MUSHROOM_LOG_CHANCE = 0.15F;

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
        List<BlockPos> logPositions = new ArrayList<>();
        List<BlockPos> sidePositions = new ArrayList<>();
        boolean placed = false;
        for (int i = 0; i < logLength; i++) {
            BlockPos logPos = origin.relative(direction, i);
            this.setBlock(level, logPos, fallenLogState);
            placed = true;

            logPositions.add(logPos);
            logTopPositions.add(logPos.above());
            sidePositions.add(logPos.relative(sideA));
            sidePositions.add(logPos.relative(sideB));
        }

        List<BlockPos> placedGrassPositions = new ArrayList<>();
        for (BlockPos sidePosition : sidePositions) {
            BlockPos grassPos = placeGrassNextToLog(level, random, sidePosition);
            if (grassPos != null) {
                placedGrassPositions.add(grassPos);
            }
        }

        DecorationResult topMushroomDecoration = placeMushroomDecorations(level, random, logTopPositions, sidePositions);
        DecorationResult wallMushroomDecoration = placeWallMushroomDecorations(level, random, logPositions, direction, sideA, sideB);
        replaceGrassWithWoodlandFern(level, random, placedGrassPositions);
        List<BlockPos> ghostPipePositions = new ArrayList<>();
        ghostPipePositions.addAll(topMushroomDecoration.ghostPipePositions());
        ghostPipePositions.addAll(wallMushroomDecoration.ghostPipePositions());
        placeGhostPipes(level, random, placedGrassPositions, ghostPipePositions, ghostPipePositions.size());
        return placed;
    }

    private static boolean canPlaceOnGround(WorldGenLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return level.getBlockState(pos).canBeReplaced()
                && !level.getFluidState(pos).isSource()
                && level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }

    private static BlockPos placeGrassNextToLog(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (random.nextFloat() > 0.45F || !canPlaceOnGround(level, pos)) {
            return null;
        }

        if (random.nextFloat() < 0.35F && canPlaceDoublePlant(level, pos)) {
            level.setBlock(pos, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(pos.above(), Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
            return null;
        }

        level.setBlock(pos, Blocks.GRASS.defaultBlockState(), 2);
        return pos;
    }

    private static void placeGhostPipes(WorldGenLevel level, RandomSource random, List<BlockPos> grassPositions,
                                        List<BlockPos> fallbackPositions, int replacements) {
        if (replacements <= 0) {
            return;
        }

        BlockState ghostPipeState = FIBlocks.GHOST_PIPE.get().defaultBlockState();
        int replaced = 0;
        for (BlockPos grassPos : shuffledCopy(grassPositions, random)) {
            if (replaced >= replacements) {
                return;
            }
            if (level.getBlockState(grassPos).is(Blocks.GRASS) && ghostPipeState.canSurvive(level, grassPos)) {
                level.setBlock(grassPos, ghostPipeState, 2);
                replaced++;
            }
        }

        for (BlockPos fallbackPos : shuffledCopy(fallbackPositions, random)) {
            if (replaced >= replacements) {
                return;
            }
            if (canReplaceGhostPipeFallback(level, fallbackPos) && ghostPipeState.canSurvive(level, fallbackPos)) {
                level.setBlock(fallbackPos, ghostPipeState, 2);
                replaced++;
            }
        }
    }
    private static List<BlockPos> shuffledCopy(List<BlockPos> positions, RandomSource random) {
        if (positions.isEmpty()) {
            return List.of();
        }

        List<BlockPos> shuffledPositions = new ArrayList<>(positions);
        Collections.shuffle(shuffledPositions, new java.util.Random(random.nextLong()));
        return shuffledPositions;
    }

    private static void replaceGrassWithWoodlandFern(WorldGenLevel level, RandomSource random, List<BlockPos> grassPositions) {
        if (grassPositions.isEmpty() || random.nextFloat() >= 0.40F) {
            return;
        }

        List<BlockPos> shuffledGrassPositions = new ArrayList<>(grassPositions);
        Collections.shuffle(shuffledGrassPositions, new java.util.Random(random.nextLong()));

        BlockState woodlandFernState = FIBlocks.WOODLAND_FERN.get().defaultBlockState();
        for (BlockPos grassPos : shuffledGrassPositions) {
            if (level.getBlockState(grassPos).is(Blocks.GRASS) && woodlandFernState.canSurvive(level, grassPos)) {
                level.setBlock(grassPos, woodlandFernState, 2);
                return;
            }
        }
    }

    private static DecorationResult placeMushroomDecorations(WorldGenLevel level, RandomSource random, List<BlockPos> logTopPositions,
                                                             List<BlockPos> ghostPipeFallbackPositions) {
        if (logTopPositions.isEmpty()) {
            return DecorationResult.empty();
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
                    return new DecorationResult(true, ghostPipeFallbackPositions);
                }
            }

            if (random.nextFloat() < COLONY_AND_MUSHROOM_LOG_CHANCE) {
                boolean colonyPlaced = placeColony(level, colonyPos, colonyState);
                boolean mushroomPlaced = placeSmallMushroom(level, mushroomPos, random);
                if (colonyPlaced || mushroomPlaced) {
                    return new DecorationResult(true, ghostPipeFallbackPositions);
                }
            }
        }

        if (random.nextFloat() < SINGLE_MUSHROOM_LOG_CHANCE) {
            BlockPos mushroomPos = logTopPositions.get(random.nextInt(logTopPositions.size()));
            return new DecorationResult(placeSmallMushroom(level, mushroomPos, random), List.of());
        }

        return DecorationResult.empty();
    }

    private static DecorationResult placeWallMushroomDecorations(WorldGenLevel level, RandomSource random, List<BlockPos> logPositions,
                                                                 Direction logDirection, Direction sideA, Direction sideB) {
        DecorationResult wallColonyDecoration = placeWallColonyAndMushroom(level, random, logPositions, logDirection, sideA, sideB);
        if (wallColonyDecoration.placed()) {
            return wallColonyDecoration;
        }

        if (random.nextFloat() < SINGLE_WALL_MUSHROOM_LOG_CHANCE && placeWallMushroom(level, random, logPositions, sideA, sideB)) {
            return new DecorationResult(true, List.of());
        }

        return DecorationResult.empty();
    }

    private static DecorationResult placeWallColonyAndMushroom(WorldGenLevel level, RandomSource random, List<BlockPos> logPositions,
                                                               Direction logDirection, Direction sideA, Direction sideB) {
        BlockState wallColonyState = getOptionalWallColonyState(random);
        if (wallColonyState == null || logPositions.size() <= 1) {
            return DecorationResult.empty();
        }

        boolean growingWallColony = random.nextFloat() < GROWING_WALL_COLONY_AND_MUSHROOM_LOG_CHANCE;
        boolean matureWallColony = !growingWallColony && random.nextFloat() < WALL_COLONY_AND_MUSHROOM_LOG_CHANCE;
        if (!growingWallColony && !matureWallColony) {
            return DecorationResult.empty();
        }

        Direction side = random.nextBoolean() ? sideA : sideB;
        List<BlockPos> shuffledLogPositions = shuffledCopy(logPositions, random);
        for (BlockPos logPos : shuffledLogPositions) {
            BlockState colonyState = wallColonyState.setValue(WallMushroomColonyBlock.FACING, side);
            if (growingWallColony) {
                colonyState = getGrowingColonyState(colonyState, random);
                if (colonyState == null) {
                    continue;
                }
            }

            BlockPos colonyPos = logPos.relative(side);
            if (!placeWallColony(level, colonyPos, colonyState)) {
                continue;
            }

            List<BlockPos> mushroomPositions = shuffledCopy(List.of(logPos.relative(logDirection).relative(side), logPos.relative(logDirection.getOpposite()).relative(side)), random);
            for (BlockPos mushroomPos : mushroomPositions) {
                if (placeWallMushroomAt(level, random, mushroomPos, side)) {
                    return new DecorationResult(true, mushroomPositions);
                }
            }

            return new DecorationResult(true, mushroomPositions);
        }

        return DecorationResult.empty();
    }

    private static boolean placeWallMushroom(WorldGenLevel level, RandomSource random, List<BlockPos> logPositions,
                                             Direction sideA, Direction sideB) {
        List<BlockPos> shuffledLogPositions = shuffledCopy(logPositions, random);
        boolean trySideAFirst = random.nextBoolean();
        Direction firstSide = trySideAFirst ? sideA : sideB;
        Direction secondSide = trySideAFirst ? sideB : sideA;

        for (BlockPos logPos : shuffledLogPositions) {
            if (placeWallMushroomAt(level, random, logPos.relative(firstSide), firstSide)
                    || placeWallMushroomAt(level, random, logPos.relative(secondSide), secondSide)) {
                return true;
            }
        }

        return false;
    }
    private static boolean placeWallMushroomAt(WorldGenLevel level, RandomSource random, BlockPos pos, Direction facing) {
        if (!canReplaceDecoration(level, pos)) {
            return false;
        }

        BlockState mushroomState = (random.nextBoolean()
                ? FIBlocks.WALL_RED_MUSHROOM.get()
                : FIBlocks.WALL_BROWN_MUSHROOM.get())
                .defaultBlockState()
                .setValue(WallMushroomBlock.FACING, facing);
        if (mushroomState.canSurvive(level, pos)) {
            level.setBlock(pos, mushroomState, 2);
            return true;
        }

        return false;
    }

    private static boolean placeColony(WorldGenLevel level, BlockPos pos, BlockState colonyState) {
        if (!canReplaceDecoration(level, pos)) {
            return false;
        }

        boolean hasHollowLogSupport = HollowLogBlock.MushroomPlacement(level.getBlockState(pos.below()), Direction.UP);
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

        boolean hasHollowLogSupport = HollowLogBlock.MushroomPlacement(level.getBlockState(pos.below()), Direction.UP);
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

    private static BlockState getOptionalWallColonyState(RandomSource random) {
        Block colony = random.nextBoolean()
                ? FIBlocks.WALL_RED_MUSHROOM_COLONY.get()
                : FIBlocks.WALL_BROWN_MUSHROOM_COLONY.get();
        return colony.defaultBlockState();
    }

    private static boolean placeWallColony(WorldGenLevel level, BlockPos pos, BlockState colonyState) {
        if (!canReplaceDecoration(level, pos)) {
            return false;
        }

        if (colonyState.canSurvive(level, pos)) {
            level.setBlock(pos, colonyState, 2);
            return true;
        }

        return false;
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
    private static boolean canReplaceGhostPipeFallback(WorldGenLevel level, BlockPos pos) {
        return canReplaceDecoration(level, pos) && !(level.getBlockState(pos).getBlock() instanceof DoublePlantBlock);
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

    private record DecorationResult(boolean placed, List<BlockPos> ghostPipePositions) {
        private static DecorationResult empty() {
            return new DecorationResult(false, List.of());
        }
    }
}