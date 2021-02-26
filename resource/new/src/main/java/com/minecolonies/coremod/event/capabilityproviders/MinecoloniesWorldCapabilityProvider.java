package com.minecolonies.coremod.event.capabilityproviders;


import javax.annotation.Nonnull;


/**
 * Capability provider for the world capability of Minecolonies.
 */
public class MinecoloniesWorldCapabilityProvider implements ICapabilitySerializable<INBT>
{
    /**
     * The chunk map capability.
     */
    private final IChunkmanagerCapability chunkMap;

    /**
     * The chunk map capability optional.
     */
    private final LazyOptional<IChunkmanagerCapability> chunkMapOptional;

    /**
     * Constructor of the provider.
     */
    public MinecoloniesWorldCapabilityProvider()
    {
        this.chunkMap = new IChunkmanagerCapability.Impl();
        this.chunkMapOptional = LazyOptional.of(() -> chunkMap);
    }

    @Override
    public INBT serializeNBT()
    {
        return CHUNK_STORAGE_UPDATE_CAP.getStorage().writeNBT(CHUNK_STORAGE_UPDATE_CAP, chunkMap, null);
    }

    @Override
    public void deserializeNBT(final INBT nbt)
    {
        CHUNK_STORAGE_UPDATE_CAP.getStorage().readNBT(CHUNK_STORAGE_UPDATE_CAP, chunkMap, null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final Direction direction)
    {
        return cap == CHUNK_STORAGE_UPDATE_CAP ? chunkMapOptional.cast() : LazyOptional.empty();
    }
}
