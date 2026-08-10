package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class FITappables {
    private static final Map<ResourceLocation, TapperSource> SOURCES = new LinkedHashMap<>();
    private static boolean defaultsRegistered;

    private FITappables() {
    }

    public static synchronized void register(ResourceLocation id, TapperSource source) {
        if (SOURCES.putIfAbsent(id, source) != null) {
            throw new IllegalArgumentException("A tapper source exists registered as " + id);
        }
    }

    public static Optional<TapperSource> find(BlockState state) {
        bootstrap();
        return SOURCES.values().stream().filter(source -> source.canTap().test(state)).findFirst();
    }

    public static synchronized void bootstrap() {
        if (defaultsRegistered) return;
        defaultsRegistered = true;

        Predicate<BlockState> uprightSappyBirch = state ->
                (state.is(FIBlocks.SAPPY_BIRCH_LOG.get()) || state.is(FIBlocks.STRIPPED_SAPPY_BIRCH_LOG.get()))
                        && state.hasProperty(RotatedPillarBlock.AXIS)
                        && state.getValue(RotatedPillarBlock.AXIS) == net.minecraft.core.Direction.Axis.Y;

        register(ForagersInsight.rl("sappy_birch"), new TapperSource(
                uprightSappyBirch,
                () -> new ItemStack(FIItems.BIRCH_SAP_BUCKET.get()),
                new FireAspectBehavior(
                        () -> new ItemStack(FIItems.BIRCH_SYRUP_BUCKET.get()),
                        level -> level >= 2 ? 2 : 1,
                        TapperTextures.syrup()
                ),
                TapperTextures.sap()
        ));
    }

    public record TapperSource(Predicate<BlockState> canTap,
                               Supplier<ItemStack> resource,
                               FireAspectBehavior fireAspect,
                               TapperTextures textures) {
        public ItemStack result(int fireAspectLevel) {
            return (fireAspectLevel > 0 ? fireAspect.resource() : resource).get().copy();
        }

        public int harvestIncrement(int fireAspectLevel) {
            return fireAspectLevel > 0 ? Math.max(1, fireAspect.harvestIncrement().applyAsInt(fireAspectLevel)) : 1;
        }

        public TapperTextures textures(int fireAspectLevel) {
            return fireAspectLevel > 0 ? fireAspect.textures() : textures;
        }
    }

    public record FireAspectBehavior(Supplier<ItemStack> resource,
                                     IntUnaryOperator harvestIncrement,
                                     TapperTextures textures) {
    }

    /** Texture names for fill levels zero through four. */
    public record TapperTextures(ResourceLocation[] bucketTops,
                                 ResourceLocation bucketBottom,
                                 ResourceLocation[] bucketSides,
                                 ResourceLocation[] taps) {
        public TapperTextures {
            if (bucketTops.length != 5 || bucketSides.length != 5 || taps.length != 5) {
                throw new IllegalArgumentException("Tapper texture arrays must contain exactly five fill stages");
            }
            bucketTops = bucketTops.clone();
            bucketSides = bucketSides.clone();
            taps = taps.clone();
        }
//Texture patterns
        //Birch Sap
        public static TapperTextures sap() {
            return textures("bucket_top_stage", "bucket_bottom", "bucket_side", "bucket_side_full", "knife_tap", "sappy_knife_tap");
        }
        //Birch Syrup
        public static TapperTextures syrup() {
            return textures("syrup_bucket_top_stage", "syrup_bucket_bottom", "syrup_bucket_side", "syrup_bucket_side_full", "knife_tap", "syrup_knife_tap");
        }

        private static TapperTextures textures(String topPrefix, String bottom, String side, String fullSide,
                                               String emptyTap, String filledTap) {
            ResourceLocation[] tops = new ResourceLocation[5];
            ResourceLocation[] sides = new ResourceLocation[5];
            ResourceLocation[] taps = new ResourceLocation[5];
            for (int stage = 0; stage < 5; stage++) {
                String top = stage == 0 && topPrefix.startsWith("syrup_") ? "syrup_bucket_top" : topPrefix + stage;
                tops[stage] = ForagersInsight.rl("block/" + top);
                sides[stage] = ForagersInsight.rl("block/" + (stage == 4 ? fullSide : side));
                taps[stage] = ForagersInsight.rl("block/" + (stage == 0 ? emptyTap : filledTap));
            }
            return new TapperTextures(tops, ForagersInsight.rl("block/" + bottom), sides, taps);
        }
    }
}