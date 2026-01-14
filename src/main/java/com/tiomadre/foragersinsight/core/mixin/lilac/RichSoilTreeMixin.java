package com.tiomadre.foragersinsight.core.mixin.lilac;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.RichSoilBlock;

@Mixin(RichSoilBlock.class)
public class RichSoilTreeMixin {
    @Inject(method = "randomTick", at = @At("TAIL"))
    private void foragersInsight$growLilacTree(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        LilacTreeGrowthMixin.tryGrowFromRandomTick(level, pos, random);
    }
}