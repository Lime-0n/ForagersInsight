package com.tiomadre.foragersinsight.data.client;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
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
        return CompletableFuture.allOf(
                saveParticle(output, FIParticleTypes.DRIPPING_SAP.getId()),
                saveParticle(output, FIParticleTypes.ROSE_SCENT.getId()),
                saveParticle(output, FIParticleTypes.CONIFEROUS_SCENT.getId()),
                saveParticle(output, FIParticleTypes.FLORAL_SCENT.getId())
        );
    }

    private CompletableFuture<?> saveParticle(CachedOutput output, ResourceLocation id) {
        return saveParticle(output, id, textureLocation(id));
    }

    private CompletableFuture<?> saveParticle(CachedOutput output, ResourceLocation id, String texture) {
        JsonObject json = new JsonObject();
        JsonArray textures = new JsonArray();
        textures.add(texture);
        json.add("textures", textures);

        Path path = particlePathProvider.json(id);
        return DataProvider.saveStable(output, json, path);
    }

    private static String textureLocation(ResourceLocation id) {
        return id.getNamespace() + ":particle/" + id.getPath();
    }

    @Override
    public @NotNull String getName() {
        return ForagersInsight.MOD_ID + " Particles";
    }
}