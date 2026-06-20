package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.core.registry.FIConfig;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MilkBucketItem extends Item {
    private static final int DRINK_DURATION = 32;

    public MilkBucketItem(Properties settings) {
        super(settings);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return DRINK_DURATION;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide) {
            if (FIConfig.COMMON.milkRemovesOdorous.get()) {
                entity.removeAllEffects();
            } else {
                new ArrayList<>(entity.getActiveEffects()).stream()
                        .map(MobEffectInstance::getEffect)
                        .filter(effect -> effect != FIMobEffects.ODOROUS.get())
                        .forEach(entity::removeEffect);
            }
        }

        if (entity instanceof Player player) {
            return ItemUtils.createFilledResult(stack, player, Items.BUCKET.getDefaultInstance());
        }

        return stack;
    }
}