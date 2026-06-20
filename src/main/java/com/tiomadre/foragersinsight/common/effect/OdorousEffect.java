package com.tiomadre.foragersinsight.common.effect;

import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;


public class OdorousEffect extends MobEffect {
    private static final int FLOWER_EXTRA_DRAIN_INTERVAL = 60;
    private static final int WATER_EXTRA_DRAIN_INTERVAL = 40;
    private static final int BLOOM_EXTRA_DRAIN_INTERVAL = 20;

    public OdorousEffect() {
        super(MobEffectCategory.HARMFUL, 0x6b5a41);

    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance odorous = entity.getEffect(this);
        if (odorous == null) {
            return;
        }

        int drainInterval = getCounterbalanceInterval(entity);
        if (drainInterval <= 0 || odorous.getDuration() % drainInterval != 0) {
            return;
        }

        int newDuration = odorous.getDuration() - 1;
        entity.removeEffect(this);
        if (newDuration > 0) {
            entity.addEffect(new MobEffectInstance(
                    this,
                    newDuration,
                    odorous.getAmplifier(),
                    odorous.isAmbient(),
                    odorous.isVisible(),
                    odorous.showIcon()
            ));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    private static int getCounterbalanceInterval(LivingEntity entity) {
        if (entity.hasEffect(FIMobEffects.BLOOM.get())) {
            return BLOOM_EXTRA_DRAIN_INTERVAL;
        }
        if (entity.isInWaterOrBubble()) {
            return WATER_EXTRA_DRAIN_INTERVAL;
        }
        if (isInFlowers(entity)) {
            return FLOWER_EXTRA_DRAIN_INTERVAL;
        }
        return 0;
    }

    private static boolean isInFlowers(LivingEntity entity) {
        BlockState feetState = entity.level().getBlockState(entity.blockPosition());
        BlockState eyeState = entity.level().getBlockState(entity.blockPosition().above());
        return feetState.is(BlockTags.FLOWERS) || eyeState.is(BlockTags.FLOWERS);
    }
}