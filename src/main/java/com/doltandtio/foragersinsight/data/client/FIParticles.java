package com.doltandtio.foragersinsight.data.client;

import com.doltandtio.foragersinsight.core.ForagersInsight;
import com.doltandtio.foragersinsight.core.registry.FIParticleTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FIParticles implements DataProvider {
    private final PackOutput.PathProvider particlePathProvider;

    public FIParticles(GatherDataEvent event) {
        this.particlePathProvider = event.getGenerator().getPackOutput()
                .createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        JsonObject json = new JsonObject();
        JsonArray textures = new JsonArray();
        textures.add(ForagersInsight.MOD_ID + ":" + FIParticleTypes.DRIPPING_SAP.getId().getPath());
        json.add("textures", textures);

        Path path = particlePathProvider.json(ResourceLocation.tryParse(FIParticleTypes.DRIPPING_SAP.getId().getPath()));
        return DataProvider.saveStable(output, json, path);
    }

    @Override
    public @NotNull String getName() {
        return ForagersInsight.MOD_ID + " Particles";
    }
}