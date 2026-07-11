package com.tiomadre.foragersinsight.common.item;

import com.tiomadre.foragersinsight.client.model.AmadouCapModel;
import com.tiomadre.foragersinsight.client.model.FIModelLayers;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "unchecked"})
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
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HumanoidModel<?> amadouCapModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.amadouCapModel == null) {
                    this.amadouCapModel = new AmadouCapModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(FIModelLayers.AMADOU_CAP)
                    );
                }

                ((HumanoidModel) original).copyPropertiesTo(this.amadouCapModel);
                return this.amadouCapModel;
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GRAY));
    }
}