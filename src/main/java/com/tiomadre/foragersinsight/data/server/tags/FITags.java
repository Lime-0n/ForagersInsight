package com.tiomadre.foragersinsight.data.server.tags;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class FITags {
    public static class ItemTag {
        public static final TagKey<Item> ICE = TagUtil.itemTag("forge", "ice");
        public static final TagKey<Item> SEEDS = TagUtil.itemTag("forge", "seeds");
        public static final TagKey<Item> NUTS = TagUtil.itemTag("forge", "nuts");
        public static final TagKey<Item> NUTS_ACORN = TagUtil.itemTag("forge", "nuts/acorn");
        public static final TagKey<Item> CROPS = TagUtil.itemTag("forge", "crops");
        public static final TagKey<Item> RAW_MEATS = TagUtil.itemTag("forge", "raw_meats");
        public static final TagKey<Item> COOKED_MEATS = TagUtil.itemTag("forge", "cooked_meats");
        public static final TagKey<Item> RAW_FISHES = TagUtil.itemTag("forge", "raw_fishes");
        public static final TagKey<Item> COOKED_FISHES = TagUtil.itemTag("forge", "cooked_fishes");
        public static final TagKey<Item> FRUITS = TagUtil.itemTag("forge", "fruits");
        public static final TagKey<Item> VEGETABLES = TagUtil.itemTag("forge", "vegetables");
        public static final TagKey<Item> FEATHERS = TagUtil.itemTag("forge", "feathers");
        public static final TagKey<Item> LEATHER = TagUtil.itemTag("forge", "leather");
        public static final TagKey<Item> EGGS = TagUtil.itemTag("forge", "eggs");
        public static final TagKey<Item> FLOUR = TagUtil.itemTag("forge", "flour");
        public static final TagKey<Item> STRAW = TagUtil.itemTag("forge", "straw");
        public static final TagKey<Item> TREE_BARK = TagUtil.itemTag("forge", "tree_bark");
        public static final TagKey<Item> SUGAR = TagUtil.itemTag("forge", "sugar");
        //Mallet
        public static final TagKey<net.minecraft.world.item.Item> MALLETS = TagUtil.itemTag("forge", "tools/mallets");
        //Milk
        public static final TagKey<net.minecraft.world.item.Item> MILK_BUCKET = TagUtil.itemTag("forge", "milk/milk");
        public static final TagKey<net.minecraft.world.item.Item> MILK_BOTTLE = TagUtil.itemTag("forge", "milk/milk_bottle");
        //Crops
        public static final TagKey<net.minecraft.world.item.Item> APPLE = TagUtil.itemTag("forge", "crops/apple");
        public static final TagKey<net.minecraft.world.item.Item> POPPY_SEEDS = TagUtil.itemTag("forge", "crops/poppy_seeds");
        public static final TagKey<net.minecraft.world.item.Item> ACORN = TagUtil.itemTag("forge", "crops/acorn");
        public static final TagKey<net.minecraft.world.item.Item> WHEAT = TagUtil.itemTag("forge", "crops/wheat");
        public static final TagKey<net.minecraft.world.item.Item> COCOA = TagUtil.itemTag("forge", "crops/cocoa");
        public static final TagKey<net.minecraft.world.item.Item> ROOTS = TagUtil.itemTag("forge", "crops/root_vegetable");
        public static final TagKey<net.minecraft.world.item.Item> MUSHROOM = TagUtil.itemTag("forge", "crops/mushroom");
        //Diffuser
        public static final TagKey<Item> AROMATICS = TagKey.create(Registries.ITEM,
                new ResourceLocation(ForagersInsight.MOD_ID, "aromatics"));
        //Handbasket
        public static final TagKey<Item> HANDBASKET_ALLOWED = TagKey.create(Registries.ITEM, new ResourceLocation("foragersinsight","handbasket_allowed"));
        public static final TagKey<Item> HANDBASKET_SPECIALTY = TagKey.create(Registries.ITEM, new ResourceLocation(ForagersInsight.MOD_ID, "handbasket/specialty"));

        public static final TagKey<Item> STORAGE_BLOCK_ROSE_HIP = storageTag("rose_hip");
        public static final TagKey<Item> STORAGE_BLOCK_SPRUCE_TIPS = storageTag("spruce_tips");
        public static final TagKey<Item> STORAGE_BLOCK_DANDELION_ROOT = storageTag("dandelion_root");
        public static final TagKey<Item> STORAGE_BLOCK_POPPY_SEEDS = storageTag("poppy_seeds");
        public static final TagKey<Item> STORAGE_BLOCK_BLACK_ACORNS = storageTag("black_acorns");

        public static TagKey<Item> storageTag(String thing) {
            return TagUtil.itemTag("forge", "storage_blocks/" + thing);
        }
    }

    public static class BlockTag {
        public static final TagKey<Block> STORAGE_BLOCK_APPLE = storageTag("apple");
        public static final TagKey<Block> STORAGE_BLOCK_ROSE_HIP = storageTag("rose_hip");
        public static final TagKey<Block> STORAGE_BLOCK_SPRUCE_TIPS = storageTag("spruce_tips");
        public static final TagKey<Block> STORAGE_BLOCK_DANDELION_ROOT = storageTag("dandelion_root");
        public static final TagKey<Block> STORAGE_BLOCK_POPPY_SEEDS = storageTag("poppy_seeds");
        public static final TagKey<Block> STORAGE_BLOCK_BLACK_ACORNS = storageTag("black_acorns");

        public static final TagKey<Block> FORAGING = blockTag("foraging");

        public static TagKey<Block> blockTag(String namespace) {
            return TagUtil.blockTag(ForagersInsight.MOD_ID, namespace);
        }

        public static TagKey<Block> storageTag(String thing) {
            return TagUtil.blockTag("forge", "storage_blocks/" + thing);
        }
    }

    public static class BiomeTag {
        public static final TagKey<Biome> HAS_APPLE_TREES = hasFeature("apple_trees");
        public static final TagKey<Biome> HAS_ACORN_TREES = hasFeature("acorn_trees");
        public static final TagKey<Biome> HAS_SPRUCE_TIP_TREES = hasFeature("spruce_tip_trees");
        public static final TagKey<Biome> HAS_SAPPY_BIRCH_TREES = hasFeature("sappy_birch_trees");
        public static final TagKey<Biome> HAS_ROSELLE_BUSHES = hasFeature("roselle_bushes");
        public static final TagKey<Biome> HAS_BEACH_ROSES = hasFeature("beach_roses");

        private static TagKey<Biome> modTag(String namespace) {
            return TagUtil.biomeTag(ForagersInsight.MOD_ID, namespace);
        }
        private static TagKey<Biome> hasFeature(String feature) {
            return modTag("has_feature/" + feature);
        }

    }
}
