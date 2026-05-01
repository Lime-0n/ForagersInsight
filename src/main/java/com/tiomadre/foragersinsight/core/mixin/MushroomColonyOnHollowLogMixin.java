package com.tiomadre.foragersinsight.core.mixin;

import com.tiomadre.foragersinsight.common.block.HollowLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

@Mixin(MushroomColonyBlock.class)
public class MushroomColonyOnHollowLogMixin {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void foragersInsight$canSurviveOnHollowLogs(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (foragersInsight$canUseHollowLogSupport(level, pos)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean foragersInsight$canUseHollowLogSupport(LevelReader level, BlockPos pos) {
        return HollowLogBlock.canSupportMushroomAt(level, pos);
    }
}