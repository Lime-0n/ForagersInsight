package com.tiomadre.foragersinsight.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class StuckEffect extends MobEffect {
    public StuckEffect() {
        super(MobEffectCategory.HARMFUL, 0x6b3f24);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "1a7f24a2-5c4b-4ea4-b78d-b86363d0fa3e", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 movement = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0D, Math.min(movement.y, 0.0D), 0.0D);
        entity.hurtMarked = true;

        if (entity instanceof PathfinderMob mob) {
            mob.getNavigation().stop();
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}