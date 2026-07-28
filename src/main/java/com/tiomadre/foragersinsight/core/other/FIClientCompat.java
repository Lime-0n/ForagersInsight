package com.tiomadre.foragersinsight.core.other;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class FIClientCompat {
    public static void registerCompat() {
        registerItemColors();
        registerBlockColors();
    }

    private static void registerItemColors() {
        ItemColors itemColors = Minecraft.getInstance().getItemColors();

        itemColors.register((stack, tintIndex) -> tintIndex == 0
                        ? ((DyeableLeatherItem) stack.getItem()).getColor(stack)
                        : 0xFFFFFF,
                FIItems.AMADOU_CAP.get());
    }
    private static void registerBlockColors() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        ItemColors itemColors = Minecraft.getInstance().getItemColors();


        List<RegistryObject<Block>> genericFoliage = new ArrayList<>();
        genericFoliage.add(FIBlocks.BOUNTIFUL_OAK_LEAVES);
        genericFoliage.add(FIBlocks.BOUNTIFUL_DARK_OAK_LEAVES);
        genericFoliage.add(FIBlocks.WOODLAND_FERN);

        List<RegistryObject<Block>> lilacLeaves = new ArrayList<>();
        lilacLeaves.add(FIBlocks.LILAC_LEAVES);
        lilacLeaves.add(FIBlocks.BLOSSOMING_LILAC_LEAVES);

        List<RegistryObject<Block>> spruceLeaves = new ArrayList<>();
        spruceLeaves.add(FIBlocks.BOUNTIFUL_SPRUCE_LEAVES);

        List<RegistryObject<Block>> suspiciousLitter = new ArrayList<>();
        suspiciousLitter.add(FIBlocks.SUSPICIOUS_LEAF_LITTER);

        DataUtil.registerBlockColor(blockColors, (state, world, pos, tintIndex) -> 0xFFFFFF, spruceLeaves);
        DataUtil.registerBlockItemColor(itemColors, (stack, tintIndex) -> 0xFFFFFF, spruceLeaves);

        DataUtil.registerBlockColor(blockColors, (state, world, pos, tintIndex) -> 0xFFFFFF, lilacLeaves);
        DataUtil.registerBlockItemColor(itemColors, (stack, tintIndex) -> 0xFFFFFF, lilacLeaves);

        DataUtil.registerBlockColor(
                blockColors,
                (state, world, pos, tintIndex) ->
                        (world != null && pos != null)
                                ? BiomeColors.getAverageFoliageColor(world, pos)
                                : FoliageColor.get(0.5D, 1.0D),
                genericFoliage
        );
        DataUtil.registerBlockItemColor(
                itemColors,
                (stack, tintIndex) -> FoliageColor.get(0.5D, 1.0D),
                genericFoliage
        );

        DataUtil.registerBlockColor(
                blockColors,
                (state, world, pos, tintIndex) -> {
                    if (state != null
                            && state.hasProperty(SuspiciousLitterBlock.FOLIAGE)
                            && state.getValue(SuspiciousLitterBlock.FOLIAGE) == SuspiciousLitterBlock.FoliageType.FLOWER) {
                        return 0xFFFFFF;
                    }
                    return (world != null && pos != null)
                            ? BiomeColors.getAverageFoliageColor(world, pos)
                            : FoliageColor.getDefaultColor();
                },
                suspiciousLitter
        );
        DataUtil.registerBlockItemColor(
                itemColors,
                (stack, tintIndex) -> FoliageColor.getDefaultColor(),
                suspiciousLitter
        );
    }
}