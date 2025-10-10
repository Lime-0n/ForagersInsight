package com.tiomadre.foragersinsight.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class DiffuserScentParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public DiffuserScentParticle(ClientLevel level, double x, double y, double z,
                                 double velocityX, double velocityY, double velocityZ,
                                 SpriteSet sprite) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = sprite;
        this.gravity = 0.0F;
        this.friction = 0.9F;
        this.hasPhysics = false;
        this.lifetime = 40 + level.random.nextInt(20);
        this.setSize(0.08F, 0.08F);
        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed && this.age % 5 == 0) {
            this.pickSprite(this.spriteSet);
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}