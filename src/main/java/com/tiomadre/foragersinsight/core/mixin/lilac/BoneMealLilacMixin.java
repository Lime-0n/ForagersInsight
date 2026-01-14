package com.tiomadre.foragersinsight.core.mixin.lilac;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.RichSoilBlock;

@Mixin(BoneMealItem.class)
public class BoneMealLilacMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void foragersInsight$onBoneMeal(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (!clickedState.is(Blocks.LILAC)) {
            return;
        }

        BlockPos lilacPos = clickedPos;
        if (clickedState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            lilacPos = clickedPos.below();
        }

        BlockState lowerLilacState = level.getBlockState(lilacPos);
        BlockState upperLilacState = level.getBlockState(lilacPos.above());

        if (!lowerLilacState.is(Blocks.LILAC)
                || lowerLilacState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER
                || !upperLilacState.is(Blocks.LILAC)
                || upperLilacState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockState soilState = level.getBlockState(lilacPos.below());
        if (!(soilState.getBlock() instanceof RichSoilBlock)) {
            return;
        }

        if (!level.isClientSide) {
            ItemStack stack = context.getItemInHand();
            RandomSource random = level.getRandom();

            if (LilacTreeGrowthMixin.shouldGrow(random)) {
                LilacTreeGrowthMixin.tryGrowLilacTree((ServerLevel) level, lilacPos.below(), random);
            }

            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                stack.shrink(1);
            }

            level.levelEvent(1505, clickedPos, 0);
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
    }
}