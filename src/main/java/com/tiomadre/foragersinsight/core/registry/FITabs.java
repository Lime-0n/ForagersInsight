package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FITabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ForagersInsight.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FORAGERS_INSIGHT = TABS.register("foragersinsight", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.foragersinsight"))
                    .icon(() -> new ItemStack(FIItems.ROSELLE_BUSH_ITEM.get()))
                    .displayItems((parameters, output) -> displayEntries(output))
                    .build());

    private static final List<RegistryObject<? extends ItemLike>> BLOCK_ENTRIES = List.of(
            FIBlocks.BOUNTIFUL_DARK_OAK_SAPLING,
            FIBlocks.BOUNTIFUL_DARK_OAK_LEAVES,
            FIBlocks.BOUNTIFUL_OAK_SAPLING,
            FIBlocks.BOUNTIFUL_OAK_LEAVES,
            FIBlocks.SAPPY_BIRCH_LOG,
            FIBlocks.BOUNTIFUL_SPRUCE_SAPLING,
            FIBlocks.BOUNTIFUL_SPRUCE_LEAVES,
            FIBlocks.APPLE_CRATE,
            FIBlocks.BLACK_ACORN_SACK,
            FIBlocks.DANDELION_ROOT_SACK,
            FIBlocks.POPPY_SEEDS_SACK,
            FIBlocks.ROSE_HIP_SACK,
            FIBlocks.ROSELLE_CALYX_SACK,
            FIBlocks.SPRUCE_TIPS_SACK,
            FIBlocks.DENSE_ROSE_PETAL_MAT,
            FIBlocks.DENSE_ROSELLE_PETAL_MAT,
            FIBlocks.DENSE_SPRUCE_TIP_MAT,
            FIBlocks.DENSE_STRAW_MAT,
            FIBlocks.SCATTERED_ROSE_PETAL_MAT,
            FIBlocks.SCATTERED_ROSELLE_PETAL_MAT,
            FIBlocks.SCATTERED_SPRUCE_TIP_MAT,
            FIBlocks.SCATTERED_STRAW_MAT,
            FIBlocks.ROSELLE_BUSH,
            FIBlocks.STOUT_BEACH_ROSE_BUSH,
            FIBlocks.TALL_BEACH_ROSE_BUSH
    );

    private static final List<RegistryObject<? extends ItemLike>> INGREDIENT_ENTRIES = List.of(
            FIItems.APPLE_SLICE,
            FIItems.COOKED_RABBIT_LEG,
            FIItems.RAW_RABBIT_LEG,
            FIItems.ROSE_PETALS,
            FIItems.ROSELLE_PETALS,
            FIItems.ACORN_MEAL,
            FIItems.COCOA_POWDER,
            FIItems.CRUSHED_ICE,
            FIItems.POPPY_SEED_PASTE,
            FIItems.WHEAT_FLOUR,
            FIItems.BLACK_ACORN,
            FIItems.DANDELION_ROOT,
            FIItems.POPPY_SEEDS,
            FIItems.ROSE_HIP,
            FIItems.ROSELLE_CALYX,
            FIItems.SPRUCE_TIPS,
            FIItems.ACORN_DOUGH,
            FIItems.GREEN_SAUCE,
            FIItems.SEED_BUTTER,
            FIItems.SEED_MILK_BOTTLE,
            FIItems.SEED_MILK_BUCKET,
            FIItems.BIRCH_SYRUP_BUCKET,
            FIItems.BIRCH_SYRUP_BOTTLE,
            FIItems.BIRCH_SAP_BUCKET,
            FIItems.BIRCH_SAP_BOTTLE
    );

    private static final List<RegistryObject<? extends ItemLike>> CUISINE_ENTRIES = List.of(
            FIItems.ACORN_COOKIE,
            FIItems.ROSE_COOKIE,
            FIItems.BLACK_FOREST_MUFFIN,
            FIItems.RED_VELVET_CUPCAKE,
            FIItems.POPPY_SEED_BAGEL,
            FIItems.SLICE_OF_ACORN_CARROT_CAKE,
            FIItems.ACORN_CARROT_CAKE_ITEM,
            FIItems.CANDIED_CALYCES,
            FIItems.ACORN_NOODLES,
            FIItems.CARROT_POPPY_CHOWDER,
            FIItems.COD_AND_PUMPKIN_STEW,
            FIItems.GLAZED_PORKCHOP_AND_ACORN_GRITS,
            FIItems.FORAGERS_GRANOLA,
            FIItems.HEARTY_SPRUCE_PILAF,
            FIItems.KELP_AND_BEET_SALAD,
            FIItems.MEADOW_MEDLEY,
            FIItems.ROSE_HIP_SOUP,
            FIItems.ROSE_ROASTED_ROOTS,
            FIItems.SAVORY_PASTA_ROLL,
            FIItems.SEASIDE_SIZZLER,
            FIItems.STEAMY_KELP_RICE,
            FIItems.SYRUP_TOAST_STACKS,
            FIItems.WOODLAND_PASTA,
            FIItems.TART_WHEAT_PILAF,
            FIItems.CREAMY_SALMON_BAGEL,
            FIItems.DANDELION_FRIES,
            FIItems.JAMMY_BREAKFAST_SANDWICH,
            FIItems.KELP_WRAP,
            FIItems.SEED_BUTTER_JAMWICH,
            FIItems.SWEET_ROASTED_RABBIT_LEG,
            FIItems.DANDELION_ROOT_TEA,
            FIItems.FOREST_ELIXIR,
            FIItems.GLOWING_CARROT_JUICE,
            FIItems.ROSE_CORDIAL,
            FIItems.ROSELLE_JUICE
    );

    private static final List<RegistryObject<? extends ItemLike>> TOOL_ENTRIES = List.of(
            FIItems.DIFFUSER,
            FIItems.HANDBASKET,
            FIItems.FLINT_MALLET,
            FIItems.IRON_MALLET,
            FIItems.GOLD_MALLET,
            FIItems.DIAMOND_MALLET,
            FIItems.NETHERITE_MALLET,
            FIItems.FLINT_SHEARS,
            FIItems.TAPPER
    );

    private static final Set<String> BLOCK_PATHS = paths(BLOCK_ENTRIES);
    private static final Set<String> INGREDIENT_PATHS = paths(INGREDIENT_ENTRIES);
    private static final Set<String> CUISINE_PATHS = paths(CUISINE_ENTRIES);
    private static final Set<String> TOOL_PATHS = paths(TOOL_ENTRIES);

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    private static void displayEntries(CreativeModeTab.Output output) {
        Stream.of(BLOCK_ENTRIES, INGREDIENT_ENTRIES, CUISINE_ENTRIES, TOOL_ENTRIES)
                .flatMap(Collection::stream)
                .map(entry -> Map.entry(entry.getId(), entry))
                .sorted(Comparator
                        .comparingInt((Map.Entry<ResourceLocation, ? extends Supplier<? extends ItemLike>> entry) ->
                                categoryOrder(entry.getKey().getPath()))
                        .thenComparing(entry -> entry.getKey().getPath()))
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .map(ItemStack::new)
                .forEach(output::accept);
    }

    private static Set<String> paths(Collection<RegistryObject<? extends ItemLike>> entries) {
        return entries.stream()
                .map(entry -> entry.getId().getPath())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static int categoryOrder(String path) {
        if (BLOCK_PATHS.contains(path)) {
            return 0;
        }
        if (INGREDIENT_PATHS.contains(path)) {
            return 1;
        }
        if (CUISINE_PATHS.contains(path)) {
            return 2;
        }
        if (TOOL_PATHS.contains(path)) {
            return 3;
        }
        return 4;
    }
}