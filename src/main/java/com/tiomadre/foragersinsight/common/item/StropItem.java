package com.tiomadre.foragersinsight.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StropItem extends Item {
    private static final int REPAIR_AMOUNT = 2;
    private static final int REPAIR_DELAY_TICKS = 20;
    private static final int REPAIR_INTERVAL_TICKS = 20;
    private static final int USE_DURATION_TICKS = 150;

    public StropItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack strop = player.getItemInHand(hand);
        if (hand != InteractionHand.OFF_HAND || !canRepair(player.getMainHandItem())) {
            return InteractionResultHolder.pass(strop);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(strop);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack strop, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player) || player.getOffhandItem() != strop) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (!canRepair(tool)) {
            player.stopUsingItem();
            return;
        }

        int elapsedTicks = this.getUseDuration(strop) - remainingUseDuration;
        if (elapsedTicks < REPAIR_DELAY_TICKS || elapsedTicks % REPAIR_INTERVAL_TICKS != 0) {
            return;
        }

        if (!level.isClientSide) {
            tool.setDamageValue(Math.max(0, tool.getDamageValue() - REPAIR_AMOUNT));
            strop.hurtAndBreak(2, player, user -> user.broadcastBreakEvent(EquipmentSlot.OFFHAND));
            player.awardStat(Stats.ITEM_USED.get(this));
            level.playSound(null, player.blockPosition(), SoundEvents.BRUSH_SAND, SoundSource.PLAYERS, 0.5F, .85F);
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return USE_DURATION_TICKS;
    }

    private static boolean canRepair(ItemStack tool) {
        return !tool.isEmpty() && tool.isDamageableItem() && tool.isDamaged();
    }
}
