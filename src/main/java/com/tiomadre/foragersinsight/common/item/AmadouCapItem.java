package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmadouCapItem extends ArmorItem {
    private static final String TOOLTIP_KEY = "tooltip.foragersinsight.amadou_cap.luck_of_the_trees";

    public AmadouCapItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    public static int applyLuckOfTheTrees(Player player, int enchantmentLevel) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(FIItems.AMADOU_CAP.get())) {
            return Math.max(enchantmentLevel, 1);
        }
        return enchantmentLevel;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GRAY));
    }
}