package com.tiomadre.foragersinsight.common.block;

import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class SapTrapBlock extends FoliageMatBlock {
    private static final int ROOT_DURATION = 60;

    public SapTrapBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.1F)
                .sound(SoundType.SLIME_BLOCK)
                .noOcclusion()
                .mapColor(MapColor.COLOR_BROWN)
                .isValidSpawn((state, level, pos, entityType) -> false));
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
            level.destroyBlock(pos, false, entity);
            livingEntity.addEffect(new MobEffectInstance(FIMobEffects.ROOTED.get(), ROOT_DURATION, 0, false, false, false));
        }

        super.stepOn(level, pos, state, entity);
    }
}