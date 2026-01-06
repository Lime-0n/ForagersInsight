package com.tiomadre.foragersinsight.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class SuspiciousLitterParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public SuspiciousLitterParticle(ClientLevel level, double x, double y, double z,
                                    double velocityX, double velocityY, double velocityZ,
                                    SpriteSet sprite) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = sprite;
        this.gravity = 0.0F;
        this.friction = 0.8F;
        this.lifetime = 20 + level.random.nextInt(10);
        this.quadSize = 0.12F;
        this.setSpriteFromAge(spriteSet);
        this.yd += 0.01D;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
