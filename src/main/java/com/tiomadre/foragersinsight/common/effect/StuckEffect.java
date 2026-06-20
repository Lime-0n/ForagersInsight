package com.tiomadre.foragersinsight.common.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public class StuckEffect extends MobEffect {
    public static final String STUCK_X_TAG = "foragersinsight:stuck_x";
    public static final String STUCK_Y_TAG = "foragersinsight:stuck_y";
    public static final String STUCK_Z_TAG = "foragersinsight:stuck_z";

    public StuckEffect() {
        super(MobEffectCategory.HARMFUL, 0xb87830);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        pinEntity(entity);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        CompoundTag data = entity.getPersistentData();
        data.putDouble(STUCK_X_TAG, entity.getX());
        data.putDouble(STUCK_Y_TAG, entity.getY());
        data.putDouble(STUCK_Z_TAG, entity.getZ());
        pinEntity(entity);
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        CompoundTag data = entity.getPersistentData();
        data.remove(STUCK_X_TAG);
        data.remove(STUCK_Y_TAG);
        data.remove(STUCK_Z_TAG);
    }

    public static void pinEntity(@NotNull LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(STUCK_X_TAG) || !data.contains(STUCK_Y_TAG) || !data.contains(STUCK_Z_TAG)) {
            data.putDouble(STUCK_X_TAG, entity.getX());
            data.putDouble(STUCK_Y_TAG, entity.getY());
            data.putDouble(STUCK_Z_TAG, entity.getZ());
        }

        double x = data.getDouble(STUCK_X_TAG);
        double y = data.getDouble(STUCK_Y_TAG);
        double z = data.getDouble(STUCK_Z_TAG);
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        entity.fallDistance = 0.0F;
        entity.teleportTo(x, y, z);

        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.getNavigation().stop();
        }
    }
}