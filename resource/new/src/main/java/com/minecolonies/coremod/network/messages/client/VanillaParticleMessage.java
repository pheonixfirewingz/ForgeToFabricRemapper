package com.minecolonies.coremod.network.messages.client;

import org.jetbrains.annotations.Nullable;

import java.util.Random;


/**
 * Message for vanilla particles around a citizen, in villager-like shape.
 */
public class VanillaParticleMessage implements IMessage
{
    /**
     * Citizen Position
     */
    private double x;
    private double y;
    private double z;

    /**
     * Particle id
     */
    private BasicParticleType type;

    public VanillaParticleMessage() {super();}

    public VanillaParticleMessage(final double x, final double y, final double z, final BasicParticleType type)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    @Override
    public void fromBytes(final PacketBuffer byteBuf)
    {
        x = byteBuf.readDouble();
        y = byteBuf.readDouble();
        z = byteBuf.readDouble();
        this.type = (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(byteBuf.readResourceLocation());
    }

    @Override
    public void toBytes(final PacketBuffer byteBuf)
    {
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        byteBuf.writeResourceLocation(this.type.getRegistryName());
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.CLIENT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ClientWorld world = Minecraft.getInstance().world;

        spawnParticles(type, world, x, y, z);
    }

    /**
     * Spawns the given particle randomly around the position.
     *
     * @param particleType praticle to spawn
     * @param world        world to use
     * @param x            x pos
     * @param y            y pos
     * @param z            z pos
     */
    private void spawnParticles(BasicParticleType particleType, World world, double x, double y, double z)
    {
        final Random rand = new Random();
        for (int i = 0; i < 5; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            world.addParticle(particleType,
              x + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              y + 1.0D + (rand.nextFloat() * CITIZEN_HEIGHT),
              z + (rand.nextFloat() * CITIZEN_WIDTH * 2.0F) - CITIZEN_WIDTH,
              d0,
              d1,
              d2);
        }
    }
}
