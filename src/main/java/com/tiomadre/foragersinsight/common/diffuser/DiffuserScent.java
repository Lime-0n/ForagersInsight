package com.tiomadre.foragersinsight.common.diffuser;

import com.google.common.base.Suppliers;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import com.tiomadre.foragersinsight.core.registry.FIMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
public final class DiffuserScent {
    public static final int STANDARD_DURATION = 12000;

    private static final List<DiffuserScent> ALL = new ArrayList<>();
    private static final Map<ResourceLocation, DiffuserScent> BY_ID = new ConcurrentHashMap<>();

    public static final Supplier<DiffuserScent> ROSEY = Suppliers.memoize(() ->
            new DiffuserScent(
                    new ResourceLocation("foragersinsight", "rosey"),
                    List.of(IngredientCount.of(Ingredient.of(FIItems.ROSE_PETALS.get()), 3)),
                    new ResourceLocation("foragersinsight", "textures/scents/rosey.png"),
                    "foragersinsight.diffuser.rosey",
                    "foragersinsight.diffuser.rosey.description",
                    5.0,
                    (Supplier<MobEffectInstance>) () -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0),
                    0));

    public static final Supplier<DiffuserScent> CONIFEROUS = Suppliers.memoize(() ->
            new DiffuserScent(
                    new ResourceLocation("foragersinsight", "coniferous"),
                    List.of(IngredientCount.of(Ingredient.of(FIItems.SPRUCE_TIPS.get()), 3)),
                    new ResourceLocation("foragersinsight", "textures/scents/coniferous.png"),
                    "foragersinsight.diffuser.coniferous",
                    "foragersinsight.diffuser.coniferous.description",
                    5.0,
                    () -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0),
                    1));

    public static final Supplier<DiffuserScent> FLORAL = Suppliers.memoize(() ->
            new DiffuserScent(
                    new ResourceLocation("foragersinsight", "floral"),
                    List.of(IngredientCount.of(Ingredient.of(FIItems.ROSELLE_PETALS.get()), 1),
                            IngredientCount.of(Ingredient.of(FIItems.ROSE_PETALS.get()), 1),
                            IngredientCount.of(Ingredient.of(Items.LILAC), 1)),
                    new ResourceLocation("foragersinsight", "textures/scents/floral.png"),
                    "foragersinsight.diffuser.floral",
                    "foragersinsight.diffuser.floral.description",
                    5.0,
                    () -> new MobEffectInstance(FIMobEffects.BLOOM.get(), 100, 0),
                    0));
    public static final Supplier<DiffuserScent> FOUL = Suppliers.memoize(() ->
            new DiffuserScent(
                    new ResourceLocation("foragersinsight", "foul"),
                    List.of(IngredientCount.of(Ingredient.of(Items.ROTTEN_FLESH), 4)),
                    new ResourceLocation("foragersinsight", "textures/scents/foul.png"),
                    "foragersinsight.diffuser.foul",
                    "foragersinsight.diffuser.foul.description",
                    5.0,
                    () -> new MobEffectInstance(FIMobEffects.ODOROUS.get(), 100, 0),
                    3));

    public static void bootstrap() {
        ROSEY.get();
        CONIFEROUS.get();
        FLORAL.get();
        FOUL.get();
    }

    private final ResourceLocation id;
    private final List<IngredientCount> ingredients;
    private final ResourceLocation icon;
    private final int totalItemCount;
    private final String translationKey;
    private final String descriptionKey;
    private final double radius;
    private final Supplier<MobEffectInstance> effectSupplier;
    private final int networkId;

    private DiffuserScent(ResourceLocation id,
                          List<IngredientCount> ingredients,
                          ResourceLocation icon,
                          String translationKey,
                          String descriptionKey,
                          double radius,
                          Supplier<MobEffectInstance> effectSupplier,
                          int networkId) {
        this.id = Objects.requireNonNull(id, "id");
        this.ingredients = List.copyOf(ingredients);
        this.icon = Objects.requireNonNull(icon, "icon");
        this.totalItemCount = this.ingredients.stream().mapToInt(IngredientCount::count).sum();
        this.translationKey = Objects.requireNonNull(translationKey, "translationKey");
        this.descriptionKey = Objects.requireNonNull(descriptionKey, "descriptionKey");
        this.radius = radius;
        this.effectSupplier = Objects.requireNonNull(effectSupplier, "effectSupplier");
        this.networkId = networkId;
        ALL.add(this);
        BY_ID.put(this.id, this);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public List<IngredientCount> ingredients() {
        return this.ingredients;
    }

    public ResourceLocation icon() {
        return this.icon;
    }

    public static Optional<DiffuserScent> byId(ResourceLocation id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public int networkId() {
        return this.networkId;
    }

    public static Optional<DiffuserScent> byNetworkId(int networkId) {
        if (networkId < 0 || networkId >= ALL.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ALL.get(networkId));
    }

    public Component displayName() {
        return Component.translatable(this.translationKey);
    }

    public Component description() {
        return Component.translatable(this.descriptionKey).withStyle(ChatFormatting.GRAY);
    }

    public List<Component> tooltip() {
        List<Component> tooltip = new ArrayList<>(2);
        tooltip.add(this.displayName().copy().withStyle(ChatFormatting.GOLD));
        tooltip.add(this.description());
        return tooltip;
    }

    public Optional<MobEffectInstance> createEffectInstance() {
        MobEffectInstance effect = this.effectSupplier.get();
        return Optional.ofNullable(effect);
    }

    public double radius() {
        return this.radius;
    }

    public boolean matches(List<ItemStack> stacks) {
        if (this.ingredients.isEmpty()) {
            return false;
        }
        int[] remaining = new int[this.ingredients.size()];
        for (int i = 0; i < this.ingredients.size(); i++) {
            remaining[i] = this.ingredients.get(i).count();
        }

        int totalItems = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;

            totalItems += stack.getCount();
            boolean matched = false;
            for (int i = 0; i < this.ingredients.size(); i++) {
                IngredientCount ingredient = this.ingredients.get(i);
                if (ingredient.ingredient().test(stack)) {
                    remaining[i] -= stack.getCount();
                    if (remaining[i] < 0) return false;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return totalItems == this.totalItemCount && Arrays.stream(remaining).allMatch(r -> r == 0);
    }

    public static final class IngredientCount {
        private final Supplier<Ingredient> ingredientSupplier;
        private Ingredient ingredient;
        private final int count;

        private IngredientCount(Supplier<Ingredient> ingredientSupplier, int count) {
            this.ingredientSupplier = ingredientSupplier;
            this.count = count;
        }

        public static IngredientCount of(Ingredient ingredient, int count) {
            return new IngredientCount(() -> ingredient, count);
        }

        public static IngredientCount of(ItemLike item, int count) {
            return new IngredientCount(() -> Ingredient.of(item), count);
        }

        public static IngredientCount of(Supplier<? extends ItemLike> item, int count) {
            return new IngredientCount(() -> Ingredient.of(item.get()), count);
        }

        public Ingredient ingredient() {
            if (this.ingredient == null) {
                this.ingredient = Objects.requireNonNull(this.ingredientSupplier.get(), "ingredient");
            }
            return this.ingredient;
        }

        public int count() {
            return this.count;
        }

        public boolean matches(ItemStack stack) {
            return this.ingredient().test(stack) && stack.getCount() >= this.count;
        }

        @Override
        public String toString() {
            return "IngredientCount[" + ingredient() + " x" + count + "]";
        }
    }

    public static Optional<DiffuserScent> findMatch(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return Optional.empty();
        }

        for (DiffuserScent scent : ALL) {
            if (scent.matches(stacks)) {
                return Optional.of(scent);
            }
        }

        return Optional.empty();
    }
}
