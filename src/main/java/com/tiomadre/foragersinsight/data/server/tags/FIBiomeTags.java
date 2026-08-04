package com.tiomadre.foragersinsight.data.server.tags;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public class FIBiomeTags extends BiomeTagsProvider {
    public FIBiomeTags(@NotNull GatherDataEvent e) {
        super(e.getGenerator().getPackOutput(), e.getLookupProvider());
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        //Flora
            //Trees
        this.tag(FITags.BiomeTag.HAS_APPLE_TREES).add(Biomes.FOREST, Biomes.FLOWER_FOREST);
        this.tag(FITags.BiomeTag.HAS_ACORN_TREES).add(Biomes.DARK_FOREST);
        this.tag(FITags.BiomeTag.HAS_LILAC_TREES).add(Biomes.FLOWER_FOREST);
        this.tag(FITags.BiomeTag.HAS_SPRUCE_TIP_TREES).add(Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA,Biomes.SNOWY_TAIGA);
        this.tag(FITags.BiomeTag.HAS_SAPPY_BIRCH_TREES).add(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST,Biomes.FOREST);
            //Plants
        this.tag(FITags.BiomeTag.HAS_ROSELLE_BUSHES).add(Biomes.SAVANNA,Biomes.SAVANNA_PLATEAU,Biomes.WINDSWEPT_SAVANNA);
        this.tag(FITags.BiomeTag.HAS_BEACH_ROSES).add(Biomes.BEACH);
        this.tag(FITags.BiomeTag.HAS_WOODLAND_FERNS).add(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.BIRCH_FOREST,Biomes.OLD_GROWTH_BIRCH_FOREST)
        .addOptional(ForagersInsight.rl("dark_woodlands"));

        //Biome Features
            //Suspicious Litter + Fallen Logs
        this.tag(FITags.BiomeTag.HAS_OAK_FOREST_LITTER).add(Biomes.FOREST);
        this.tag(FITags.BiomeTag.HAS_BIRCH_FOREST_LITTER).add(Biomes.BIRCH_FOREST,Biomes.OLD_GROWTH_BIRCH_FOREST);
        this.tag(FITags.BiomeTag.HAS_SPRUCE_FOREST_LITTER).add(Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        this.tag(FITags.BiomeTag.HAS_DARK_OAK_FOREST_LITTER).add(Biomes.DARK_FOREST).addOptional(ForagersInsight.rl("dark_woodlands"));
        this.tag(FITags.BiomeTag.HAS_FLOWER_FOREST_LITTER).add(Biomes.FLOWER_FOREST);
            //Puddles
        this.tag(FITags.BiomeTag.HAS_PUDDLES).add(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.BIRCH_FOREST,Biomes.OLD_GROWTH_BIRCH_FOREST,Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA
        ,Biomes.FLOWER_FOREST)
        .addOptional(ForagersInsight.rl("dark_woodlands"));


    }
}
