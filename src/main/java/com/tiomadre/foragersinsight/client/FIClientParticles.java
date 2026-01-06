package com.tiomadre.foragersinsight.client;

import com.tiomadre.foragersinsight.client.particle.DiffuserScentParticleProvider;
import com.tiomadre.foragersinsight.client.particle.SapDripParticleProvider;
import com.tiomadre.foragersinsight.client.particle.SuspiciousLitterParticleProvider;
import com.tiomadre.foragersinsight.core.registry.FIParticleTypes;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ForagersInsight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FIClientParticles {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FIParticleTypes.DRIPPING_SAP.get(), SapDripParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.DRIPPING_SYRUP.get(), SapDripParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.CONIFEROUS_SCENT.get(), DiffuserScentParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.ROSE_SCENT.get(), DiffuserScentParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.FLORAL_SCENT.get(), DiffuserScentParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.FOUL_SCENT.get(), DiffuserScentParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.SUSPICIOUS_LEAVES.get(), SuspiciousLitterParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.SUSPICIOUS_NEEDLES.get(), SuspiciousLitterParticleProvider::new);
        event.registerSpriteSet(FIParticleTypes.SUSPICIOUS_FLOWER.get(), SuspiciousLitterParticleProvider::new);
    }
}