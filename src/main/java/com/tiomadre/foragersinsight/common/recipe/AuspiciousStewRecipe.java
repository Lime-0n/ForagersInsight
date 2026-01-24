package com.tiomadre.foragersinsight.common.recipe;

import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import com.tiomadre.foragersinsight.core.registry.FIRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AuspiciousStewRecipe extends CustomRecipe {
    private static final int EFFECT_DURATION = 400;

    public AuspiciousStewRecipe(net.minecraft.resources.ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundBowl = false;
        int mushroomCount = 0;
        boolean foundFlower = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Items.BOWL)) {
                if (foundBowl) {
                    return false;
                }
                foundBowl = true;
                continue;
            }

            if (stack.is(FIItems.BLEWIT_MUSHROOM.get())) {
                if (mushroomCount >= 2) {
                    return false;
                }
                mushroomCount++;
                continue;
            }

            if (isAuspiciousFlower(stack.getItem())) {
                if (foundFlower) {
                    return false;
                }
                foundFlower = true;
                continue;
            }

            return false;
        }

        return foundBowl && foundFlower && mushroomCount == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack soup = new ItemStack(FIItems.AUSPICIOUS_STEW.get());
        MobEffectInstance effect = findEffect(container);
        if (effect != null) {
            applyEffect(soup, effect);
        }
        return soup;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(FIItems.AUSPICIOUS_STEW.get());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FIRecipeSerializers.AUSPICIOUS_STEW.get();
    }

    private static boolean isAuspiciousFlower(Item item) {
        return item == FIItems.ROSE_PETALS.get()
                || item == FIItems.ROSELLE_PETALS.get()
                || item == FIItems.SPRUCE_TIPS.get()
                || item == FIItems.LILAC_BLOOM.get();
    }

    private static MobEffectInstance findEffect(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            if (item == FIItems.ROSE_PETALS.get()) {
                return new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION);
            }
            if (item == FIItems.ROSELLE_PETALS.get()) {
                return new MobEffectInstance(FIMobEffects.BLOOM.get(), EFFECT_DURATION);
            }
            if (item == FIItems.SPRUCE_TIPS.get()) {
                return new MobEffectInstance(MobEffects.ABSORPTION, EFFECT_DURATION);
            }
            if (item == FIItems.LILAC_BLOOM.get()) {
                return new MobEffectInstance(FIMobEffects.MEDICINAL.get(), EFFECT_DURATION);
            }
        }
        return null;
    }

    private static void applyEffect(ItemStack stack, MobEffectInstance effect) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag effects = tag.getList("Effects", Tag.TAG_COMPOUND);
        CompoundTag effectTag = new CompoundTag();
        MobEffect effectType = effect.getEffect();
        effectTag.putByte("EffectId", (byte) BuiltInRegistries.MOB_EFFECT.getId(effectType));
        effectTag.putInt("EffectDuration", effect.getDuration());
        effects.add(effectTag);
        tag.put("Effects", effects);
    }
}