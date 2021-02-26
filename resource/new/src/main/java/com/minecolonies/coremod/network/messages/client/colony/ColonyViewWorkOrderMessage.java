package com.minecolonies.coremod.network.messages.client.colony;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewWorkOrderMessage implements IMessage
{
    private int                colonyId;
    private RegistryKey<World> dimension;
    private PacketBuffer       workOrderBuffer;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewWorkOrderMessage()
    {
        super();
    }

    /**
     * Updates a {@link WorkOrderView} of the workOrders.
     *
     * @param colony        colony of the workOrder.
     * @param workOrderList list of workorders to send to the client
     */
    public ColonyViewWorkOrderMessage(@NotNull final Colony colony, @NotNull final List<IWorkOrder> workOrderList)
    {
        this.colonyId = colony.getID();
        this.workOrderBuffer = new PacketBuffer(Unpooled.buffer());
        this.dimension = colony.getDimension();

        workOrderBuffer.writeInt(workOrderList.size());
        for (final IWorkOrder workOrder : workOrderList)
        {
            workOrder.serializeViewNetworkData(workOrderBuffer);
        }
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        final PacketBuffer newbuf = new PacketBuffer(buf.retain());
        colonyId = newbuf.readInt();
        dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(newbuf.readString(32767)));
        workOrderBuffer = newbuf;
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeString(dimension.getLocation().toString());
        buf.writeBytes(workOrderBuffer);
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
        IColonyManager.getInstance().handleColonyViewWorkOrderMessage(colonyId, workOrderBuffer, dimension);
        workOrderBuffer.release();
    }
}
