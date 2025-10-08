package com.tiomadre.foragersinsight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class DiffuserBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public DiffuserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double offsetX = (random.nextDouble() - 0.5D) * 0.1D;
        double offsetZ = (random.nextDouble() - 0.5D) * 0.1D;

        // billowing smoke plume
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, offsetX * 0.6D, 0.07D, offsetZ * 0.6D);
        } else {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.05D, z, offsetX, 0.02D, offsetZ);
        }

        // subtle scented trail that can be tinted when scents are implemented
        Vec3 scentColor = getScentColor(state);
        Vector3f tint = new Vector3f((float) scentColor.x(), (float) scentColor.y(), (float) scentColor.z());
        ParticleOptions scentedParticle = new DustColorTransitionOptions(tint, tint, 0.75F);
        level.addParticle(scentedParticle, x, y + 0.15D, z, offsetX * 1.5D, 0.01D, offsetZ * 1.5D);
    }

    protected @NotNull Vec3 getScentColor(@NotNull BlockState state) {
        return new Vec3(0.8D, 0.8D, 0.8D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}