package com.tiomadre.foragersinsight.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.item.ConsumableItem;

public class AuspiciousStewItem extends ConsumableItem {
    private static final int EFFECT_DURATION = 400;
    private static final String EFFECT_TAG = "AuspiciousEffect";

    public AuspiciousStewItem(Properties properties, boolean hasFoodEffectTooltip) {
        super(properties, hasFoodEffectTooltip);
    }

    @Override
    public void affectConsumer(ItemStack stack, Level level, LivingEntity entity) {
        if (level.isClientSide) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(EFFECT_TAG, Tag.TAG_STRING)) {
            return;
        }
        ResourceLocation effectId = ResourceLocation.tryParse(tag.getString(EFFECT_TAG));
        if (effectId == null) {
            return;
        }
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, EFFECT_DURATION));
        }
    }
}