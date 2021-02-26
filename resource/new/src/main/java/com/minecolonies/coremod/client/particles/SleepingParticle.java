package com.minecolonies.coremod.client.particles;

import org.jetbrains.annotations.Nullable;

/**
 * Custom particle for sleeping.
 */
public class SleepingParticle extends SpriteBillboardParticle
{

    /**
     * Spawn coords
     */
    private final double coordX;
    private final double coordY;
    private final double coordZ;

    /**
     * The light level of the particle
     */
    private static final int LIGHT_LEVEL = 15 << 20 | 15 << 4;

    /**
     * The resourcelocation for the sleeping image.
     */
    public static final Identifier SLEEPING_TEXTURE = new ModIdentifier("particle/sleeping");

    public SleepingParticle(SpriteProvider spriteSet, ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);

        //this.setSprite(Minecraft.getInstance().getTextureMap().getSprite(SLEEPING_TEXTURE));
        selectSpriteWithAge(spriteSet);
        this.velocityX = xSpeedIn * 0.5;
        this.velocityY = ySpeedIn;
        this.velocityZ = zSpeedIn;
        this.coordX = xCoordIn;
        this.coordY = yCoordIn;
        this.coordZ = zCoordIn;
        this.prevPosX = xCoordIn;
        this.prevPosY = yCoordIn;
        this.prevPosZ = zCoordIn;
        this.posX = this.prevPosX;
        this.posY = this.prevPosY;
        this.posZ = this.prevPosZ;
        // Slight color variance
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.9F * f;
        this.particleGreen = 0.9F * f;
        this.particleBlue = f;
        // particles max age in ticks, random causes them to appear a bit more dynamic, as they get faster/slower with shorter/longer lifetime
        this.maxAge = (int) (Math.random() * 30.0D) + 40;
        // starting scale to fit
        this.particleScale = (float) ((0.8 * Math.sin(0) + 1.3) * 0.1);
    }


    @Override public ParticleTextureSheet getType()
    {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Nullable @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
        {
            return new SleepingParticle(spriteSet, world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
