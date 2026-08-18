package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FIParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ForagersInsight.MOD_ID);
    //tapper drip particles
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_SAP =
            PARTICLES.register("dripping_sap", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_SYRUP =
            PARTICLES.register("dripping_syrup", () -> new SimpleParticleType(false));

    //diffuser scent particles
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CONIFEROUS_SCENT =
            PARTICLES.register("coniferous_scent", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ROSE_SCENT =
            PARTICLES.register("rose_scent", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLORAL_SCENT =
            PARTICLES.register("floral_scent", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLORAL_II_SCENT =
            PARTICLES.register("floral_ii_scent", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOUL_SCENT =
            PARTICLES.register("foul_scent", () -> new SimpleParticleType(false));

    //suspicious leaf litter brush particles
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUSPICIOUS_LEAVES =
            PARTICLES.register("suspicious_leaves", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUSPICIOUS_NEEDLES =
            PARTICLES.register("suspicious_needles", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUSPICIOUS_FLOWER =
            PARTICLES.register("suspicious_flower", () -> new SimpleParticleType(false));

    //other
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GHOST_PIPE =
            PARTICLES.register("ghost_pipe", () -> new SimpleParticleType(false));

    private FIParticleTypes() {
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }
}