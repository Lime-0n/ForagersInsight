package com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.registry.FIForageLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class SuspiciousLitterLoot {

    private SuspiciousLitterLoot() {
    }

    public static ItemStack chooseLoot(ServerLevel level, BlockPos pos, BlockState state) {
        return chooseLoot(level, pos, state, 0);
    }

    public static ItemStack chooseLoot(ServerLevel level, BlockPos pos, BlockState state, int luckOfTheTreesLevel) {
        if (!state.hasProperty(SuspiciousLitterBlock.FOLIAGE)) {
            return ItemStack.EMPTY;
        }

        SuspiciousLitterBlock.FoliageType type = state.getValue(SuspiciousLitterBlock.FOLIAGE);
        ResourceLocation lootTableId = FIForageLoot.suspiciousLeafLitterByFoliage(type);
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,lootTableId));

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withLuck(luckOfTheTreesLevel)
                .create(LootContextParamSets.ARCHAEOLOGY);

        List<ItemStack> generated = lootTable.getRandomItems(params);
        if (generated.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return generated.get(level.random.nextInt(generated.size())).copy();
    }

    public static void dropLoot(ServerLevel level, BlockPos pos, BlockState state, ItemStack itemStack) {
        ItemStack stack = itemStack.isEmpty() ? chooseLoot(level, pos, state) : itemStack;
        if (!stack.isEmpty()) {
            Block.popResource(level, pos, stack.copy());
        }
    }
}