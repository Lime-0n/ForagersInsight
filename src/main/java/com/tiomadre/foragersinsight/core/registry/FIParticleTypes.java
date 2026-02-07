package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FIParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ForagersInsight.MOD_ID);


    //tapper drip particles
    public static final RegistryObject<SimpleParticleType> DRIPPING_SAP =
            PARTICLES.register("dripping_sap", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DRIPPING_SYRUP =
            PARTICLES.register("dripping_syrup", () -> new SimpleParticleType(false));

    //diffuser scent particles
    public static final RegistryObject<SimpleParticleType> CONIFEROUS_SCENT =
            PARTICLES.register("coniferous_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ROSE_SCENT =
            PARTICLES.register("rose_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLORAL_SCENT =
            PARTICLES.register("floral_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLORAL_II_SCENT =
            PARTICLES.register("floral_II_scent", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FOUL_SCENT =
            PARTICLES.register("foul_scent", () -> new SimpleParticleType(false));

    //suspicious leaf litter brush particles
    public static final RegistryObject<SimpleParticleType> SUSPICIOUS_LEAVES =
            PARTICLES.register("suspicious_leaves", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SUSPICIOUS_NEEDLES =
            PARTICLES.register("suspicious_needles", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SUSPICIOUS_FLOWER =
            PARTICLES.register("suspicious_flower", () -> new SimpleParticleType(false));

    private FIParticleTypes() {
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}