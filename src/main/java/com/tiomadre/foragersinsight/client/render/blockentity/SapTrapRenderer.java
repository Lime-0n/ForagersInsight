package com.tiomadre.foragersinsight.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiomadre.foragersinsight.common.block.entity.SapTrapBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SapTrapRenderer implements BlockEntityRenderer<SapTrapBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SapTrapRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SapTrapBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack bait = blockEntity.getBait();
        if (bait.isEmpty() || blockEntity.getLevel() == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.135D, 0.5D);
        poseStack.scale(0.55F, 0.55F, 0.55F);
        this.itemRenderer.renderStatic(bait, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}