package com.tiomadre.foragersinsight.client;

import com.tiomadre.foragersinsight.client.render.blockentity.SuspiciousLitterRenderer;
import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIBlockEntityTypes;
import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIMenuTypes;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.client.gui.DiffuserScreen;
import com.tiomadre.foragersinsight.client.gui.HandbasketScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

@Mod.EventBusSubscriber(
        modid = ForagersInsight.MOD_ID,
        bus = Bus.MOD,
        value = Dist.CLIENT
)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            //container screen
            MenuScreens.register(
                    FIMenuTypes.HANDBASKET_MENU.get(),
                    HandbasketScreen::new
            );
            //diffuser screen
            MenuScreens.register(
                    FIMenuTypes.DIFFUSER_MENU.get(),
                    DiffuserScreen::new
            );
            // "full" handbasket property
            ItemProperties.register(
                    FIItems.HANDBASKET.get(),
                    new ResourceLocation(ForagersInsight.MOD_ID, "full"),
                    (stack, world, entity, seed) ->
                            stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                                    .map(handler -> {
                                        for (int i = 0; i < handler.getSlots(); i++) {
                                            if (handler.getStackInSlot(i).isEmpty()) {
                                                return 0.0F;
                                            }
                                        }
                                        return 1.0F;
                                    })
                                    .orElse(0.0F)
            );

            ItemBlockRenderTypes.setRenderLayer(
                    FIBlocks.SUSPICIOUS_LEAF_LITTER.get(),
                    RenderType.cutout()
            );
            ItemBlockRenderTypes.setRenderLayer(
                    FIBlocks.WOODLAND_FERN.get(),
                    RenderType.cutout()
            );

            BlockEntityRenderers.register(
                    FIBlockEntityTypes.SUSPICIOUS_LEAF_LITTER.get(),
                    SuspiciousLitterRenderer::new
            );
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) -> {
                    if (state == null) {
                        return FoliageColor.getDefaultColor();
                    }

                    return switch (state.getValue(SuspiciousLitterBlock.FOLIAGE)) {
                        case SPRUCE -> FoliageColor.getEvergreenColor();
                        case BIRCH -> FoliageColor.getBirchColor();
                        default -> level != null && pos != null
                                ? BiomeColors.getAverageFoliageColor(level, pos)
                                : FoliageColor.getDefaultColor();
                    };
                },
                FIBlocks.SUSPICIOUS_LEAF_LITTER.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> {
                    if (level == null || pos == null) {
                        return FoliageColor.getDefaultColor();
                    }
                    return BiomeColors.getAverageGrassColor(level, pos);
                },
                FIBlocks.WOODLAND_FERN.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> {
                    SuspiciousLitterBlock.FoliageType foliageType = SuspiciousLitterBlock.FoliageType.OAK;
                    if (stack.hasTag() && stack.getTag().contains("BlockStateTag") && stack.getTag().getCompound("BlockStateTag").contains("foliage")) {
                        String foliageName = stack.getTag().getCompound("BlockStateTag").getString("foliage");
                        try {
                            foliageType = SuspiciousLitterBlock.FoliageType.valueOf(foliageName.toUpperCase());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }

                    return switch (foliageType) {
                        case SPRUCE -> FoliageColor.getEvergreenColor();
                        case BIRCH -> FoliageColor.getBirchColor();
                        default -> FoliageColor.getDefaultColor();
                    };
                },
                FIBlocks.SUSPICIOUS_LEAF_LITTER.get()
        );
        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(), FIBlocks.WOODLAND_FERN.get());
    }
}