package com.tiomadre.foragersinsight.data.server.tags;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.tag.ModTags;

import static com.tiomadre.foragersinsight.core.registry.FIBlocks.*;
import static com.tiomadre.foragersinsight.data.server.tags.FITags.BlockTag.*;

public class FIBlockTags extends BlockTagsProvider {

    public FIBlockTags(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), ForagersInsight.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.LOGS).add(SAPPY_BIRCH_LOG.get());
        this.tag(BlockTags.BIRCH_LOGS).add(SAPPY_BIRCH_LOG.get());
        this.tag(BlockTags.SAPLINGS).add(BOUNTIFUL_OAK_SAPLING.get(), BOUNTIFUL_DARK_OAK_SAPLING.get(), BOUNTIFUL_SPRUCE_SAPLING.get());
        this.tag(BlockTags.LEAVES).add(BOUNTIFUL_OAK_LEAVES.get(),BOUNTIFUL_DARK_OAK_LEAVES.get(),BOUNTIFUL_SPRUCE_LEAVES.get());
        this.tag(BlockTags.CROPS).add(ROSE_CROP.get(), DANDELION_BUSH.get(), POPPY_BUSH.get(),BOUNTIFUL_DARK_OAK_LEAVES.get()
        ,BOUNTIFUL_OAK_LEAVES.get(),BOUNTIFUL_SPRUCE_TIPS.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(DANDELION_BUSH.get(), POPPY_BUSH.get(),ROSE_CROP.get(),BOUNTIFUL_DARK_OAK_LEAVES.get(),BOUNTIFUL_OAK_LEAVES.get()
        ,BOUNTIFUL_SPRUCE_TIPS.get(),ROSE_CROP.get());
        this.tag(FORAGING)
        //FD Wild Crops
        .add(ModBlocks.WILD_BEETROOTS.get(),ModBlocks.WILD_CABBAGES.get(),ModBlocks.WILD_CARROTS.get(),
        ModBlocks.WILD_ONIONS.get(),ModBlocks.WILD_POTATOES.get(),ModBlocks.WILD_TOMATOES.get(),ModBlocks.WILD_RICE.get(),
        //Grasses
        ModBlocks.SANDY_SHRUB.get(),Blocks.TALL_GRASS,Blocks.GRASS,
        //Vanilla & Forager Wild Flowers
        Blocks.ROSE_BUSH,Blocks.DANDELION,Blocks.POPPY,STOUT_BEACH_ROSE_BUSH.get(), TALL_BEACH_ROSE_BUSH.get(),ROSELLE_BUSH.get());
        registerForgeTags();
        registerMineables();
    }

    protected void registerMineables() {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(TAPPER.get(), DIFFUSER.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(APPLE_CRATE.get(),(BOUNTIFUL_OAK_SAPLING.get()), BOUNTIFUL_DARK_OAK_SAPLING.get(), SAPPY_BIRCH_LOG.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(BOUNTIFUL_OAK_LEAVES.get(),BOUNTIFUL_DARK_OAK_LEAVES.get(),BOUNTIFUL_SPRUCE_LEAVES.get());
        this.tag(ModTags.MINEABLE_WITH_KNIFE).add(DANDELION_ROOT_SACK.get(), ROSE_HIP_SACK.get(), POPPY_SEEDS_SACK.get(), SPRUCE_TIPS_SACK.get(), BLACK_ACORN_SACK.get());
    }


    protected void registerForgeTags() {
        tag(STORAGE_BLOCK_ROSE_HIP).add(ROSE_HIP_SACK.get());
        tag(STORAGE_BLOCK_POPPY_SEEDS).add(POPPY_SEEDS_SACK.get());
        tag(STORAGE_BLOCK_APPLE).add(Block.byItem(Items.APPLE));
        tag(STORAGE_BLOCK_DANDELION_ROOT).add(DANDELION_ROOT_SACK.get());
        tag(STORAGE_BLOCK_SPRUCE_TIPS).add(SPRUCE_TIPS_SACK.get());
        tag(STORAGE_BLOCK_BLACK_ACORNS).add(BLACK_ACORN_SACK.get());
    }


}