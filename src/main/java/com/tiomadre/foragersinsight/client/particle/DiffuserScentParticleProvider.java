package com.tiomadre.foragersinsight.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class DiffuserScentParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprite;

    public DiffuserScentParticleProvider(SpriteSet sprite) {
        this.sprite = sprite;
    }

    @Override
    public TextureSheetParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                               double x, double y, double z,
                                               double velocityX, double velocityY, double velocityZ) {
        return new DiffuserScentParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.sprite);
    }
}