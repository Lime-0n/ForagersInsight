package com.tiomadre.foragersinsight.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StropAnimation {
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isUsingItem() || player.getUsedItemHand() != InteractionHand.OFF_HAND) {
            return;
        }
        if (!player.getOffhandItem().is(FIItems.STROP.get())) {
            return;
        }

        float useTicks = player.getTicksUsingItem() + event.getPartialTick();
        float rub = (float) Math.sin(useTicks * 0.9F) * 0.08F;
        boolean rightMainHand = player.getMainArm() == HumanoidArm.RIGHT;
        int mainHandSign = rightMainHand ? 1 : -1;
        int renderedHandSign = event.getHand() == InteractionHand.MAIN_HAND ? mainHandSign : -mainHandSign;

        PoseStack poseStack = event.getPoseStack();
        poseStack.translate(-0.18F * renderedHandSign, -0.12F + Math.abs(rub) * 0.35F, -0.28F);
        poseStack.mulPose(Axis.YP.rotationDegrees(18.0F * renderedHandSign));
        poseStack.mulPose(Axis.XP.rotationDegrees(-16.0F));

        if (event.getHand() == InteractionHand.MAIN_HAND) {
            poseStack.translate(0.0F, rub, 0.08F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(10.0F * renderedHandSign));
        } else {
            poseStack.translate(0.10F * renderedHandSign, -rub * 0.35F, -0.18F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-12.0F * renderedHandSign));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-12.0F * renderedHandSign));
        }
    }
}