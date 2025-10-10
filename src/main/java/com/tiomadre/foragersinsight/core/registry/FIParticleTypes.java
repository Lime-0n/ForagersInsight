package com.tiomadre.foragersinsight.core.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FIParticleTypes {
    public static final String MODID = "foragersinsight";

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<SimpleParticleType> DRIPPING_SAP =
            PARTICLES.register("dripping_sap", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CONIFEROUS_SCENT =
            PARTICLES.register("coniferous_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ROSE_SCENT =
            PARTICLES.register("rosey_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLORAL_SCENT =
            PARTICLES.register("floral_scent", () -> new SimpleParticleType(false));

    private FIParticleTypes() {}

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}
