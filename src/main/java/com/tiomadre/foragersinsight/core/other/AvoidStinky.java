package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

public class AvoidStinky extends AvoidEntityGoal<LivingEntity> {
    public AvoidStinky(PathfinderMob mob, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, LivingEntity.class, 8.0F, walkSpeedModifier, sprintSpeedModifier,
                livingEntity -> livingEntity != mob && livingEntity.hasEffect(FIMobEffects.ODOROUS.get()));
    }
}