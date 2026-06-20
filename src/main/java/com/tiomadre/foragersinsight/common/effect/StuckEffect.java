package com.tiomadre.foragersinsight.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class StuckEffect extends MobEffect {

    public StuckEffect() {
        super(MobEffectCategory.HARMFUL, 0xb87830);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        stopMovementActions(entity);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public static void stopMovementActions(@NotNull LivingEntity entity) {
        entity.setDeltaMovement(0.0D, Math.min(entity.getDeltaMovement().y, 0.0D), 0.0D);
        entity.hurtMarked = true;

        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.getNavigation().stop();
        }
    }
}