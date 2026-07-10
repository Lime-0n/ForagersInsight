package com.tiomadre.foragersinsight.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ForagersInsight.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class StropAnimation {

    private StropAnimation() {
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (!shouldPlayStroppingAnimation(player, mainHand, offHand)) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();

        float animationTime =
                offHand.getUseDuration() -
                        player.getUseItemRemainingTicks() +
                        partialTick;

        float rub = Mth.sin(animationTime * 1.8F);
        float lift = Mth.sin(animationTime * 3.6F) * 0.025F;
        poseStack.pushPose();

        poseStack.translate(
                -0.32F + rub * 0.10F,
                0.18F + lift,
                -0.18F
        );
        poseStack.mulPose(Axis.XP.rotationDegrees(-28.0F));
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        -24.0F + rub * 7.0F
                )
        );
        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        18.0F + rub * 5.0F
                )
        );

        poseStack.scale(0.9F, 0.9F, 0.9F);
    }

    private static boolean shouldPlayStroppingAnimation(
            Player player,
            ItemStack mainHand,
            ItemStack offHand
    ) {
        if (!offHand.is(FIItems.STROP.get())) {
            return false;
        }

        if (!player.isUsingItem()) {
            return false;
        }

        if (player.getUsedItemHand() != InteractionHand.OFF_HAND) {
            return false;
        }

        return canRepair(mainHand);
    }

    private static boolean canRepair(ItemStack tool) {
        return !tool.isEmpty() && tool.isDamageableItem() && tool.isDamaged();
    }
}