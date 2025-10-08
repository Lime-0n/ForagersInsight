package com.tiomadre.foragersinsight.common.diffuser;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
public final class DiffuserScent {
    public static final int STANDARD_DURATION = 12000;
    public static final Vec3 DEFAULT_COLOR = new Vec3(0.8D, 0.8D, 0.8D);

    private static final List<DiffuserScent> ALL = new ArrayList<>();
    private static final Map<ResourceLocation, DiffuserScent> BY_ID = new ConcurrentHashMap<>();

    public static final DiffuserScent ROSEY = register(
            ForagersInsight.rl("rosey"),
            List.of(IngredientCount.of(FIItems.ROSE_PETALS.get(), 3)),
            ForagersInsight.rl("textures/scents/rosey.png"),
            colorFromRgb(0xff007f),
            STANDARD_DURATION,
            "scent.foragersinsight.rosey",
            "scent.foragersinsight.rosey.description",
            8.0D,
            () -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0, true, true, true)
    );

    public static void bootstrap() {
        Objects.requireNonNull(ROSEY, "Default scent not registered");
    }

    private final ResourceLocation id;
    private final List<IngredientCount> ingredients;
    private final ResourceLocation icon;
    private final Vec3 particleColor;
    private final int duration;
    private final int totalItemCount;
    private final String translationKey;
    private final String descriptionKey;
    private final double radius;
    private final Supplier<MobEffectInstance> effectSupplier;
    private final int networkId;

    private DiffuserScent(ResourceLocation id,
                          List<IngredientCount> ingredients,
                          ResourceLocation icon,
                          Vec3 particleColor,
                          int duration,
                          String translationKey,
                          String descriptionKey,
                          double radius,
                          Supplier<MobEffectInstance> effectSupplier,
                          int networkId) {
        this.id = Objects.requireNonNull(id, "id");
        this.ingredients = List.copyOf(ingredients);
        this.icon = Objects.requireNonNull(icon, "icon");
        this.particleColor = particleColor;
        this.duration = duration;
        this.totalItemCount = this.ingredients.stream().mapToInt(IngredientCount::count).sum();
        this.translationKey = Objects.requireNonNull(translationKey, "translationKey");
        this.descriptionKey = Objects.requireNonNull(descriptionKey, "descriptionKey");
        this.radius = radius;
        this.effectSupplier = Objects.requireNonNull(effectSupplier, "effectSupplier");
        this.networkId = networkId;
    }

    private static DiffuserScent register(ResourceLocation id,
                                          List<IngredientCount> ingredients,
                                          ResourceLocation icon,
                                          Vec3 particleColor,
                                          int duration,
                                          String translationKey,
                                          String descriptionKey,
                                          double radius,
                                          Supplier<MobEffectInstance> effectSupplier) {
        int networkId = ALL.size();
        DiffuserScent scent = new DiffuserScent(id, ingredients, icon, particleColor, duration, translationKey,
                descriptionKey, radius, effectSupplier, networkId);
        ALL.add(scent);
        BY_ID.put(scent.id, scent);
        return scent;
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

    public Vec3 particleColor() {
        return this.particleColor;
    }

    public int duration() {
        return this.duration;
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

    public static Vec3 colorFromRgb(int rgb) {
        double r = ((rgb >> 16) & 0xFF) / 255.0;
        double g = ((rgb >> 8) & 0xFF) / 255.0;
        double b = (rgb & 0xFF) / 255.0;
        return new Vec3(r, g, b);
    }

    public static final class IngredientCount {
        private final Ingredient ingredient;
        private final int count;

        private IngredientCount(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }

        public static IngredientCount of(Ingredient ingredient, int count) {
            return new IngredientCount(ingredient, count);
        }

        public static IngredientCount of(ItemLike item, int count) {
            return new IngredientCount(Ingredient.of(item), count);
        }

        public Ingredient ingredient() {
            return this.ingredient;
        }

        public int count() {
            return this.count;
        }

        public boolean matches(ItemStack stack) {
            return this.ingredient.test(stack) && stack.getCount() >= this.count;
        }

        @Override
        public String toString() {
            return "IngredientCount[" + ingredient + " x" + count + "]";
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
