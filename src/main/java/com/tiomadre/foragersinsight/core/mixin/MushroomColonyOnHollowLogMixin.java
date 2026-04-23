package com.tiomadre.foragersinsight.core.mixin;

import com.tiomadre.foragersinsight.common.block.HollowLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    private void foragersInsight$progressOnHollowLogs(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!foragersInsight$canUseHollowLogSupport(level, pos)) {
            return;
        }

        int age = state.getValue(MushroomColonyBlock.COLONY_AGE);
        int maxAge = MushroomColonyBlock.COLONY_AGE.getPossibleValues().stream().max(Integer::compareTo).orElse(age);
        if (age >= maxAge || random.nextInt(5) != 0) {
            return;
        }

        level.setBlock(pos, state.setValue(MushroomColonyBlock.COLONY_AGE, age + 1), Block.UPDATE_ALL);
    }

    private static boolean foragersInsight$canUseHollowLogSupport(LevelReader level, BlockPos pos) {
        return HollowLogBlock.MushroomPlacement(level.getBlockState(pos.below()), Direction.UP);
    }
}