package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.DyeColor;
import java.util.List;

public class AmadouCapItem extends ArmorItem implements DyeableLeatherItem  {
    private static final String TOOLTIP_KEY = "tooltip.foragersinsight.amadou_cap.luck_of_the_trees";
    private static final String DYED_TOOLTIP_KEY = "tooltip.foragersinsight.amadou_cap.dyed";
    private static final String ARMOR_TEXTURE = ForagersInsight.MOD_ID + ":textures/block/amadou_hat.png";
    private static final String EMPTY_ARMOR_OVERLAY_TEXTURE = ForagersInsight.MOD_ID + ":textures/misc/empty_armor_overlay.png";


    public AmadouCapItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack itemToRepair, @NotNull ItemStack repairItem) {
        return repairItem.is(FIItems.AMADOU.get());
    }


    public static int applyLuckOfTheTrees(Player player, int enchantmentLevel) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(FIItems.AMADOU_CAP.get())) {
            return Math.max(enchantmentLevel, 1);
        }
        return enchantmentLevel;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return type == null ? ARMOR_TEXTURE : EMPTY_ARMOR_OVERLAY_TEXTURE;
    }

    @Override
    public int getColor(ItemStack stack) {
        return this.hasCustomColor(stack) ? lesscolor(DyeableLeatherItem.super.getColor(stack)) : 0xFFFFFF;
    }

    private static int lesscolor(int color) {
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int gray = (int) (red * 0.3F + green * 0.59F + blue * 0.11F);

        red = desaturateChannel(red, gray);
        green = desaturateChannel(green, gray);
        blue = desaturateChannel(blue, gray);

        return red << 16 | green << 8 | blue;
    }

    private static int desaturateChannel(int channel, int gray) {
        return (int) (channel * 0.67F + gray * 0.33F);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (this.hasCustomColor(stack)) {
            tooltip.add(Component.translatable(DYED_TOOLTIP_KEY, getClosestDyeColorName(DyeableLeatherItem.super.getColor(stack)))
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GRAY));
    }
    private static Component getClosestDyeColorName(int color) {
        DyeColor closestColor = DyeColor.WHITE;
        int closestDistance = Integer.MAX_VALUE;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;

        for (DyeColor dyeColor : DyeColor.values()) {
            float[] textureColors = dyeColor.getTextureDiffuseColors();
            int dyeRed = (int) (textureColors[0] * 255.0F);
            int dyeGreen = (int) (textureColors[1] * 255.0F);
            int dyeBlue = (int) (textureColors[2] * 255.0F);
            int redDifference = red - dyeRed;
            int greenDifference = green - dyeGreen;
            int blueDifference = blue - dyeBlue;
            int distance = redDifference * redDifference + greenDifference * greenDifference
                    + blueDifference * blueDifference;

            if (distance < closestDistance) {
                closestDistance = distance;
                closestColor = dyeColor;
            }
        }

        return Component.translatable("color.minecraft." + closestColor.getName());
    }
}