package com.minecolonies.coremod.network.messages.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Update the ChunkCapability with a colony.
 */
public class UpdateChunkCapabilityMessage implements IMessage
{
    /**
     * The chunk cap data
     */
    private ChunkCapData chunkCapData;

    /**
     * Empty constructor used when registering the
     */
    public UpdateChunkCapabilityMessage()
    {
        super();
    }

    /**
     * Create a message to update the chunk cap on the client side.
     *
     * @param tagCapability the cap.
     * @param x             the x pos.
     * @param z             the z pos.
     */
    public UpdateChunkCapabilityMessage(@NotNull final IColonyTagCapability tagCapability, final int x, final int z)
    {
        chunkCapData = new ChunkCapData(x, z, tagCapability.getOwningColony(), tagCapability.getAllCloseColonies());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        chunkCapData = ChunkCapData.fromBytes(buf);
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        chunkCapData.toBytes(buf);
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.CLIENT;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ClientWorld world = Minecraft.getInstance().world;

        if (!WorldUtil.isChunkLoaded(world, new ChunkPos(chunkCapData.x, chunkCapData.z)))
        {
            ChunkClientDataHelper.addCapData(chunkCapData);
            return;
        }

        final Chunk chunk = world.getChunk(chunkCapData.x, chunkCapData.z);
        final IColonyTagCapability cap = chunk.getCapability(CLOSE_COLONY_CAP, null).orElseGet(null);

        if (cap != null && cap.getOwningColony() != chunkCapData.owningColony)
        {
            ChunkClientDataHelper.applyCap(chunkCapData, chunk);
        }
    }
}
