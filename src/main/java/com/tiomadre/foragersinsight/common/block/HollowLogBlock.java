package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class HollowLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<LogTexture> LOG_TEXTURE = EnumProperty.create("log_texture", LogTexture.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HollowLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, net.minecraft.core.Direction.Axis.Y)
                .setValue(LOG_TEXTURE, LogTexture.OAK)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOG_TEXTURE, WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState baseState = super.getStateForPlacement(context);
        if (baseState == null) {
            return null;
        }

        boolean isWaterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return baseState
                .setValue(LOG_TEXTURE, getDominantBiomeTexture(context.getLevel().getBiome(context.getClickedPos())))
                .setValue(WATERLOGGED, isWaterlogged);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull net.minecraft.core.BlockPos currentPos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static boolean MushroomPlacement(BlockState state, Direction face) {
        if (!(state.getBlock() instanceof HollowLogBlock)) {
            return false;
        }

        Direction.Axis axis = state.getValue(AXIS);
        if (axis == Direction.Axis.Y) {
            return face == Direction.UP;
        }

        return face.getAxis() != axis;
    }
    public static boolean canSupportMushroomAt(LevelReader level, BlockPos mushroomPos) {
        if (MushroomPlacement(level.getBlockState(mushroomPos.below()), Direction.UP)) {
            return true;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState supportState = level.getBlockState(mushroomPos.relative(direction));
            if (MushroomPlacement(supportState, direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }


    public static LogTexture getDominantBiomeTexture(Holder<Biome> biome) {
        if (biome.is(FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER)) {
            return LogTexture.BIRCH;
        }
        if (biome.is(FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER)) {
            return LogTexture.SPRUCE;
        }
        if (biome.is(FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER)) {
            return LogTexture.DARK_OAK;
        }
        biome.is(FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER);
        return LogTexture.OAK;
    }

    public enum LogTexture implements StringRepresentable {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak"),
        MANGROVE("mangrove"),
        CHERRY("cherry");

        private final String name;

        LogTexture(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}