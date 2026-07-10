package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class WaxedBoots {
    public static final String WAXED_DURATION_TAG = "ForagersInsightWaxedDuration";
    private static final int MAX_WAXED_DURATION = 6000;
    private static final int WALKING_DRAIN_INTERVAL = 20;
    private static final int STUCK_PREVENTION_DRAIN = 200;
    private static final int STICKY_RESISTANCE_REFRESH_DURATION = 40;

    public static boolean isBoots(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.FEET;
    }

    public static void wax(ItemStack stack) {
        stack.getOrCreateTag().putInt(WAXED_DURATION_TAG, MAX_WAXED_DURATION);
    }

    public static boolean isWaxed(ItemStack stack) {
        return getWaxedDuration(stack) > 0;
    }

    public static int getWaxedDuration(ItemStack stack) {
        return stack.getOrCreateTag().getInt(WAXED_DURATION_TAG);
    }

    public static void appendTooltip(ItemStack stack, java.util.List<Component> tooltip) {
        int duration = getWaxedDuration(stack);
        if (duration <= 0) return;

        int totalSeconds = (int) Math.ceil(duration / 20.0D);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        tooltip.add(Component.translatable("tooltip.foragersinsight.waxed_boots", minutes, String.format("%02d", seconds))
                .withStyle(ChatFormatting.GOLD));
    }

    public static void tick(LivingEntity entity) {
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!isWaxed(boots)) return;

        entity.addEffect(new MobEffectInstance(FIMobEffects.STICKY_RESISTANCE.get(), STICKY_RESISTANCE_REFRESH_DURATION, 0, false, false, true));

        if (entity.level().getGameTime() % WALKING_DRAIN_INTERVAL == 0 && entity.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D) {
            shrinkWaxedDuration(boots, WALKING_DRAIN_INTERVAL);
        }
    }

    public static void drainForStuckPrevention(LivingEntity entity) {
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!isWaxed(boots)) return;

        shrinkWaxedDuration(boots, STUCK_PREVENTION_DRAIN);
    }

    private static void shrinkWaxedDuration(ItemStack stack, int amount) {
        int duration = Math.max(0, getWaxedDuration(stack) - amount);
        if (duration == 0) {
            stack.getOrCreateTag().remove(WAXED_DURATION_TAG);
            return;
        }
        stack.getOrCreateTag().putInt(WAXED_DURATION_TAG, duration);
    }
}