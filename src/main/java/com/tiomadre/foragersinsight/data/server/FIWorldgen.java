package com.tiomadre.foragersinsight.data.server;

import com.tiomadre.foragersinsight.common.worldgen.FIBiomeModifiers;
import com.tiomadre.foragersinsight.common.worldgen.FIBiomes;
import com.tiomadre.foragersinsight.common.worldgen.FIConfiguredFeatures;
import com.tiomadre.foragersinsight.common.worldgen.FIPlacedFeatures;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


import java.util.Set;

public class FIWorldgen extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, FIBiomes::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, FIConfiguredFeatures::bootstap)
            .add(Registries.PLACED_FEATURE, FIPlacedFeatures::bootstap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FIBiomeModifiers::bootstap);


    public FIWorldgen(GatherDataEvent e) {
        super(e.getGenerator().getPackOutput(), e.getLookupProvider(), BUILDER, Set.of(ForagersInsight.MOD_ID));
    }
}