package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.block.SuspiciousLitterBlock;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.BiConsumer;

import static com.tiomadre.foragersinsight.core.registry.FIItems.*;

public final class FIForageLoot {

    public static final ResourceKey<LootTable> SUSPICIOUS_LEAF_LITTER_OAK = ResourceKey.create(Registries.LOOT_TABLE,ForagersInsight.rl("archaeology/suspicious_leaf_litter/oak"));
    public static final ResourceKey<LootTable> SUSPICIOUS_LEAF_LITTER_BIRCH = ResourceKey.create(Registries.LOOT_TABLE,ForagersInsight.rl("archaeology/suspicious_leaf_litter/birch"));
    public static final ResourceKey<LootTable> SUSPICIOUS_LEAF_LITTER_SPRUCE = ResourceKey.create(Registries.LOOT_TABLE,ForagersInsight.rl("archaeology/suspicious_leaf_litter/spruce"));
    public static final ResourceKey<LootTable> SUSPICIOUS_LEAF_LITTER_DARK_OAK = ResourceKey.create(Registries.LOOT_TABLE,ForagersInsight.rl("archaeology/suspicious_leaf_litter/dark_oak"));
    public static final ResourceKey<LootTable> SUSPICIOUS_LEAF_LITTER_FLOWER = ResourceKey.create(Registries.LOOT_TABLE,ForagersInsight.rl("archaeology/suspicious_leaf_litter/flower"));

    private FIForageLoot() {
    }

    public static ResourceLocation suspiciousLeafLitterByFoliage(SuspiciousLitterBlock.FoliageType foliageType) {
        return switch (foliageType) {
            case BIRCH -> ForagersInsight.rl("archaeology/suspicious_leaf_litter/birch");
            case SPRUCE -> ForagersInsight.rl("archaeology/suspicious_leaf_litter/spruce");
            case DARK_OAK -> ForagersInsight.rl("archaeology/suspicious_leaf_litter/dark_oak");
            case FLOWER -> ForagersInsight.rl("archaeology/suspicious_leaf_litter/flower");
            case OAK -> ForagersInsight.rl("archaeology/suspicious_leaf_litter/oak");
        };
    }


    public static class FIForagingFinds implements LootTableSubProvider {

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            output.accept(FIForageLoot.SUSPICIOUS_LEAF_LITTER_OAK, suspiciousLitterOak());
            output.accept(FIForageLoot.SUSPICIOUS_LEAF_LITTER_BIRCH, suspiciousLitterBirch());
            output.accept(FIForageLoot.SUSPICIOUS_LEAF_LITTER_SPRUCE, suspiciousLitterSpruce());
            output.accept(FIForageLoot.SUSPICIOUS_LEAF_LITTER_DARK_OAK, suspiciousLitterDarkOak());
            output.accept(FIForageLoot.SUSPICIOUS_LEAF_LITTER_FLOWER, suspiciousLitterFlower());

        }

        private static LootTable.Builder suspiciousLitterOak() {
            return baseSuspiciousLitterBuilder()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.APPLE).setWeight(7).setQuality(-1))
                            .add(LootItem.lootTableItem(ROSE_HIP.get()).setWeight(6))
                            .add(LootItem.lootTableItem(Items.EGG).setWeight(4).setQuality(1)));
        }

        private static LootTable.Builder suspiciousLitterFlower() {
            return baseSuspiciousLitterBuilder()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(LILAC_BLOOM.get()).setWeight(7).setQuality(-1))
                            .add(LootItem.lootTableItem(ROSE_HIP.get()).setWeight(6))
                            .add(LootItem.lootTableItem(Items.HONEYCOMB).setWeight(5).setQuality(1)));
        }

        private static LootTable.Builder suspiciousLitterBirch() {
            return baseSuspiciousLitterBuilder()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ROSE_HIP.get()).setWeight(6))
                            .add(LootItem.lootTableItem(FIBlocks.TINDER_CONK.get()).setWeight(4))
                    );
        }

        private static LootTable.Builder suspiciousLitterSpruce() {
            return baseSuspiciousLitterBuilder()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.FERN).setWeight(8).setQuality(-1))
                            .add(LootItem.lootTableItem(SPRUCE_TIPS.get()).setWeight(7))
                            .add(LootItem.lootTableItem(Items.SWEET_BERRIES).setWeight(6)));
        }

        private static LootTable.Builder suspiciousLitterDarkOak() {
            return baseSuspiciousLitterBuilder()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(BLACK_ACORN.get()).setWeight(7))
                            .add(LootItem.lootTableItem(ROSE_HIP.get()).setWeight(6))
                            .add(LootItem.lootTableItem(Items.STRING).setWeight(5).setQuality(1)));
        }

        private static LootTable.Builder baseSuspiciousLitterBuilder() {
            return LootTable.lootTable()
                    .setParamSet(LootContextParamSets.ARCHAEOLOGY)
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(Items.STICK).setWeight(8).setQuality(-1))
                            .add(LootItem.lootTableItem(vectorwing.farmersdelight.common.registry.ModItems.TREE_BARK.get()).setWeight(7))
                            .add(LootItem.lootTableItem(Items.FEATHER).setWeight(6))
                            .add(LootItem.lootTableItem(DANDELION_ROOT.get()).setWeight(6))
                            .add(LootItem.lootTableItem(POPPY_SEEDS.get()).setWeight(6))
                            .add(LootItem.lootTableItem(Items.RED_MUSHROOM).setWeight(5).setQuality(1))
                            .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM).setWeight(5).setQuality(1))
                            .add(LootItem.lootTableItem(BLEWIT_MUSHROOM.get()).setWeight(2).setQuality(2)));
        }


    }
}
