package com.tiomadre.foragersinsight.common.effect;

import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class OdorousEffect extends MobEffect {
    public OdorousEffect() {
        super(MobEffectCategory.HARMFUL, 0x6b5a41);

    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            return;
        }

        entity.level().addParticle(
                FIParticleTypes.FOUL_SCENT.get(),
                entity.getRandomX(0.6D),
                entity.getRandomY() + 0.5D,
                entity.getRandomZ(0.6D),
                0.0D,
                0.0D,
                0.0D
        );
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
