package com.tiomadre.foragersinsight.data.server.tags;

import com.tiomadre.foragersinsight.core.registry.FIBlocks;
import com.tiomadre.foragersinsight.core.registry.FIItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;
import static com.tiomadre.foragersinsight.data.server.tags.FITags.ItemTag.*;

public class FIItemTags extends ItemTagsProvider {
    public FIItemTags(GatherDataEvent e, FIBlockTags blockTags) {
        super(e.getGenerator().getPackOutput(), e.getLookupProvider(), blockTags.contentsGetter());
    }
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //Aromatic
        this.tag(AROMATICS).add(FIItems.ROSE_PETALS.get(), FIItems.ROSELLE_PETALS.get(), FIItems.SPRUCE_TIPS.get());
        //Other
        this.tag(FITags.ItemTag.ICE).add(Items.ICE, FIItems.CRUSHED_ICE.get())
                .addOptional(new ResourceLocation("neapolitan", "ice_cubes"));
        this.tag(NUTS).add(FIItems.BLACK_ACORN.get());
        this.tag(NUTS_ACORN).add(FIItems.BLACK_ACORN.get());
        this.tag(SEEDS).add(FIItems.POPPY_SEEDS.get());
        this.tag(MILK_BUCKET).add(FIItems.SEED_MILK_BUCKET.get());
        this.tag(MILK_BOTTLE).add(FIItems.SEED_MILK_BOTTLE.get());
        //Crops
        this.tag(APPLE).add(FIItems.APPLE_SLICE.get(),Items.APPLE);
        this.tag(POPPY_SEEDS).add(FIItems.POPPY_SEEDS.get(), FIItems.POPPY_SEED_PASTE.get());
        this.tag(ACORN).add(FIItems.BLACK_ACORN.get(), FIItems.ACORN_MEAL.get());
        this.tag(WHEAT).add(FIItems.WHEAT_FLOUR.get(), Items.WHEAT);
        this.tag(COCOA).add(Items.COCOA_BEANS, FIItems.COCOA_POWDER.get());
        this.tag(ROOTS).add(Items.CARROT, Items.BEETROOT, FIItems.DANDELION_ROOT.get());
        this.tag(MUSHROOM).add(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM);
        this.tag(CROPS)
                .addTag(APPLE)
                .addTag(POPPY_SEEDS)
                .addTag(ACORN)
                .addTag(WHEAT)
                .addTag(COCOA)
                .addTag(ROOTS)
                .addTag(MUSHROOM);

        // Handbasket support tags
        this.tag(RAW_MEATS).add(Items.RABBIT, Items.CHICKEN, Items.PORKCHOP, Items.BEEF, Items.MUTTON, FIItems.RAW_RABBIT_LEG.get())
                .addOptional(new ResourceLocation("farmersdelight", "chicken_cuts"))
                .addOptional(new ResourceLocation("farmersdelight", "minced_beef"))
                .addOptional(new ResourceLocation("farmersdelight", "bacon"))
                .addOptional(new ResourceLocation("farmersdelight", "mutton_chops"));
        this.tag(COOKED_MEATS).add(Items.COOKED_RABBIT, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP, Items.COOKED_BEEF,
                        Items.COOKED_MUTTON, FIItems.COOKED_RABBIT_LEG.get())
                .addOptional(new ResourceLocation("farmersdelight", "cooked_bacon"))
                .addOptional(new ResourceLocation("farmersdelight", "cooked_mutton_chops"))
                .addOptional(new ResourceLocation("farmersdelight", "ham"))
                .addOptional(new ResourceLocation("farmersdelight", "beef_patty"));
        this.tag(RAW_FISHES).add(Items.COD, Items.SALMON)
                .addOptional(new ResourceLocation("farmersdelight", "cod_slice"))
                .addOptional(new ResourceLocation("farmersdelight", "salmon_slice"));
        this.tag(COOKED_FISHES).add(Items.COOKED_COD, Items.COOKED_SALMON)
                .addOptional(new ResourceLocation("farmersdelight", "cooked_cod_slice"))
                .addOptional(new ResourceLocation("farmersdelight", "cooked_salmon_slice"));
        this.tag(FRUITS).add(FIItems.ROSE_HIP.get(), FIItems.ROSELLE_CALYX.get(), FIItems.APPLE_SLICE.get())
                .addTag(APPLE);
        this.tag(VEGETABLES).add(FIItems.SPRUCE_TIPS.get())
                .addTag(ROOTS);
        this.tag(FLOUR).add(FIItems.ACORN_MEAL.get(), FIItems.WHEAT_FLOUR.get());
        this.tag(STRAW).add(ModItems.STRAW.get());
        this.tag(TREE_BARK).add(ModItems.TREE_BARK.get());
        this.tag(EGGS).add(Items.EGG);
        this.tag(LEATHER).add(Items.LEATHER, Items.RABBIT_HIDE);
        this.tag(FEATHERS).add(Items.FEATHER);
        this.tag(SUGAR).add(Items.SUGAR);

        this.tag(HANDBASKET_SPECIALTY)
                .add(Items.INK_SAC, Items.GLOW_INK_SAC, Items.RABBIT_FOOT, Items.KELP, Items.DRIED_KELP, Items.BAMBOO, Items.CACTUS, Items.VINE, Items.MOSS_BLOCK,
                        Items.AZALEA, Items.FLOWERING_AZALEA, Items.HONEYCOMB, Items.HONEY_BOTTLE)
                .add(FIItems.ROSE_PETALS.get(), FIItems.ROSELLE_PETALS.get(), FIItems.COCOA_POWDER.get(), FIItems.POPPY_SEED_PASTE.get(),
                        FIItems.BIRCH_SAP_BOTTLE.get(), FIItems.BIRCH_SYRUP_BOTTLE.get(), FIItems.ROSELLE_BUSH_ITEM.get())
                .add(ModItems.WILD_CABBAGES.get(), ModItems.WILD_BEETROOTS.get(), ModItems.WILD_POTATOES.get(), ModItems.WILD_TOMATOES.get(),
                        ModItems.WILD_CARROTS.get(), ModItems.RICE_PANICLE.get(), ModItems.PUMPKIN_SLICE.get(), ModItems.CABBAGE_LEAF.get(),
                        ModItems.MILK_BOTTLE.get());

        this.tag(ItemTags.SAPLINGS)
                .add(FIBlocks.BOUNTIFUL_DARK_OAK_SAPLING.get().asItem(), FIBlocks.BOUNTIFUL_OAK_SAPLING.get().asItem(),
                        FIBlocks.BOUNTIFUL_SPRUCE_SAPLING.get().asItem());

        this.tag(HANDBASKET_ALLOWED)
                .addOptionalTag(new ResourceLocation("forge", "raw_meats"))
                .addOptionalTag(new ResourceLocation("forge", "cooked_meats"))
                .addOptionalTag(new ResourceLocation("forge", "raw_fishes"))
                .addOptionalTag(new ResourceLocation("forge", "cooked_fishes"))
                .addOptionalTag(new ResourceLocation("forge", "eggs"))
                .addOptionalTag(new ResourceLocation("forge", "leather"))
                .addOptionalTag(new ResourceLocation("forge", "feathers"))
                .addOptionalTag(new ResourceLocation("forge", "nuts"))
                .addOptionalTag(new ResourceLocation("forge", "seeds"))
                .addOptionalTag(new ResourceLocation("forge", "crops"))
                .addOptionalTag(new ResourceLocation("forge", "fruits"))
                .addOptionalTag(new ResourceLocation("forge", "vegetables"))
                .addOptionalTag(new ResourceLocation("forge", "crops/mushroom"))
                .addOptionalTag(new ResourceLocation("forge", "flour"))
                .addOptionalTag(new ResourceLocation("forge", "straw"))
                .addOptionalTag(new ResourceLocation("forge", "tree_bark"))
                .addOptionalTag(new ResourceLocation("forge", "ice"))
                .addOptionalTag(new ResourceLocation("forge", "sugar"))
                .addOptionalTag(new ResourceLocation("forge", "milk/milk"))
                .addOptionalTag(new ResourceLocation("forge", "milk/milk_bottle"))
                .addOptionalTag(new ResourceLocation("forge", "honey_bottle"))
                .addOptionalTag(new ResourceLocation("minecraft", "flowers"))
                .addOptionalTag(new ResourceLocation("minecraft", "saplings"))
                .addTag(HANDBASKET_SPECIALTY);
        // Mallet
        this.tag(FITags.ItemTag.MALLETS).add(FIItems.FLINT_MALLET.get(),FIItems.IRON_MALLET.get(),
        FIItems.GOLD_MALLET.get(),FIItems.DIAMOND_MALLET.get(),FIItems.NETHERITE_MALLET.get());
        //Shears
        //this.tag(Tags.Items.TOOLS_SHEAR).add(FIItems.FLINT_SHEARS.get()); reenable when using newer FD version that uses tag
    }
    protected void registerForgeTags() {
        tag(STORAGE_BLOCK_ROSE_HIP).add(FIBlocks.ROSE_HIP_SACK.get().asItem());

        tag(STORAGE_BLOCK_POPPY_SEEDS).add(FIBlocks.POPPY_SEEDS_SACK.get().asItem());
        tag(STORAGE_BLOCK_DANDELION_ROOT).add(FIBlocks.DANDELION_ROOT_SACK.get().asItem());

        tag(STORAGE_BLOCK_SPRUCE_TIPS).add(FIBlocks.SPRUCE_TIPS_SACK.get().asItem());
        tag(STORAGE_BLOCK_BLACK_ACORNS).add(FIBlocks.BLACK_ACORN_SACK.get().asItem());
    }
}
