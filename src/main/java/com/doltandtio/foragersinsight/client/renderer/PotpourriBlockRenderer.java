package com.doltandtio.foragersinsight.client.renderer;

import com.doltandtio.foragersinsight.common.block.entity.PotpourriBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PotpourriBlockRenderer implements BlockEntityRenderer<PotpourriBlockEntity> {
    private static final Vec3[] SINGLE = {new Vec3(0.0D, 0.0D, 0.0D)};
    private static final Vec3[] DOUBLE = {new Vec3(-0.18D, 0.0D, 0.0D), new Vec3(0.18D, 0.0D, 0.0D)};
    private static final Vec3[] TRIPLE = {new Vec3(-0.28D, 0.0D, 0.0D), new Vec3(0.0D, 0.0D, 0.0D), new Vec3(0.28D, 0.0D, 0.0D)};

    private final ItemRenderer itemRenderer;

    public PotpourriBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull PotpourriBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        List<ItemStack> stacks = blockEntity.getDisplayedItems().stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
        if (stacks.isEmpty()) {
            return;
        }

        Vec3[] offsets = offsetsForCount(stacks.size());
        for (int i = 0; i < stacks.size() && i < offsets.length; i++) {
            ItemStack stack = stacks.get(i);
            Vec3 offset = offsets[i];
            poseStack.pushPose();
            poseStack.translate(0.5D + offset.x, 0.6D, 0.5D + offset.z);
            poseStack.mulPose(Axis.YP.rotationDegrees((i - stacks.size() / 2.0F) * 12.0F));
            poseStack.scale(0.6F, 0.6F, 0.6F);
            this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, buffer, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }

    private static Vec3[] offsetsForCount(int count) {
        return switch (count) {
            case 1 -> SINGLE;
            case 2 -> DOUBLE;
            default -> TRIPLE;
        };
    }
}