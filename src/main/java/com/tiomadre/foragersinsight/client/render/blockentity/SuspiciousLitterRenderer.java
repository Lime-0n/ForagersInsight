package com.tiomadre.foragersinsight.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiomadre.foragersinsight.common.block.entity.suspiciouslitter.SuspiciousLitterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SuspiciousLitterRenderer implements BlockEntityRenderer<SuspiciousLitterBlockEntity> {

    private final ItemRenderer itemRenderer;

    public SuspiciousLitterRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SuspiciousLitterBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        ItemStack itemStack = blockEntity.getRevealedItem();
        if (itemStack.isEmpty()) {
            return;
        }

        float progress = blockEntity.getProgress();
        if (progress <= 0.0F) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.25D + progress * 0.5D, 0.5D);
        poseStack.scale(0.8F, 0.8F, 0.8F);

        this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}