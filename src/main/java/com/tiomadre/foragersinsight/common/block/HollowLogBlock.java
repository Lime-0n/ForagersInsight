package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.data.server.tags.FITags;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class HollowLogBlock extends RotatedPillarBlock {
    public static final EnumProperty<LogTexture> LOG_TEXTURE = EnumProperty.create("log_texture", LogTexture.class);

    public HollowLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, net.minecraft.core.Direction.Axis.Y)
                .setValue(LOG_TEXTURE, LogTexture.OAK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOG_TEXTURE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState baseState = super.getStateForPlacement(context);
        if (baseState == null) {
            return null;
        }

        return baseState.setValue(LOG_TEXTURE, getDominantBiomeTexture(context.getLevel().getBiome(context.getClickedPos())));
    }

    public static boolean supportsMushroomOnFace(BlockState state, Direction face) {
        if (!(state.getBlock() instanceof HollowLogBlock)) {
            return false;
        }

        Direction.Axis axis = state.getValue(AXIS);
        if (axis == Direction.Axis.Y) {
            return face == Direction.UP;
        }

        return face.getAxis() != axis;
    }

    public static LogTexture getDominantBiomeTexture(Holder<Biome> biome) {
        if (biome.is(FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER)) {
            return LogTexture.BIRCH;
        }
        if (biome.is(FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER)) {
            return LogTexture.SPRUCE;
        }
        if (biome.is(FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER) || biome.is(FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER)) {
            return LogTexture.DARK_OAK;
        }
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