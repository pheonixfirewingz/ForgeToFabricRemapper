package com.minecolonies.coremod.event.capabilityproviders;


import javax.annotation.Nonnull;


/**
 * Capability provider for the world capability of Minecolonies.
 */
public class MinecoloniesWorldColonyManagerCapabilityProvider implements ICapabilitySerializable<INBT>
{
    /**
     * The chunk map capability optional.
     */
    private final LazyOptional<IColonyManagerCapability> colonyManagerOptional;

    /**
     * The chunk map capability.
     */
    private final IColonyManagerCapability colonyManager;

    /**
     * Constructor of the provider.
     */
    public MinecoloniesWorldColonyManagerCapabilityProvider()
    {
        this.colonyManager = new IColonyManagerCapability.Impl();
        this.colonyManagerOptional = LazyOptional.of(() -> colonyManager);
    }

    @Override
    public INBT serializeNBT()
    {
        return COLONY_MANAGER_CAP.getStorage().writeNBT(COLONY_MANAGER_CAP, colonyManager, null);
    }

    @Override
    public void deserializeNBT(final INBT nbt)
    {
        COLONY_MANAGER_CAP.getStorage().readNBT(COLONY_MANAGER_CAP, colonyManager, null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final Direction dir)
    {
        return cap == COLONY_MANAGER_CAP ? colonyManagerOptional.cast() : LazyOptional.empty();
    }
}
