package com.tiomadre.foragersinsight.common.crafting;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.other.WaxedBoots;
import com.tiomadre.foragersinsight.core.registry.FIRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class WaxedBootsRecipe extends CustomRecipe {
    private static final Ingredient HONEYCOMB = Ingredient.of(Items.HONEYCOMB);
    private final int jeiHoneycombCount;

    public WaxedBootsRecipe(ResourceLocation id, CraftingBookCategory category) {
        this(id, category, 0);
    }

    private WaxedBootsRecipe(ResourceLocation id, CraftingBookCategory category, int jeiHoneycombCount) {
        super(id, category);
        this.jeiHoneycombCount = jeiHoneycombCount;
    }

    public static java.util.List<CraftingRecipe> createJeiRecipes() {
        java.util.List<CraftingRecipe> recipes = new java.util.ArrayList<>();
        for (int honeycombCount = 1; honeycombCount <= WaxedBoots.MAX_WAXED_LEVEL; honeycombCount++) {
            recipes.add(new WaxedBootsRecipe(ForagersInsight.rl("jei_waxed_boots_" + honeycombCount),
                    CraftingBookCategory.EQUIPMENT, honeycombCount));
        }
        return recipes;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        return getCraftingResult(container).isPresent();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull net.minecraft.core.RegistryAccess registryAccess) {
        return getCraftingResult(container).orElse(ItemStack.EMPTY);
    }

    private java.util.Optional<ItemStack> getCraftingResult(CraftingContainer container) {
        ItemStack boots = ItemStack.EMPTY;
        int honeycombCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (HONEYCOMB.test(stack)) {
                honeycombCount++;
                continue;
            }

            if (WaxedBoots.isBoots(stack) && boots.isEmpty()) {
                boots = stack;
                continue;
            }

            return java.util.Optional.empty();
        }

        if (boots.isEmpty() || honeycombCount < 1 || honeycombCount > WaxedBoots.MAX_WAXED_LEVEL) {
            return java.util.Optional.empty();
        }

        ItemStack result = boots.copy();
        result.setCount(1);
        WaxedBoots.wax(result, honeycombCount);
        return java.util.Optional.of(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        if (jeiHoneycombCount <= 0) {
            return ingredients;
        }

        ingredients.add(createBootsIngredient());
        for (int i = 0; i < jeiHoneycombCount; i++) {
            ingredients.add(HONEYCOMB);
        }
        return ingredients;
    }

    private Ingredient createBootsIngredient() {
        java.util.List<ItemStack> boots = ForgeRegistries.ITEMS.getValues().stream()
                .map(ItemStack::new)
                .filter(WaxedBoots::isBoots)
                .toList();
        return Ingredient.of(boots.stream());
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess registryAccess) {
        if (jeiHoneycombCount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(Items.DIAMOND_BOOTS);
        WaxedBoots.wax(result, jeiHoneycombCount);
        return result;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remainingItems.size(); ++i) {
            remainingItems.set(i, net.minecraftforge.common.ForgeHooks.getCraftingRemainingItem(container.getItem(i)));
        }
        return remainingItems;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return FIRecipeSerializers.WAXED_BOOTS.get();
    }
}